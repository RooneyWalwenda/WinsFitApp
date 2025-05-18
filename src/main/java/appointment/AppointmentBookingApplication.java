package appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;  // Changed from javax to jakarta
import java.io.File;

@SpringBootApplication
@EnableJpaRepositories("appointment")
public class AppointmentBookingApplication {

    @Value("${spring.web.resources.static-locations}")
    private String[] staticLocations;

    @Value("${application.video-dir.create-if-missing:false}")
    private boolean createVideoDir;

    public static void main(String[] args) {
        SpringApplication.run(AppointmentBookingApplication.class, args);
    }

    @PostConstruct
    public void init() {
        if (createVideoDir) {
            for (String location : staticLocations) {
                if (location.startsWith("file:")) {
                    String path = location.substring(5);
                    File dir = new File(path);
                    if (!dir.exists()) {
                        boolean created = dir.mkdirs();
                        System.out.println("Video directory " + path + " created: " + created);
                    }
                }
            }
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterRegistration() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter());
        registrationBean.addUrlPatterns("/api/users/*", "/api/appointments/*");
        return registrationBean;
    }
}