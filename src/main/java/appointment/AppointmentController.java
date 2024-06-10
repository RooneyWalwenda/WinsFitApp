package appointment;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @Parameter(description = "Appointment object to be created", required = true)
            @RequestBody Appointment appointment) {
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            if (createdAppointment == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update an existing appointment", description = "Update an existing appointment by its ID")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @Parameter(description = "ID of the appointment to be updated", required = true)
            @PathVariable int id,
            @Parameter(description = "Updated appointment object", required = true)
            @RequestBody Appointment newAppointment) {
        Appointment updatedAppointment = appointmentService.updateAppointment(id, newAppointment);
        if (updatedAppointment != null) {
            return ResponseEntity.ok(updatedAppointment);
        }
        return ResponseEntity.notFound().build();
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
    public ResponseEntity<Appointment> cancelAppointment(
            @Parameter(description = "ID of the appointment to be canceled", required = true)
            @PathVariable int id) {
        Appointment canceledAppointment = appointmentService.cancelAppointment(id);
        if (canceledAppointment != null) {
            return ResponseEntity.ok(canceledAppointment);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/availableslots/{dateAndDepartment}")
    public ResponseEntity<List<Time>> getAvailableSlots(
            @PathVariable("dateAndDepartment") String dateAndDepartment) {
        try {
            logger.info("Received request to retrieve available slots for: {}", dateAndDepartment);
            // Extract date and department from the combined string
            String[] parts = dateAndDepartment.split("/");
            Date date = Date.valueOf(parts[0]);
            String department = parts[1];

            logger.info("Retrieving available slots for date: {} and department: {}", date, department);
            List<Time> availableSlots = appointmentService.getAvailableSlots(date, department);
            logger.info("Available slots retrieved successfully.");
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            logger.error("An error occurred while retrieving available slots.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

