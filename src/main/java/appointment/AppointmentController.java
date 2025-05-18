package appointment;

import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Appointment Management", description = "Operations related to appointment management")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private InstitutionService institutionService;

    @Operation(summary = "Get all appointments", description = "Retrieve a list of all appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @Operation(summary = "Get appointment by ID", description = "Retrieve a single appointment by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(
            @Parameter(description = "ID of the appointment to be retrieved", required = true)
            @PathVariable int id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new appointment", description = "Create a new appointment in the system")
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        try {
            Institution institution = institutionService.getInstitutionById(appointment.getInstitution().getId()).orElse(null);
            if (institution == null) {
                logger.error("Invalid institution ID provided: {}", appointment.getInstitution().getId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            appointment.setInstitution(institution);

            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            if (createdAppointment == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Log virtual meeting link if applicable
            if (createdAppointment.getMeetingType() == MeetingType.VIRTUAL) {
                logger.info("Virtual meeting link generated for appointment ID {}: {}",
                        createdAppointment.getAppointmentid(), createdAppointment.getVideoMeetingLink());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (Exception e) {
            logger.error("Error creating appointment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByVisitorId(@PathVariable int visitorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByVisitorId(visitorId);
        if (appointments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(appointments);
    }
    

    @Operation(summary = "Update an existing appointment", description = "Update an existing appointment by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @Parameter(description = "ID of the appointment to be updated", required = true)
            @PathVariable int id,
            @Parameter(description = "Updated appointment object", required = true)
            @RequestBody Appointment newAppointment) {
        try {
            // Validate institution
            Institution institution = institutionService.getInstitutionById(newAppointment.getInstitution().getId()).orElse(null);
            if (institution == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            newAppointment.setInstitution(institution);

            Appointment updatedAppointment = appointmentService.updateAppointment(id, newAppointment);
            if (updatedAppointment != null) {
                return ResponseEntity.ok(updatedAppointment);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @Operation(summary = "Delete an appointment", description = "Delete an appointment by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @Parameter(description = "ID of the appointment to be deleted", required = true)
            @PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancel an appointment", description = "Cancel an appointment by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @Parameter(description = "ID of the appointment to be canceled", required = true)
            @PathVariable int id) {
        try {
            Appointment canceledAppointment = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(canceledAppointment);
        } catch (AppointmentNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (AppointmentAlreadyCanceledException | AppointmentAlreadyAttendedException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/availableslots")
    public ResponseEntity<List<Time>> getAvailableSlots(
            @RequestParam("date") String dateString,
            @RequestParam("department") String department,
            @RequestParam("institutionId") Long institutionId) {
        try {
            LocalDate date = LocalDate.parse(dateString); // Parse date string to LocalDate
            logger.info("Retrieving available slots for date: {}, department: {}, institutionId: {}", date, department, institutionId);
            
            // Retrieve available slots using service method
            List<Time> availableSlots = appointmentService.getAvailableSlots(date, department, institutionId);
            
            // Return successful response with available slots
            return ResponseEntity.ok(availableSlots);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format provided: {}", dateString);
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid institution ID provided: {}", institutionId);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("An error occurred while retrieving available slots.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Check-in for an appointment", description = "Mark a visitor as checked-in for their appointment")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PutMapping("/{id}/checkin")
    public ResponseEntity<Appointment> checkInAppointment(
            @Parameter(description = "ID of the appointment to be checked-in", required = true)
            @PathVariable int id,
            @Parameter(description = "Passcode to authorize check-in", required = true)
            @RequestHeader("Passcode") String passcode) {
        // Retrieve appointment from the database
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        // Compare passcode with the one stored in the database
        if (!passcode.equals(appointment.getPasscode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Passcode is correct, proceed with check-in
        Appointment checkedInAppointment = appointmentService.checkInAppointment(id);
        if (checkedInAppointment != null) {
            return ResponseEntity.ok(checkedInAppointment);
        }
        return ResponseEntity.badRequest().body(null); // or appropriate response if check-in is not allowed
    }

    @Operation(summary = "Check-out for an appointment", description = "Mark a visitor as checked-out after their appointment")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PutMapping("/{id}/checkout")
    public ResponseEntity<Appointment> checkOutAppointment(
            @Parameter(description = "ID of the appointment to be checked-out", required = true)
            @PathVariable int id,
            @Parameter(description = "Passcode to authorize check-out", required = true)
            @RequestHeader("Passcode") String passcode) {
        // Retrieve appointment from the database
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }

        // Compare passcode with the one stored in the database
        if (!passcode.equals(appointment.getPasscode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Passcode is correct, proceed with check-out
        Appointment checkedOutAppointment = appointmentService.checkOutAppointment(id);
        if (checkedOutAppointment != null) {
            return ResponseEntity.ok(checkedOutAppointment);
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Get appointments by institution ID", description = "Retrieve appointments for a specific institution")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByInstitutionId(
            @Parameter(description = "ID of the institution for which appointments are to be retrieved", required = true)
            @PathVariable Long institutionId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByInstitutionId(institutionId);
        if (!appointments.isEmpty()) {
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/institution/{institutionId}/date/{date}/status/{status}")
    public ResponseEntity<List<Appointment>> getAppointmentsByInstitutionIdAndDateAndStatus(
            @PathVariable Long institutionId, @PathVariable LocalDate date, @PathVariable String status) {
        logger.info("Received request to fetch appointments for institution ID: {}, date: {}, status: {}", institutionId, date, status);
        List<Appointment> appointments = appointmentService.getAppointmentsByInstitutionIdAndDateAndStatus(institutionId, date, status);
        if (!appointments.isEmpty()) {
            return new ResponseEntity<>(appointments, HttpStatus.OK);
        }
        logger.warn("No appointments found for institution ID: {}, date: {}, status: {}", institutionId, date, status);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Get appointments by physiotherapist ID", description = "Retrieve appointments for a specific physiotherapist")
    @PreAuthorize("hasRole('PHYSIOTHERAPIST')")
    @GetMapping("/physio/{physioId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPhysioId(
            @Parameter(description = "ID of the physiotherapist", required = true)
            @PathVariable int physioId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPhysioId(physioId);
        if (!appointments.isEmpty()) {
            return ResponseEntity.ok(appointments);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reschedule/{appointmentId}")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Integer appointmentId,
            @RequestParam("newDate") String newDateStr,
            @RequestParam("newTime") String newTimeStr) {
        try {
            LocalDate newDate = LocalDate.parse(newDateStr);
            Time newTime = Time.valueOf(newTimeStr);

            Appointment updatedAppointment = appointmentService.rescheduleAppointment(appointmentId, newDate, newTime);

            // Log new meeting link if the appointment is virtual
            if (updatedAppointment.getMeetingType() == MeetingType.VIRTUAL) {
                logger.info("New virtual meeting link generated for rescheduled appointment ID {}: {}",
                        updatedAppointment.getAppointmentid(), updatedAppointment.getVideoMeetingLink());
            }

            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error rescheduling appointment: " + e.getMessage());
        }
    }

}
