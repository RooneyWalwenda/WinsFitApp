package appointment;  // Use your existing package name

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // For development - serves files from src/main/resources/static
        registry.addResourceHandler("/Images/ExerciseVideos/**")
                .addResourceLocations("classpath:/static/Images/ExerciseVideos/")
                .setCachePeriod(3600);

        // For production - serves files from external directory if needed
        String externalPath = "file:./exercise-videos/";  // Relative to application directory
        registry.addResourceHandler("/external-videos/**")
                .addResourceLocations(externalPath)
                .setCachePeriod(3600);
    }
}