package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle 404 Not Found (both for API endpoints and static resources)
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleNotFound(Exception e) {
        logger.warn("Resource not found: {}", e.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Not Found");
        response.put("message", "The requested resource was not found");
        response.put("suggestions", new String[]{
                "/api/exercises",
                "/media/Images/ExerciseVideos/"
        });
        return response;
    }

    // Handle AppointmentNotFoundException
    @ExceptionHandler(AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, Object> handleAppointmentNotFoundException(AppointmentNotFoundException e) {
        logger.warn("Appointment not found: {}", e.getMessage());
        return Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Appointment Not Found",
                "message", e.getMessage()
        );
    }

    // Handle AppointmentAlreadyCanceledException
    @ExceptionHandler(AppointmentAlreadyCanceledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleAppointmentAlreadyCanceledException(AppointmentAlreadyCanceledException e) {
        logger.warn("Appointment already canceled: {}", e.getMessage());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", e.getMessage()
        );
    }

    // Handle AppointmentAlreadyAttendedException
    @ExceptionHandler(AppointmentAlreadyAttendedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleAppointmentAlreadyAttendedException(AppointmentAlreadyAttendedException e) {
        logger.warn("Appointment already attended: {}", e.getMessage());
        return Map.of(
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", e.getMessage()
        );
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleException(Exception e) {
        logger.error("Internal server error: {}", e.getMessage(), e);
        return Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", "An unexpected error occurred. Please try again later."
        );
    }
}