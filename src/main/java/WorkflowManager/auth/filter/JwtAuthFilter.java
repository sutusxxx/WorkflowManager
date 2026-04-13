package WorkflowManager.auth.filter;

import WorkflowManager.auth.JwtUtility;
import WorkflowManager.common.model.ErrorResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtUtility jwtUtility;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(UserDetailsService userDetailsService, JwtUtility jwtUtility, ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.jwtUtility = jwtUtility;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromCookies(request);
            String username = null;
            if (!Objects.isNull(token)) {
                username = jwtUtility.extractUsername(token);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtility.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            sendError(response, HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", "Your session has expired, please log in again");
        } catch (JwtException ex) {
            sendError(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Invalid token");
        }
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String errorCode, String message) throws IOException {
        ErrorResponseDTO error = new ErrorResponseDTO();
        error.setStatus(status.value());
        error.setErrorCode(errorCode);
        error.setMessage(message);
        error.setTimestamp(LocalDateTime.now());

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (!Objects.isNull(cookies)) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("session_token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
