package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasscodeGenerator passcodeGenerator;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(int id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        return optionalAppointment.orElse(null);
    }

    public Appointment createAppointment(Appointment appointment) throws Exception {
        logger.info("Creating appointment for visitor: {}", appointment.getVisitor().getVisitorname());

        // Fetch visitor entity from the database using visitorid
        Visitor visitor = visitorRepository.findById(appointment.getVisitor().getVisitorid()).orElse(null);

        if (visitor != null && visitor.getEmail() != null) {
            // Check if the visitor can make a new appointment
            if (!canMakeAppointment(visitor.getVisitorid())) {
                // Visitor has existing appointments that are not completed
                logger.warn("Visitor {} cannot make a new appointment as they have existing appointments not completed.", visitor.getVisitorname());
                return null;
            }

            // Check if the time slot is available
            if (!isSlotAvailable(appointment.getDepartment(), appointment.getDate(), appointment.getTime())) {
                logger.warn("The selected time slot {} is already full on {} for department {}.", appointment.getTime(), appointment.getDate(), appointment.getDepartment());
                throw new Exception("Appointment slot is already full");
            }

            // Generate a unique passcode for the appointment
            String passcode = passcodeGenerator.generateUniquePasscode();
            appointment.setPasscode(passcode);

            // Save the appointment to the database
            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Send appointment details email using visitor's email
            String email = visitor.getEmail();
            String subject = "Your Appointment Details";
            String body = "Dear " + visitor.getVisitorname() + ",\n\nYour appointment has been successfully booked.\nDetails:\nDate: " + appointment.getDate() + "\nTime: " + appointment.getTime() + "\nPasscode: " + passcode + "\nDepartment: " + appointment.getDepartment();

            logger.info("Sending appointment details email to: {}", email);

            // Send the appointment details via email
            try {
                emailService.sendEmail(email, subject, body);
                logger.info("Appointment details email sent successfully to: {}", email);
            } catch (Exception e) {
                logger.error("Failed to send appointment details email to: {}", email, e);
            }

            return savedAppointment;
        } else {
            // Visitor's email not found, log a warning and return null or handle accordingly
            logger.warn("Visitor's email address is null or visitor not found. Skipping appointment details email sending.");
            return null;
        }
    }

    public List<Time> getAvailableSlots(Date date, String department) {
        try {
            logger.info("Retrieving available slots for date: {} and department: {}", date, department);

            // Retrieve booked slots for the selected date and department
            List<Appointment> appointments = appointmentRepository.findByDepartmentAndDate(department, date);
            List<Time> bookedSlots = appointments.stream()
                    .map(Appointment::getTime)
                    .collect(Collectors.toList());

            // Define your working hours and slot duration
            List<Time> allSlots = Stream.of(
                    "09:00:00", "10:00:00", "11:00:00",
                    "12:00:00", "13:00:00", "14:00:00",
                    "15:00:00", "16:00:00"
            ).map(Time::valueOf).collect(Collectors.toList());

            List<Time> availableSlots = allSlots.stream()
                    .filter(slot -> bookedSlots.stream().filter(bookedSlot -> bookedSlot.equals(slot)).count() < 5)
                    .collect(Collectors.toList());

            logger.info("Available slots retrieved successfully.");
            return availableSlots;
        } catch (Exception e) {
            logger.error("An error occurred while retrieving available slots.", e);
            throw e; // Re-throw the exception to be handled by the controller
        }
    }


    private boolean isSlotAvailable(String department, Date date, Time time) {
        long count = appointmentRepository.countByDepartmentAndDateAndTime(department, date, time);
        return count < 5;
    }

    private boolean canMakeAppointment(int visitorId) {
        List<Appointment> appointments = appointmentRepository.findByVisitorVisitorid(visitorId);

        if (appointments.isEmpty()) {
            return true;
        }

        for (Appointment appointment : appointments) {
            if (!isAppointmentCompleted(appointment)) {
                return false;
            }
        }

        return true;
    }

    private boolean isAppointmentCompleted(Appointment appointment) {
        return "attended".equalsIgnoreCase(appointment.getAppointmentstatus()) ||
               "canceled".equalsIgnoreCase(appointment.getAppointmentstatus());
    }

    public Appointment updateAppointment(int id, Appointment newAppointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();
            existingAppointment.setDate(newAppointment.getDate());
            existingAppointment.setTime(newAppointment.getTime());
            existingAppointment.setAppointmentstatus(newAppointment.getAppointmentstatus());
            existingAppointment.setVisitor(newAppointment.getVisitor());
            existingAppointment.setDepartment(newAppointment.getDepartment());
            return appointmentRepository.save(existingAppointment);
        }
        return null;
    }

    public void deleteAppointment(int id) {
        appointmentRepository.deleteById(id);
    }

    public Appointment cancelAppointment(int id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            appointment.setAppointmentstatus("canceled");
            LocalDateTime now = LocalDateTime.now();
            appointment.setCancellationDate(now); // Set cancellation date and time
            logger.info("Cancelling appointment with ID: {}", id);

            // Retrieve visitor email
            Visitor visitor = visitorRepository.findById(appointment.getVisitor().getVisitorid()).orElse(null);
            if (visitor != null && visitor.getEmail() != null) {
                String email = visitor.getEmail();
                String subject = "Your Appointment Cancellation";
                String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);
                String formattedTime = DateTimeFormatter.ofPattern("HH:mm:ss").format(now);
                String body = "Dear " + visitor.getVisitorname() + ",\n\nYour request to cancel your appointment scheduled for " + appointment.getDate() + " at " + appointment.getTime() + " has been processed successfully on " + formattedDate + " " + formattedTime + "\nFeel free to schedule another appointment soon!"+ ".\n\nRegards,\nGikTek Company";

                logger.info("Sending cancellation email to: {}", email);

                // Send the cancellation email
                try {
                    emailService.sendEmail(email, subject, body);
                    logger.info("Cancellation email sent successfully to: {}", email);
                } catch (Exception e) {
                    logger.error("Failed to send cancellation email to: {}", email, e);
                }
            } else {
                logger.warn("Visitor's email address is null or visitor not found. Skipping cancellation email sending.");
            }

            return appointmentRepository.save(appointment);
        }
        logger.warn("Appointment with ID: {} not found. Cannot cancel.", id);
        return null;
    }
}
