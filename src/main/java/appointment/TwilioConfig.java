package appointment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfig {

    @Value("${twilio.phone.number}")
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
