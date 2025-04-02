package appointment;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(description = "userController", name = "UsersApis")
@Api(value = "User Management System")
@RestController
@RequestMapping("/api/users")
public class UsersController {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private AppointmentService appointmentService;

    @ApiOperation(value = "View a list of users", response = List.class)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Users> getAllUsers() {
        return usersService.getAllUsers();
    }

    @ApiOperation(value = "Get a user by Id")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Users> getUserById(
            @ApiParam(value = "User id from which user object will retrieve", required = true)
            @PathVariable int id) {
        Users user = usersService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Create a new institution admin user")
    @PostMapping("/institution-admin")
    public ResponseEntity<Users> createInstitutionAdmin(
            @ApiParam(value = "User object to store in database table", required = true)
            @RequestBody Users user,
            @RequestParam(name = "institutionId") Long Id) { // Accept institutionId as a request parameter
        // Create the new institution admin
        Users newUser = usersService.createInstitutionAdmin(user, getCurrentUser(), Id);
        return ResponseEntity.ok(newUser);
    }



    @ApiOperation(value = "Create a new physiotherapist user")
    @PostMapping("/physiotherapist")
    public ResponseEntity<Users> createReceptionist(
            @ApiParam(value = "User object to store in database table", required = true)
            @RequestBody Users user,
            @RequestParam(name = "institutionId") Long institutionId) { // Accept institutionId as a request parameter
        Users newUser = usersService.createPhysiotherapist(user, institutionId);
        return ResponseEntity.ok(newUser);
    }

    @ApiOperation(value = "Login user")
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @ApiParam(value = "Login request object", required = true)
            @RequestBody LoginRequest loginRequest) {
        try {
            ResponseEntity<LoginResponse> user = usersService.loginUser(loginRequest.getEmail(), loginRequest.getPassword(), loginRequest.getRole());
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    @ApiOperation(value = "Update an existing user")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @ApiParam(value = "User Id to update user object", required = true)
            @PathVariable int id,
            @ApiParam(value = "Updated user object", required = true)
            @RequestBody Users newUser) {
        logger.info("Received request to update user with ID: {}", id);
        Users updatedUser = usersService.updateUser(id, newUser);
        if (updatedUser != null) {
            logger.info("User with ID: {} updated successfully", id);
            return ResponseEntity.ok(updatedUser);
        } else {
            logger.warn("User with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(new ResponseModel("User not found", "USER_NOT_FOUND"));
        }
    }

    @ApiOperation(value = "Delete a user")
    @PreAuthorize("hasAnyRole('SUPERADMIN', 'INSTITUTION_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @ApiParam(value = "User Id from which user object will delete from database table", required = true)
            @PathVariable int id) {
        usersService.deleteUser(id, getCurrentUser()); // Assuming you have a method to get the current user
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "View a list of appointments", response = List.class)
    @PreAuthorize("hasAnyRole('INSTITUTION_ADMIN', 'PHYSIOTHERAPIST')")
    @GetMapping("/appointments")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @ApiOperation(value = "Get an appointment by Id")
    @PreAuthorize("hasAnyRole('INSTITUTION_ADMIN', 'PHYSIOTHERAPIST')")
    @GetMapping("/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(
            @ApiParam(value = "Appointment id from which appointment object will retrieve", required = true)
            @PathVariable int id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        if (appointment != null) {
            return ResponseEntity.ok(appointment);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Update an existing appointment")
    @PreAuthorize("hasRole('INSTITUTION_ADMIN')")
    @PutMapping("/appointments/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @ApiParam(value = "Appointment Id to update appointment object", required = true)
            @PathVariable int id,
            @ApiParam(value = "Updated appointment object", required = true)
            @RequestBody Appointment newAppointment) {
        Appointment updatedAppointment = appointmentService.updateAppointment(id, newAppointment);
        if (updatedAppointment != null) {
            return ResponseEntity.ok(updatedAppointment);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Delete an appointment")
    @PreAuthorize("hasRole('INSTITUTION_ADMIN')")
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @ApiParam(value = "Appointment Id from which appointment object will delete from database table", required = true)
            @PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Cancel an appointment")
    @PreAuthorize("hasRole('INSTITUTION_ADMIN')")
    @PutMapping("/appointments/{id}/cancel")
    public ResponseEntity<Appointment> cancelAppointment(
            @ApiParam(value = "Appointment Id to cancel appointment", required = true)
            @PathVariable int id) {
        Appointment canceledAppointment = appointmentService.cancelAppointment(id);
        if (canceledAppointment != null) {
            return ResponseEntity.ok(canceledAppointment);
        }
        return ResponseEntity.notFound().build();
    }
    
    @ApiOperation(value = "Create a new super admin user")
    @PostMapping("/super-admin")
    public ResponseEntity<Users> createSuperAdmin(
            @ApiParam(value = "Super Admin object to store in database table", required = true)
            @RequestBody SuperAdminRequest superAdminRequest) {
        Users newSuperAdmin = usersService.createSuperAdmin(
            superAdminRequest.getEmail(),
            superAdminRequest.getPassword(),
            superAdminRequest.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(newSuperAdmin);
    }

    // Method to get the current user - You need to implement this method
    private Users getCurrentUser() {
        // Implement this method to get the current user
        return null;
    }
    
   
    @GetMapping("/institution/{institutionId}/users")
    public ResponseEntity<List<Users>> getUsersByInstitutionId(
            @PathVariable Long institutionId) {
        List<Users> users = usersService.getUsersByInstitutionId(institutionId);
        if (!users.isEmpty()) {
            return ResponseEntity.ok(users);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
 // Endpoint to initiate password reset
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        try {
            usersService.initiatePasswordReset(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while resetting the password.");
        }
    }

    // Endpoint to change password
 // Endpoint to change password
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            ResponseEntity<?> response = usersService.changePassword(changePasswordRequest);
            return ResponseEntity.ok(response.getBody());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change password.");
        }
    }

}
