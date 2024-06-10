package appointment;

import java.security.SecureRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PasscodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSCODE_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private AppointmentRepository appointmentRepository;

    public String generateUniquePasscode() {
        String passcode;
        do {
            passcode = generatePasscode();
        } while (appointmentRepository.existsByPasscode(passcode));
        return passcode;
    }

    private String generatePasscode() {
        StringBuilder passcode = new StringBuilder(PASSCODE_LENGTH);
        for (int i = 0; i < PASSCODE_LENGTH; i++) {
            passcode.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return passcode.toString();
    }
}
