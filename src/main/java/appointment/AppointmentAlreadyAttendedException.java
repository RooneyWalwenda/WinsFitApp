package appointment;

import java.io.Serializable;

public class AppointmentAlreadyAttendedException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppointmentAlreadyAttendedException(String message) {
        super(message);
    }
}
