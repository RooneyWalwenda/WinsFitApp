package appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories("appointment")
public class AppointmentBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentBookingApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterRegistration() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter());
        registrationBean.addUrlPatterns("/api/users/*", "/api/appointments/*"); // Include all relevant endpoints
        return registrationBean;
    }
}
