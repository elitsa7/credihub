package bg.credihub.security;

import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.UUID;

@Component
public class SessionInterceptor implements HandlerInterceptor {
    public static final String USER_ID = "user_id";
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
            "/",
            "/auth/login",
            "/auth/register",
            "/error"
    );
    private final UserRepository userRepository;

    public SessionInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String endpoint = request.getServletPath();

        if (PUBLIC_ENDPOINTS.contains(endpoint)
                || endpoint.startsWith("/css/")
                || endpoint.startsWith("/js/")
                || endpoint.startsWith("/images/")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("/auth/login");
            return false;
        }


        UUID userId = (UUID) session.getAttribute(USER_ID);
        if (userId == null) {
            session.invalidate();
            response.sendRedirect("/auth/login");
            return false;
        }

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            session.invalidate();
            response.sendRedirect("/auth/login");
            return false;
        }

        if (endpoint.startsWith("/admin") && user.getRole() != Role.ADMIN) {
            response.sendRedirect("/");
            return false;
        }

        request.setAttribute("currentUser", user);

        return true;
    }
}
