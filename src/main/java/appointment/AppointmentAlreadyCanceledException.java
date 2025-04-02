package appointment;

import java.io.Serializable;

public class AppointmentAlreadyCanceledException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppointmentAlreadyCanceledException(String message) {
        super(message);
    }
}
