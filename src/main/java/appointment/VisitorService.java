package appointment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class VisitorService {

    private static final Logger logger = LoggerFactory.getLogger(VisitorService.class);

    private final VisitorRepository visitorRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public VisitorService(VisitorRepository visitorRepository,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.visitorRepository = visitorRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

        // Encode password
        visitor.setPassword(passwordEncoder.encode(visitor.getPassword()));

        // Save visitor
        Visitor savedVisitor = visitorRepository.save(visitor);

        // Send welcome email
        String formattedVisitorId = String.format("%04d", savedVisitor.getVisitorid());
        logger.info("Sending welcome email to: {}", savedVisitor.getEmail());

        emailService.sendWelcomeEmail(savedVisitor, formattedVisitorId);

        return savedVisitor;
    }

    public ResponseEntity<?> loginVisitor(String email, String password) {
        logger.info("Attempting to log in visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);

        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();

            if (passwordEncoder.matches(password, visitor.getPassword())) {
                logger.info("Login successful for visitor with email: {}", email);

                if (visitor.isDefaultPassword()) {
                    return ResponseEntity.ok().body(new ResponseModel("DEFAULT_PASSWORD", "You need to change your default password to a new unique password", visitor));
                }

                return ResponseEntity.ok().body(new ResponseModel("200", "Request executed successfully", visitor));
            } else {
                logger.warn("Password mismatch for visitor with email: {}", email);
                return ResponseEntity.badRequest().body(new ResponseModel("400", "Invalid credentials"));
            }
        } else {
            logger.warn("Visitor with email: {} not found", email);
            return ResponseEntity.badRequest().body(new ResponseModel("400", "User does not exist"));
        }
    }

    public void initiatePasswordReset(String email) {
        logger.info("Initiating password reset for visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            String newPassword = generateRandomPassword();
            visitor.setPassword(passwordEncoder.encode(newPassword));
            visitor.setDefaultPassword(true);
            visitorRepository.save(visitor);
            emailService.sendPasswordResetEmail(visitor.getEmail(), visitor.getVisitorname(), newPassword);
            logger.info("Password reset initiated successfully for visitor with email: {}", email);
        } else {
            logger.warn("Visitor with email: {} not found", email);
        }
    }

    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8);
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
            existingVisitor.setDepartment(newVisitor.getDepartment());

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
    public Map<String, String> getWelcomeDetails(int visitorId) {
        Optional<Visitor> optionalVisitor = visitorRepository.findById(visitorId);

        if (optionalVisitor.isEmpty()) {
            throw new RuntimeException("Visitor not found");
        }

        Visitor visitor = optionalVisitor.get();

        // Ensure proper initialization of HashMap
        Map<String, String> response = new HashMap<>();

        response.put("greetingMessage", "Good Morning, " + visitor.getVisitorname() + "! Let's crush your fitness goals today! 💪");
        response.put("profilePicture", "https://example.com/profile.jpg"); // Placeholder image
        response.put("name", visitor.getVisitorname());
        response.put("age", String.valueOf(visitor.getDob())); // Convert Date to Age in frontend
        response.put("gender", visitor.getGender());
        response.put("fitnessGoal", visitor.getFitness_goal());
        response.put("workoutStreak", "🔥 " + visitor.getWorkout_streak() + "-Day Streak! Keep Going!");
        response.put("nextWorkout", visitor.getWorkout_type() != null && visitor.getWorkout_time() != null
                ? "Next Workout: " + visitor.getWorkout_type() + " - Today at " + visitor.getWorkout_time()
                : "No workout scheduled yet!");

        return response;
    }

    public ResponseEntity<String> changePassword(ChangePasswordRequest changePasswordRequest) {
        String newPassword = changePasswordRequest.getNewPassword();
        boolean isPasswordChanged = changePassword(
                changePasswordRequest.getEmail(),
                changePasswordRequest.getOldPassword(),
                newPassword
        );

        if (isPasswordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password change failed.");
        }
    }

    public boolean changePassword(String email, String oldPassword, String newPassword) {
        logger.info("Attempting to change password for visitor with email: {}", email);
        Optional<Visitor> optionalVisitor = visitorRepository.findByEmail(email);
        if (optionalVisitor.isPresent()) {
            Visitor visitor = optionalVisitor.get();
            if (passwordEncoder.matches(oldPassword, visitor.getPassword())) {
                visitor.setPassword(passwordEncoder.encode(newPassword));
                visitor.setDefaultPassword(false);
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

