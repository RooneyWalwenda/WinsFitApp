package appointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"appointment"})
@EnableJpaRepositories("appointment")
public class AppointmentBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentBookingApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> roleFilterRegistration() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter());
        registrationBean.addUrlPatterns("/api/users/*", "/api/appointments/*"); // Include all relevant endpoints
        return registrationBean;
    }
}
