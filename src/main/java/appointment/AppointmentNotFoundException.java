package appointment;

import java.io.Serializable;

public class AppointmentNotFoundException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    public AppointmentNotFoundException(String message) {
        super(message);
    }
}
