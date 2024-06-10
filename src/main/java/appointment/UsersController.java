package appointment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "User Management System")
@RestController
@RequestMapping("/api/users")
public class UsersController {

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

    @ApiOperation(value = "Create a new admin user")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<Users> createAdmin(
            @ApiParam(value = "User object to store in database table", required = true)
            @RequestBody Users user) {
        user.setRoleName("ADMIN");
        Users newUser = usersService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @ApiOperation(value = "Create a new receptionist user")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/receptionist")
    public ResponseEntity<Users> createReceptionist(
            @ApiParam(value = "User object to store in database table", required = true)
            @RequestBody Users user) {
        user.setRoleName("RECEPTIONIST");
        Users newUser = usersService.createUser(user);
        return ResponseEntity.ok(newUser);
    }

    @ApiOperation(value = "Login user")
    @PostMapping("/login")
    public ResponseEntity<Users> loginUser(
            @ApiParam(value = "Login request object", required = true)
            @RequestBody LoginRequest loginRequest) {
        Users user = usersService.loginUser(loginRequest.getEmail(), loginRequest.getPassword(), loginRequest.getRole());
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    @ApiOperation(value = "Update an existing user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Users> updateUser(
            @ApiParam(value = "User Id to update user object", required = true)
            @PathVariable int id,
            @ApiParam(value = "Updated user object", required = true)
            @RequestBody Users newUser) {
        Users updatedUser = usersService.updateUser(id, newUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Delete a user")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @ApiParam(value = "User Id from which user object will delete from database table", required = true)
            @PathVariable int id) {
        usersService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "View a list of appointments", response = List.class)
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    @GetMapping("/appointments")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @ApiOperation(value = "Get an appointment by Id")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> deleteAppointment(
            @ApiParam(value = "Appointment Id from which appointment object will delete from database table", required = true)
            @PathVariable int id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation(value = "Cancel an appointment")
    @PreAuthorize("hasRole('ADMIN')")
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
}
