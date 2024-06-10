package appointment;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@Api(value = "Visitor Management System")
@RestController
@RequestMapping("/api/visitors")
public class VisitorControllerClass {

    @Autowired
    private VisitorService visitorService;

    @ApiOperation(value = "View a list of visitors", response = List.class)
    @GetMapping
    public List<Visitor> getAllVisitors() {
        return visitorService.getAllVisitors();
    }

    @ApiOperation(value = "Get a visitor by Id")
    @GetMapping("/{id}")
    public Visitor getVisitorById(
            @ApiParam(value = "Visitor id from which visitor object will retrieve", required = true)
            @PathVariable int id) {
        return visitorService.getVisitorById(id);
    }

    @ApiOperation(value = "Create a new visitor")
    @PostMapping
    public Visitor createVisitor(
            @ApiParam(value = "Visitor object to store in database table", required = true)
            @RequestBody Visitor visitor) throws Exception {
        return visitorService.createVisitor(visitor);
    }

    @ApiOperation(value = "Login visitor")
    @PostMapping("/login")
    public ResponseEntity<Visitor> loginVisitor(
            @ApiParam(value = "Login request object", required = true)
            @RequestBody LoginRequest loginRequest) {
        Visitor visitor = visitorService.loginVisitor(loginRequest.getEmail(), loginRequest.getPassword());
        if (visitor != null) {
            return ResponseEntity.ok(visitor);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @ApiOperation(value = "Reset visitor password")
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(
            @ApiParam(value = "Reset password request object", required = true)
            @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String email = resetPasswordRequest.getEmail();
        visitorService.initiatePasswordReset(email);
        return ResponseEntity.ok("Password reset initiated successfully. Please check your email for further instructions.");
    }

    @ApiOperation(value = "Change visitor password")
    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(
            @ApiParam(value = "Change password request object", required = true)
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean isPasswordChanged = visitorService.changePassword(
                changePasswordRequest.getEmail(),
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword()
        );

        if (isPasswordChanged) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Password change failed.");
        }
    }

    @ApiOperation(value = "Check in a visitor")
    @PutMapping("/{id}/checkin")
    public ResponseEntity<Visitor> checkinVisitor(
            @ApiParam(value = "Visitor Id to check in visitor", required = true)
            @PathVariable int id) {
        Visitor visitor = visitorService.getVisitorById(id);
        if (visitor != null) {
            visitor.setVisit_status("active");
            visitor.setCheckin_time(new Date(System.currentTimeMillis()));
            visitorService.updateVisitor(id, visitor);
            return ResponseEntity.ok(visitor);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Check out a visitor")
    @PutMapping("/{id}/checkout")
    public ResponseEntity<Visitor> checkoutVisitor(
            @ApiParam(value = "Visitor Id to check out visitor", required = true)
            @PathVariable int id) {
        Visitor visitor = visitorService.getVisitorById(id);
        if (visitor != null) {
            visitor.setVisit_status("done");
            visitor.setCheckout_time(new Date(System.currentTimeMillis()));
            visitorService.updateVisitor(id, visitor);
            return ResponseEntity.ok(visitor);
        }
        return ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Delete a visitor")
    @DeleteMapping("/{id}")
    public void deleteVisitor(
            @ApiParam(value = "Visitor Id from which visitor object will delete from database table", required = true)
            @PathVariable int id) {
        visitorService.deleteVisitor(id);
    }

    @ApiOperation(value = "Test endpoint")
    @GetMapping("/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }
}
