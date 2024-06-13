package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendHtmlEmail(String to, String subject, String body) throws MessagingException {
        logger.info("Preparing to send email to: {}", to);
        if (to == null || to.isEmpty()) {
            logger.warn("Email recipient address is null or empty. Skipping email send.");
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true); // true indicates HTML content

        mailSender.send(mimeMessage);
        logger.info("Email sent successfully to: {}", to);
    }

    public void sendRescheduledAppointmentEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("passcode", appointment.getPasscode()); // New passcode
        context.setVariable("appointmentId", appointment.getAppointmentid());
        context.setVariable("department", appointment.getDepartment());
        context.setVariable("institutionName", appointment.getInstitution().getName());

        try {
            logger.info("Attempting to process Thymeleaf template for email body.");
            String body = templateEngine.process("rescheduleEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(appointment.getVisitor().getEmail(), "Appointment Rescheduled Notification", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
            // Handle the template error, possibly rethrow or send a default email
        } catch (MessagingException e) {
            logger.error("Failed to send email to: {}", appointment.getVisitor().getEmail(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending email: {}", e.getMessage());
        }
    }


    public void sendCancellationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());

        try {
            logger.info("Attempting to process Thymeleaf template for cancellation email body.");
            String body = templateEngine.process("cancelEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(appointment.getVisitor().getEmail(), "Appointment Cancellation Notification", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
            // Handle the template error, possibly rethrow or send a default email
        } catch (MessagingException e) {
            logger.error("Failed to send cancellation email to: {}", appointment.getVisitor().getEmail(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending cancellation email: {}", e.getMessage());
        }
    }

    public void sendCheckoutConfirmationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("department", appointment.getDepartment());

        try {
            logger.info("Attempting to process Thymeleaf template for checkout email body.");
            String body = templateEngine.process("checkoutEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(appointment.getVisitor().getEmail(), "Thank You for Attending Your Appointment", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
            // Handle the template error, possibly rethrow or send a default email
        } catch (MessagingException e) {
            logger.error("Failed to send checkout email to: {}", appointment.getVisitor().getEmail(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending checkout email: {}", e.getMessage());
        }
    }
    
    public void sendBookingConfirmationEmail(Appointment appointment) {
        Context context = new Context();
        context.setVariable("visitorName", appointment.getVisitor().getVisitorname());
        context.setVariable("appointmentId", appointment.getAppointmentid());
        context.setVariable("date", appointment.getDate().toString());
        context.setVariable("time", appointment.getTime().toString());
        context.setVariable("passcode", appointment.getPasscode());
        context.setVariable("department", appointment.getDepartment());
        context.setVariable("institutionName", appointment.getInstitution().getName());

        try {
            logger.info("Attempting to process Thymeleaf template for booking confirmation email body.");
            String body = templateEngine.process("bookingConfirmationEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(appointment.getVisitor().getEmail(), "Your Appointment Details", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
            // Handle the template error, possibly rethrow or send a default email
        } catch (MessagingException e) {
            logger.error("Failed to send booking confirmation email to: {}", appointment.getVisitor().getEmail(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending booking confirmation email: {}", e.getMessage());
        }
    }
    
    public void sendPasswordResetEmail(String email, String visitorName, String newPassword) {
        Context context = new Context();
        context.setVariable("visitorName", visitorName);
        context.setVariable("newPassword", newPassword);

        try {
            logger.info("Attempting to process Thymeleaf template for password reset email body.");
            String body = templateEngine.process("passwordResetEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(email, "Password Reset Request", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
        } catch (MessagingException e) {
            logger.error("Failed to send password reset email to: {}", email, e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending password reset email: {}", e.getMessage());
        }
    }
    
    public void sendWelcomeEmail(Visitor visitor, String formattedVisitorId) {
        Context context = new Context();
        context.setVariable("visitorName", visitor.getVisitorname());
        context.setVariable("visitorId", formattedVisitorId);

        try {
            logger.info("Attempting to process Thymeleaf template for welcome email body.");
            String body = templateEngine.process("welcomeEmailTemplate", context);
            logger.info("Thymeleaf template processed successfully.");
            sendHtmlEmail(visitor.getEmail(), "Booking made easy!", body);
        } catch (TemplateInputException e) {
            logger.error("Error parsing Thymeleaf template: {}", e.getMessage());
        } catch (MessagingException e) {
            logger.error("Failed to send welcome email to: {}", visitor.getEmail(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while sending welcome email: {}", e.getMessage());
        }
    }

}
