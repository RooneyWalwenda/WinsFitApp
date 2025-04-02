package appointment;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;



@Service
public class UsersService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InstitutionRepository institutionRepository;

    public UsersService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public ResponseEntity<LoginResponse> loginUser(String email, String password, String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                if (user.getRoleName().equalsIgnoreCase(role)) {
                    boolean isDefaultPassword = user.isDefaultPassword(); // Check if the password is default
                    if (isDefaultPassword) {
                        
                        // e.g., prompting the user to change their password.
                        logger.info("User logged in with default password: {}", email);
                    }
                    logger.info("User logged in successfully: {}", email);
                    LoginResponse loginResponse = new LoginResponse(user, isDefaultPassword);
                    return ResponseEntity.ok(loginResponse);
                } else {
                    logger.warn("Failed login attempt: Role mismatch for user {}", email);
                    throw new IllegalArgumentException("Invalid role");
                }
            } else {
                logger.warn("Failed login attempt: Incorrect password for user {}", email);
                throw new IllegalArgumentException("Incorrect password");
            }
        } else {
            logger.warn("Failed login attempt: User not found for email {}", email);
            throw new IllegalArgumentException("User not found");
        }
    }


    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }

    public Users getUserById(int id) {
        Optional<Users> optionalUser = usersRepository.findById(id);
        return optionalUser.orElse(null);
    }

    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }

    public Users createUser(Users user, Users creatingUser) {
        // Set the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Check the role of the user being created and handle accordingly
        if (user.getRole() == UserRole.INSTITUTION_ADMIN || user.getRole() == UserRole.PHYSIOTHERAPIST) {
            return usersRepository.save(user);
        } else {
            throw new IllegalArgumentException("Invalid role specified for user creation.");
        }
    }


    public Users updateUser(int id, Users newUser) {
        logger.info("Attempting to update user with ID: {}", id);
        Optional<Users> optionalUser = usersRepository.findById(id);
        if (optionalUser.isPresent()) {
            Users existingUser = optionalUser.get();
            // Update user details
            existingUser.setUsername(newUser.getUsername());
            //existingUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            existingUser.setEmail(newUser.getEmail());  // Update email field
            Users updatedUser = usersRepository.save(existingUser);
            logger.info("User with ID: {} updated successfully", id);
            return updatedUser;
        } else {
            logger.warn("User with ID: {} not found", id);
            return null;
        }
    }

    public Users createSuperAdmin(String email, String password, String username) {
        Users superAdmin = new Users();
        superAdmin.setEmail(email);
        superAdmin.setPassword(passwordEncoder.encode(password));
        superAdmin.setUsername(username);
        superAdmin.setRole(UserRole.SUPERADMIN);

        return usersRepository.save(superAdmin);
    }

    public void deleteUser(int id, Users deletingUser) {
        Optional<Users> optionalUser = usersRepository.findById(id);
        if (optionalUser.isPresent()) {
            usersRepository.deleteById(id);
            logger.info("Successfully deleted user with ID: {}", id);
        } else {
            logger.error("User with ID: {} not found.", id);
            throw new IllegalArgumentException("User not found.");
        }
    }

    public Users createInstitutionAdmin(Users user, Users creatingUser, Long Id) {
        // Ensure that institutionId is provided
        if (Id != null) {
            // Set the role to INSTITUTION_ADMIN
            user.setRole(UserRole.INSTITUTION_ADMIN);
            // Set the institutionId for the new admin
            user.setInstitutionId(Id);
            // Set institution name based on institutionId
            String institutionName = fetchInstitutionNameById(Id);
            user.setInstitutionName(institutionName);
            // Encode the password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            // Save the user
            return usersRepository.save(user);
        } else {
            throw new IllegalArgumentException("Institution ID is required to create an institution admin.");
        }
    }


    // Method to fetch institution name based on institutionId
    private String fetchInstitutionNameById(Long institutionId) {
        Optional<Institution> institutionOptional = institutionRepository.findById(institutionId);
        return institutionOptional.map(Institution::getName).orElse(null);
    }

    public List<Users> getUsersByInstitutionId(Long institutionId) {
        List<Users> users = usersRepository.findByInstitutionId(institutionId);
        logger.info("Retrieved {} users for institution with ID {}", users.size(), institutionId);
        return users;
    }

    public Users createPhysiotherapist(Users user, Long institutionId) {
        logger.info("Starting physiotherapist creation for email: {}", user.getEmail());

        // Validate user details
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            logger.error("Password is null or empty for user: {}", user.getEmail());
            throw new IllegalArgumentException("Password cannot be null or empty.");
        }
        if (institutionId == null) {
            logger.error("Institution ID is null for physiotherapist: {}", user.getEmail());
            throw new IllegalArgumentException("Institution ID is required.");
        }

        // Set the role to PHYSIOTHERAPIST
        user.setRole(UserRole.PHYSIOTHERAPIST);
        logger.info("Assigned role 'PHYSIOTHERAPIST' to user: {}", user.getEmail());

        // Set the institution ID for the physiotherapist
        user.setInstitutionId(institutionId);

        // Fetch institution name based on institutionId
        String institutionName = fetchInstitutionNameById(institutionId);
        user.setInstitutionName(institutionName);
        logger.info("Assigned institution '{}' (ID: {}) to physiotherapist: {}", institutionName, institutionId, user.getEmail());

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("Password successfully encoded for physiotherapist: {}", user.getEmail());

        try {
            // Save the physiotherapist
            Users savedUser = usersRepository.save(user);
            logger.info("Successfully created physiotherapist '{}' associated with institution '{}'", savedUser.getUsername(), institutionName);
            return savedUser;
        } catch (Exception e) {
            logger.error("Failed to create physiotherapist '{}': {}", user.getEmail(), e.getMessage());
            throw new IllegalArgumentException("Failed to create physiotherapist");
        }
    }

    @Transactional
    public void initiatePasswordReset(String email) {
        logger.info("Initiating password reset for user with email: {}", email);
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            String newPassword = generateRandomPassword();
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setDefaultPassword(true); // Set isDefaultPassword to true
            usersRepository.save(user);
            emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), newPassword);
            logger.info("Password reset initiated successfully for user with email: {}", email);
        } else {
            logger.warn("User with email: {} not found", email);
            throw new IllegalArgumentException("User not found");
        }
    }


    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        String email = changePasswordRequest.getEmail();
        String oldPassword = changePasswordRequest.getOldPassword();
        String newPassword = changePasswordRequest.getNewPassword();

        logger.info("Attempting to change password for user with email: {}", email);
        Optional<Users> optionalUser = usersRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setDefaultPassword(false); // Set isDefaultPassword to false after changing password
                usersRepository.save(user);
                logger.info("Password changed successfully for user with email: {}", email);
                return ResponseEntity.ok(user); // Return user details after successful password change
            } else {
                logger.warn("Old password mismatch for user with email: {}", email);
                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Old password is incorrect.");
            }
        } else {
            logger.warn("User with email: {} not found", email);
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body("User not found.");
        }
    }


    private String generateRandomPassword() {
        return RandomStringUtils.randomAlphanumeric(8);
    }
}


