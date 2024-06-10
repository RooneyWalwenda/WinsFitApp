package appointment;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class RoleFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        // Check if the request requires role-based access control
        if (requiresRoleCheck(method, requestURI)) {
            // Extract the role from request headers or session
            String role = httpRequest.getHeader("Role");

            // Check if the role is allowed for the requested endpoint
            if (role == null) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("Access Denied: Missing Role");
                return;
            }

            switch (requestURI) {
                case "/api/users/admin":
                case "/api/users/receptionist":
                    if (!role.equals("ADMIN")) {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.getWriter().write("Access Denied: Only ADMIN can create a user");
                        return;
                    }
                    break;
                case "/api/users":
                    if (!role.equals("ADMIN")) {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.getWriter().write("Access Denied: Only ADMIN can get all users");
                        return;
                    }
                    break;
                case "/api/users/":
                    if (!role.equals("ADMIN") && !method.equals("GET")) {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.getWriter().write("Access Denied: Only ADMIN can edit or delete a user");
                        return;
                    }
                    break;
                case "/api/appointments":
                    if (!role.equals("ADMIN") && !role.equals("RECEPTIONIST")) {
                        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        httpResponse.getWriter().write("Access Denied: Only ADMIN and RECEPTIONIST can get all appointments");
                        return;
                    }
                    break;
                default:
                    // Endpoint doesn't require special role-based access control
                    break;
            }
        }

        // Continue with the request
        chain.doFilter(request, response);
    }

    private boolean requiresRoleCheck(String method, String requestURI) {
        // Define the endpoints that require role-based access control
        return (method.equals("POST") && (requestURI.equals("/api/users/admin") || requestURI.equals("/api/users/receptionist"))) ||
               (method.equals("GET") && (requestURI.equals("/api/users") || requestURI.equals("/api/appointments"))) ||
               (method.equals("PUT") && (requestURI.matches("/api/users/\\d+") || requestURI.matches("/api/appointments/\\d+"))) ||
               (method.equals("DELETE") && (requestURI.matches("/api/users/\\d+") || requestURI.matches("/api/appointments/\\d+")));
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
