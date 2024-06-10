package appointment;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {

    private static final Logger logger = LoggerFactory.getLogger(VisitorService.class);

    private final VisitorRepository visitorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SmsService smsService;

    @Autowired
    public VisitorService(VisitorRepository visitorRepository, PasswordEncoder passwordEncoder, EmailService emailService, SmsService smsService) {
        this.visitorRepository = visitorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public List<Visitor> getAllVisitors() {
        logger.info("Fetching all visitors");
        return visitorRepository.findAll();
    }

    public Visitor getVisitorById(int id) {
        logger.info("Fetching visitor by ID: {}", id);
        Optional<Visitor> optionalVisitor = visitorRepository.findById(id);
        return optionalVisitor.orElse(null);
    }

    public Visitor createVisitor(Visitor visitor) {
        logger.info("Creating new visitor with email: {}", visitor.getEmail());
        visitor.setPassword(passwordEncoder.encode(visitor.getPassword()));
        Visitor savedVisitor = visitorRepository.save(visitor);

        logger.info("Sending welcome email to: {}", savedVisitor.getEmail());
        String subject = "Booking made easy!";
        String body = "Dear " + savedVisitor.getVisitorname() + ",\n\nWelcome to Giktek, your convenient and time-saving appointment booking app! We're thrilled to have you on board.\r\n"
                + "\r\n"
                + "With Giktek, you can easily schedule appointments for a variety of services, all from the comfort of your phone or computer.";
        emailService.sendEmail(savedVisitor.getEmail(), subject, body);

        logger.info("Sending welcome SMS to: {}", savedVisitor.getPhone_number());
        String smsBody = "Welcome to Wins, " + savedVisitor.getVisitorname() + "! We're excited to have you.";
        smsService.sendSms(savedVisitor.getPhone_number(), smsBody);

        return savedVisitor;
    }

    public Visitor loginVisitor(String email, String password) {
        logger.info("Attempting to log in visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            if (passwordEncoder.matches(password, visitor.getPassword())) {
                logger.info("Login successful for visitor with email: {}", email);
                return visitor;
            } else {
                logger.warn("Password mismatch for visitor with email: {}", email);
            }
        } else {
            logger.warn("Visitor with email: {} not found", email);
        }
        return null;
    }

    public void initiatePasswordReset(String email) {
        logger.info("Initiating password reset for visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            String newPassword = generateRandomPassword();
            visitor.setPassword(passwordEncoder.encode(newPassword));
            visitorRepository.save(visitor);
            sendPasswordResetEmail(visitor.getEmail(), newPassword);
            logger.info("Password reset initiated successfully for visitor with email: {}", email);
        } else {
            logger.warn("Visitor with email: {} not found", email);
        }
    }

    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }

    private void sendPasswordResetEmail(String email, String newPassword) {
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            String visitorName = visitor.getVisitorname();
            String subject = "Password Reset Request";
            String body = "Dear " + visitorName + ",\n\nYour password has been reset successfully. Your new password is: " + newPassword + "\n\nPlease log in using this password and change it immediately.";
            emailService.sendEmail(email, subject, body);
        } else {
            logger.error("Visitor with email {} not found while sending password reset email", email);
        }
    }

    public Visitor updateVisitor(int id, Visitor newVisitor) {
        logger.info("Updating visitor with ID: {}", id);
        Optional<Visitor> optionalVisitor = visitorRepository.findById(id);
        if (optionalVisitor.isPresent()) {
            Visitor existingVisitor = optionalVisitor.get();
            existingVisitor.setVisitorname(newVisitor.getVisitorname());
            existingVisitor.setCompany(newVisitor.getCompany());
            existingVisitor.setEmail(newVisitor.getEmail());
            existingVisitor.setPhone_number(newVisitor.getPhone_number());
            existingVisitor.setCheckin_time(newVisitor.getCheckin_time());
            existingVisitor.setCheckout_time(newVisitor.getCheckout_time());
            existingVisitor.setVisit_status(newVisitor.getVisit_status());
            return visitorRepository.save(existingVisitor);
        } else {
            logger.warn("Visitor with ID: {} not found", id);
            return null;
        }
    }

    public void deleteVisitor(int id) {
        logger.info("Deleting visitor with ID: {}", id);
        visitorRepository.deleteById(id);
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            if (passwordEncoder.matches(oldPassword, visitor.getPassword())) {
                visitor.setPassword(passwordEncoder.encode(newPassword));
                visitorRepository.save(visitor);
                logger.info("Password changed successfully for visitor with email: {}", email);
                return true;
            } else {
                logger.warn("Old password mismatch for visitor with email: {}", email);
            }
        } else {
            logger.warn("Visitor with email: {} not found", email);
        }
        return false;
    }
}
