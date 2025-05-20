package appointment;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private ResourceLoader resourceLoader;

    public void sendEmailFromTemplate(String to, String subject, String templateName, Context context) {
        try {
            logger.info("Processing Thymeleaf template: {}", templateName);
            String body = templateEngine.process(templateName, context);
            sendHtmlEmail(to, subject, body);
            logger.info("Email sent successfully to: {}", to);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template '{}': {}", templateName, e.getMessage());
        } catch (MessagingException e) {
            logger.error("Failed to send email '{}' to {}: {}", subject, to, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while sending email '{}': {}", subject, e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        try {
            // Load WinsFit Logo
            Resource logoResource = resourceLoader.getResource("classpath:static/Images/WinsFit Logo.png");
            if (logoResource.exists()) {
                helper.addInline("winsFitLogo", logoResource);
            } else {
                logger.warn("WinsFit Logo not found at: classpath:static/Images/WinsFit Logo.png");
            }

            // Load WinsFit Robot
            Resource robotResource = resourceLoader.getResource("classpath:static/Images/winsFit Robot.png");
            if (robotResource.exists()) {
                helper.addInline("winsFitRobot", robotResource);
            } else {
                logger.warn("WinsFit Robot not found at: classpath:static/Images/winsFit Robot.png");
            }
//yes it works
        } catch (Exception e) {
            logger.error("Error loading email images: {}", e.getMessage());
        }

        mailSender.send(message);
    }

    public void sendRescheduledAppointmentEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("passcode", appointment.getPasscode());
        context.setVariable("appointmentId", appointment.getAppointmentid());
        context.setVariable("department", appointment.getDepartment());
        context.setVariable("institutionName", appointment.getInstitution().getName());

        if (appointment.getMeetingType() == MeetingType.VIRTUAL) {
            context.setVariable("videoMeetingLink", appointment.getVideoMeetingLink());
        } else {
            context.setVariable("videoMeetingLink", null);
        }
        sendEmailFromTemplate(appointment.getVisitor().getEmail(), "Appointment Rescheduled Notification", "rescheduleEmailTemplate", context);
    }

    public void sendCancellationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());

        sendEmailFromTemplate(appointment.getVisitor().getEmail(), "Appointment Cancellation Notification", "cancelEmailTemplate", context);
    }

    public void sendCheckoutConfirmationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("institutionName", appointment.getInstitution().getName());

        sendEmailFromTemplate(appointment.getVisitor().getEmail(), "Checkout Confirmation", "checkoutEmailTemplate", context);
    }

    public void sendBookingConfirmationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("department", appointment.getDepartment());
        context.setVariable("institutionName", appointment.getInstitution().getName());

        if (appointment.getMeetingType() == MeetingType.VIRTUAL) {
            context.setVariable("videoMeetingLink", appointment.getVideoMeetingLink());
        } else {
            context.setVariable("videoMeetingLink", null);
        }

        sendEmailFromTemplate(appointment.getVisitor().getEmail(), "Appointment Booking Confirmation", "bookingConfirmationEmailTemplate", context);

        if (appointment.getMeetingType() == MeetingType.VIRTUAL && appointment.getUser() != null) {
            sendPhysiotherapistNotification(appointment);
        }
    }

    public void sendPhysiotherapistNotification(Appointment appointment) {
        Context context = new Context();
        context.setVariable("physiotherapistName", appointment.getUser().getUsername());
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("meetingLink", appointment.getVideoMeetingLink());

        sendEmailFromTemplate(
                appointment.getUser().getEmail(),
                "Virtual Appointment Assigned",
                "physiotherapistEmailTemplate",
                context
        );
    }

    public void sendPasswordResetEmail(String visitorEmail, String visitorName, String temporaryPassword) {
        Context context = new Context();
        context.setVariable("visitorName", visitorName);
        context.setVariable("temporaryPassword", temporaryPassword);

        sendEmailFromTemplate(visitorEmail, "Password Reset", "passwordResetEmailTemplate", context);
    }

    public void sendWelcomeEmail(Visitor visitor, String visitorId) {
        Context context = new Context();
        context.setVariable("visitorName", visitor.getVisitorname());
        context.setVariable("visitorId", visitorId);

        sendEmailFromTemplate(visitor.getEmail(), "Welcome to Our Service", "welcomeEmailTemplate", context);
    }

    public void sendReminderEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("meetingLink", appointment.getVideoMeetingLink());

        sendEmailFromTemplate(
                appointment.getVisitor().getEmail(),
                "Upcoming Appointment Reminder",
                "appointmentReminderTemplate",
                context
        );

        if (appointment.getUser() != null) {
            context.setVariable("physiotherapistName", appointment.getUser().getUsername());
            sendEmailFromTemplate(
                    appointment.getUser().getEmail(),
                    "Upcoming Appointment Reminder",
                    "appointmentReminderTemplate",
                    context
            );
        }
    }
}