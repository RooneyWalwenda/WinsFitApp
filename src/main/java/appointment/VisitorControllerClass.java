package appointment;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;

@Api(value = "Visitor Management System")
@RestController
@RequestMapping("/api/visitors")
@Validated
public class VisitorControllerClass {

    private static final Logger logger = LoggerFactory.getLogger(VisitorControllerClass.class);
    //@Autowired
    // private Validator validator; // Injecting the Validator bean
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
    public ResponseEntity<?> createVisitor(
            @ApiParam(value = "Visitor object to store in database table", required = true)
            @Valid @RequestBody Visitor visitor) {

        try {
            Visitor createdVisitor = visitorService.createVisitor(visitor);
            logger.info("Created visitor with email: {}", createdVisitor.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVisitor);
        } catch (IllegalArgumentException e) {
            // Handle validation or password criteria failure
            logger.error("Error creating visitor", e);
            return ResponseEntity.badRequest().body(new ResponseModel("Validation error", "VALIDATION_ERROR", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating visitor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseModel("Failed to create visitor", "SERVER_ERROR"));
        }
    }



    @ApiOperation(value = "Login visitor")
    @PostMapping("/login")
    public ResponseEntity<?> loginVisitor(
            @ApiParam(value = "Login request object", required = true)
            @RequestBody LoginRequest loginRequest) {
        return visitorService.loginVisitor(loginRequest.getEmail(), loginRequest.getPassword());
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
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

        return visitorService.changePassword(changePasswordRequest);
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
    @ApiOperation(value = "Fetch Welcome Section for a Visitor")
    @GetMapping("/{id}/welcome")
    public ResponseEntity<Map<String, String>> getWelcomeSection(
            @ApiParam(value = "Visitor ID for fetching welcome section", required = true)
            @PathVariable int id) {
        logger.info("Fetching welcome section for visitor ID: {}", id);
        return ResponseEntity.ok(visitorService.getWelcomeDetails(id));
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
