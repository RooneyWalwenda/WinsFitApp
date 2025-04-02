package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
    private UsersRepository usersRepository;
    @Autowired
    private InstitutionRepository institutionRepository;

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
        try {
            // Validate visitor existence
            if (appointment.getVisitor() == null) {
                throw new RuntimeException("Visitor details are required.");
            }

            // Fetch visitor details from the database based on visitor ID
            Visitor visitor = visitorRepository.findById(appointment.getVisitor().getVisitorid()).orElse(null);
            if (visitor == null) {
                throw new RuntimeException("Visitor not found for ID: " + appointment.getVisitor().getVisitorid());
            }

            // Set visitor object in appointment
            appointment.setVisitor(visitor);

            // Validate institution ID before fetching
            if (appointment.getInstitution() == null || appointment.getInstitution().getId() == null) {
                throw new RuntimeException("Institution details are required.");
            }

            if (appointment.getInstitution() == null || appointment.getInstitution().getId() == null) {
                throw new RuntimeException("Institution details are required.");
            }
            Institution institution = institutionRepository.findById(appointment.getInstitution().getId()).orElse(null);
            if (institution == null) {
                throw new RuntimeException("Invalid institution ID: " + appointment.getInstitution().getId());
            }
            appointment.setInstitution(institution);

// ✅ Fetch and validate the user only once
            Users user = usersRepository.findById(appointment.getUser().getUserid())
                    .orElseThrow(() -> new RuntimeException("Invalid user ID: " + appointment.getUser().getUserid()));

// ✅ Ensure the user is a physiotherapist before assigning
            if (!user.getRoleName().equals("PHYSIOTHERAPIST")) {
                throw new RuntimeException("User is not a physiotherapist.");
            }

            appointment.setUser(user);

            // Convert java.sql.Time to java.time.LocalTime
            LocalTime time = appointment.getTime().toLocalTime();

            // Validate appointment date and time
            LocalDate date = appointment.getDate();

            if (!isDateTimeInFuture(date, time)) {
                logger.warn("Appointment date or time is in the past.");
                throw new RuntimeException("Cannot book an appointment in the past");
            }

            // Check if appointment can be made
            if (!canMakeAppointment(visitor.getVisitorid(), appointment.getTime(), institution.getId(), appointment.getDepartment())) {
                logger.warn("Visitor {} cannot make a new appointment due to existing conditions not met.", visitor.getVisitorname());
                return null;
            }

            // Generate passcode and set appointment status
            String passcode = passcodeGenerator.generateUniquePasscode();
            appointment.setPasscode(passcode);
            appointment.setAppointmentstatus("booked");

            // Check if appointment is virtual and generate a meeting link
            if (appointment.getMeetingType() == MeetingType.VIRTUAL) {
                String meetingLink = generateMeetingLink(user);

                appointment.setVideoMeetingLink(meetingLink);
            }

            // Save appointment
            Appointment savedAppointment = appointmentRepository.save(appointment);

            // Send booking confirmation email to visitor
            emailService.sendBookingConfirmationEmail(savedAppointment);

            return savedAppointment;
        } catch (Exception e) {
            logger.warn("Appointment creation failed for visitor ID: {}, institution ID: {}, physiotherapist ID: {}",
                    appointment.getVisitor().getVisitorid(),
                    appointment.getInstitution().getId(),
                    appointment.getUser().getUserid()
            );

            throw e; // Rethrow the exception or handle as needed
        }
    }

    // Generate a random meeting link (Jitsi Meet Example)
    private String generateMeetingLink(Users physiotherapist) {
        String uniqueRoom = UUID.randomUUID().toString();

        // Add physiotherapist email as a host parameter (if supported by the meeting provider)
        String meetingLink = "https://meet.jit.si/" + uniqueRoom + "?moderator=" + physiotherapist.getEmail();

        return meetingLink;
    }



    private boolean isDateTimeInFuture(LocalDate date, LocalTime time) {
        // Get the current time with the system's default time zone
        ZonedDateTime now = ZonedDateTime.now();

        // Create a ZonedDateTime instance for the appointment using the system's default time zone
        ZonedDateTime appointmentDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault());

        // Check if the appointment time is after the current time
        return appointmentDateTime.isAfter(now);
    }


    public List<Appointment> getAppointmentsByInstitutionId(Long institutionId) {
        List<Appointment> appointments = appointmentRepository.findByInstitutionId(institutionId);
        logger.info("Retrieved {} appointments for institution with ID {}", appointments.size(), institutionId);
        return appointments;
    }

    public List<Time> getAvailableSlots(LocalDate date, String department, Long institutionId) {
        try {
            logger.info("Retrieving available slots for date: {}, department: {}, institutionId: {}", date, department, institutionId);


            // Check if the date is in the past
            if (date.isBefore(LocalDate.now())) {
                logger.warn("The provided date is in the past.");
                return new ArrayList<>(); // Return an empty list as no slots are available for past dates
            }

            // Fetch institution details
            Institution institution = institutionRepository.findById(institutionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid institution ID"));

            // Retrieve appointments for the given date, department, and institution
            List<Appointment> appointments = appointmentRepository.findByDepartmentAndDateAndInstitution(department, date, institution);

            // Define all possible time slots
            List<Time> allSlots = generateAllTimeSlots();

            // Filter appointments with status 'booked'
            appointments = appointments.stream()
                    .filter(appointment -> appointment.getAppointmentstatus().equals("booked"))
                    .collect(Collectors.toList());

            // Count booked slots for the given department and date
            Map<Time, Long> bookedSlotsMap = appointments.stream()
                    .collect(Collectors.groupingBy(Appointment::getTime, Collectors.counting()));

            // Filter available slots based on concurrency limits
            List<Time> availableSlots = allSlots.stream()
                    .filter(slot -> {
                        long bookedCount = bookedSlotsMap.getOrDefault(slot, 0L);
                        return bookedCount < 5; // Check if booked slots are less than 5
                    })
                    .collect(Collectors.toList());

            logger.info("Available slots retrieved successfully.");
            return availableSlots;
        } catch (Exception e) {
            logger.error("An error occurred while retrieving available slots.", e);
            throw e; // Re-throw the exception to be handled by the controller
        }
    }


    private List<Time> generateAllTimeSlots() {
        // Define all possible time slots
        return Stream.of(
                Time.valueOf("09:00:00"),
                Time.valueOf("10:00:00"),
                Time.valueOf("11:00:00"),
                Time.valueOf("12:00:00"),
                Time.valueOf("14:00:00"),
                Time.valueOf("15:00:00"),
                Time.valueOf("16:00:00")
        ).collect(Collectors.toList());
    }

    private boolean canMakeAppointment(int visitorId, Time newAppointmentTime, Long newInstitutionId, String newDepartment) {
        List<Appointment> appointments = appointmentRepository.findByVisitorVisitorid(visitorId);

        if (appointments.isEmpty()) {
            return true; // No existing appointments, user can make a new appointment
        }

        for (Appointment appointment : appointments) {
            if (!isAppointmentResolved(appointment)) {
                if (appointment.getDepartment().equals(newDepartment)) {
                    logger.warn("Visitor {} has unresolved appointment in department {}.", visitorId, newDepartment);
                    return false; // User has an existing unresolved appointment in the same department, cannot make a new appointment
                }
            }
            // Check if there is an appointment at the same time in a different institution
            if (appointment.getTime().equals(newAppointmentTime) && !appointment.getInstitution().getId().equals(newInstitutionId)) {
                logger.warn("Visitor {} has a concurrent appointment at the same time in a different institution.", visitorId);
                return false; // User has a concurrent appointment at the same time
            }
        }

        return true; // All conditions are met, user can make a new appointment
    }

    private boolean isAppointmentResolved(Appointment appointment) {
        return "attended".equalsIgnoreCase(appointment.getAppointmentstatus()) ||
                "canceled".equalsIgnoreCase(appointment.getAppointmentstatus());
    }

    public Appointment updateAppointment(int id, Appointment newAppointment) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();
            existingAppointment.setVisitor(newAppointment.getVisitor());
            existingAppointment.setDate(newAppointment.getDate());
            existingAppointment.setTime(newAppointment.getTime());
            existingAppointment.setAppointmentstatus(newAppointment.getAppointmentstatus());
            existingAppointment.setDepartment(newAppointment.getDepartment());
            existingAppointment.setPasscode(newAppointment.getPasscode());
            existingAppointment.setInstitution(newAppointment.getInstitution());

            return appointmentRepository.save(existingAppointment);
        }

        return null;
    }

    public Appointment checkInAppointment(int id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if ("attended".equalsIgnoreCase(appointment.getAppointmentstatus())) {
                logger.warn("Appointment with ID: {} has already been attended. Cannot check in again.", id);
                return null; // or throw an exception if you prefer
            }
            if (!"checked_in".equalsIgnoreCase(appointment.getAppointmentstatus())) {
                appointment.setAppointmentstatus("checked_in");
                appointment.setCheckInTime(LocalDateTime.now());
                logger.info("Checked in appointment with ID: {}", id);
                return appointmentRepository.save(appointment);
            } else {
                logger.warn("Appointment with ID: {} is already checked in.", id);
            }
        } else {
            logger.warn("Appointment with ID: {} not found. Cannot check in.", id);
        }
        return null;
    }


    public Appointment checkOutAppointment(int id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if ("checked_in".equalsIgnoreCase(appointment.getAppointmentstatus())) {
                appointment.setAppointmentstatus("attended");
                appointment.setCheckOutTime(LocalDateTime.now());
                logger.info("Checked out appointment with ID: {}", id);


                // Send checkout confirmation email
                sendCheckoutConfirmationEmail(appointment);

                return appointmentRepository.save(appointment);
            } else {
                logger.warn("Appointment with ID: {} is not checked in.", id);
            }
        } else {
            logger.warn("Appointment with ID: {} not found. Cannot check out.", id);
        }
        return null;
    }


    private void sendCheckoutConfirmationEmail(Appointment appointment) {
        try {
            emailService.sendCheckoutConfirmationEmail(appointment);
            logger.info("Checkout confirmation email sent successfully to: {}", appointment.getVisitor().getEmail());
        } catch (Exception e) {
            logger.error("Failed to send checkout confirmation email", e);
        }
    }

    public Appointment cancelAppointment(int id) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            if ("canceled".equalsIgnoreCase(appointment.getAppointmentstatus())) {
                throw new AppointmentAlreadyCanceledException("Appointment with ID " + id + " is already canceled.. cannot cancel a cancelled appointment.");
            } else if ("attended".equalsIgnoreCase(appointment.getAppointmentstatus())) {
                throw new AppointmentAlreadyAttendedException("Appointment with ID " + id + " is already attended.. cannot cancel an attended appointment");
            } else {
                appointment.setAppointmentstatus("canceled");
                LocalDateTime now = LocalDateTime.now();
                appointment.setCancellationDate(now);
                logger.info("Cancelling appointment with ID: {}", id);

                // Send cancellation email
                emailService.sendCancellationEmail(appointment);

                return appointmentRepository.save(appointment);
            }
        } else {
            throw new AppointmentNotFoundException("Appointment with ID " + id + " not found.");
        }
    }


    public void deleteAppointment(int id) {
        // TODO Auto-generated method stub

    }

    public List<Appointment> getAppointmentsByInstitutionIdAndDateAndStatus(Long institutionId, LocalDate date, String status) {
        logger.info("Fetching appointments for institution ID: {}, date: {}, status: {}", institutionId, date, status);
        List<Appointment> appointments = appointmentRepository.findByInstitutionIdAndDateAndAppointmentstatus(institutionId, date, status);
        logger.info("Retrieved {} appointments for institution ID: {}, date: {}, status: {}", appointments.size(), institutionId, date, status);
        return appointments;
    }

    public List<Appointment> getAppointmentsByVisitorId(int visitorId) {
        List<Appointment> appointments = appointmentRepository.findByVisitorVisitorid(visitorId);
        logger.info("Retrieved {} appointments for visitor with ID: {}", appointments.size(), visitorId);
        return appointments;
    }

    @Transactional
    public Appointment rescheduleAppointment(int id, LocalDate newDate, Time newTime) throws Exception {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isPresent()) {
            Appointment existingAppointment = optionalAppointment.get();

            // Check if the appointment status is 'attended' or 'canceled'
            if (existingAppointment.getAppointmentstatus().equalsIgnoreCase("attended") ||
                    existingAppointment.getAppointmentstatus().equalsIgnoreCase("canceled")) {
                throw new Exception("Rescheduling not allowed for attended or canceled appointments.");
            }

            // Convert java.sql.Time to java.time.LocalTime
            LocalTime localTime = newTime.toLocalTime();

            // Validate new appointment date and time
            if (!isDateTimeInFuture(newDate, localTime)) {
                logger.warn("New appointment date or time is in the past.");
                throw new RuntimeException("Cannot reschedule to a past date or time.");
            }

            // Generate a new passcode
            String newPasscode = passcodeGenerator.generateUniquePasscode();
            existingAppointment.setPasscode(newPasscode);

            // Update appointment details
            existingAppointment.setDate(newDate);
            existingAppointment.setTime(newTime);
            existingAppointment.setAppointmentstatus("rescheduled");

            // If the appointment is virtual, generate a new meeting link
            if (existingAppointment.getMeetingType() == MeetingType.VIRTUAL) {
                Users physiotherapist = existingAppointment.getUser(); // ✅ Get physiotherapist

                if (physiotherapist != null) {  // ✅ Ensure physiotherapist is not null
                    String meetingLink = generateMeetingLink(physiotherapist);
                    existingAppointment.setVideoMeetingLink(meetingLink);
                } else {
                    logger.warn("No physiotherapist assigned to appointment ID: {}", existingAppointment.getAppointmentid());
                }
            }


            // ✅ Save and return the updated appointment
            appointmentRepository.save(existingAppointment);
            emailService.sendRescheduledAppointmentEmail(existingAppointment);

            return existingAppointment;
        }

        // ✅ Throw an error if no appointment is found
        throw new Exception("Appointment not found");
    }
    @Scheduled(fixedRate = 60000) // Runs every 60 seconds (1 minute)
    public void sendAppointmentReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reminderTime = now.plusMinutes(30); // Find appointments starting in 30 minutes

        LocalDate today = now.toLocalDate();
        Time startTime = Time.valueOf(now.toLocalTime());
        Time endTime = Time.valueOf(reminderTime.toLocalTime());

        List<Appointment> upcomingAppointments = appointmentRepository.findAppointmentsBetween(today, startTime, endTime);

        for (Appointment appointment : upcomingAppointments) {
            if (appointment.getMeetingType() == MeetingType.VIRTUAL) {
                emailService.sendReminderEmail(appointment); // Ensure this method exists in `EmailService`
            }
        }
    }


}
