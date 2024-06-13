package appointment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle AppointmentNotFoundException
    @ExceptionHandler(AppointmentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleAppointmentNotFoundException(AppointmentNotFoundException e) {
        logger.warn("Appointment not found: {}", e.getMessage());
        return e.getMessage();
    }

    // Handle AppointmentAlreadyCanceledException
    @ExceptionHandler(AppointmentAlreadyCanceledException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleAppointmentAlreadyCanceledException(AppointmentAlreadyCanceledException e) {
        logger.warn("Appointment already canceled: {}", e.getMessage());
        return e.getMessage();
    }

    // Handle AppointmentAlreadyAttendedException
    @ExceptionHandler(AppointmentAlreadyAttendedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleAppointmentAlreadyAttendedException(AppointmentAlreadyAttendedException e) {
        logger.warn("Appointment already attended: {}", e.getMessage());
        return e.getMessage();
    }

    // Handle generic exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception e) {
        logger.error("Internal server error: {}", e.getMessage(), e);
        return "An error occurred. Please try again later.";
    }
}
