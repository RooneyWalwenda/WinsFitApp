package appointment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.twilio.Twilio;
import com.twilio.http.TwilioRestClient;

@Configuration
public class TwilioConfig {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String phoneNumber;

    @Bean
    public TwilioRestClient twilioInitializer() {
        Twilio.init(accountSid, authToken);
        return Twilio.getRestClient();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

