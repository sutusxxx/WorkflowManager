package com.sutusxxx.auth;

import com.sutusxxx.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import static com.sutusxxx.commons.CookieHelper.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final KeycloakService keycloakService;

    public AuthController(UserService userService, KeycloakService keycloakService) {
        this.userService = userService;
        this.keycloakService = keycloakService;
    }

    @GetMapping("/login")
    public void login(
            @RequestParam(required = false, defaultValue = "/") String redirectTo,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        String state = generateState();
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        request.getSession().setAttribute("oauth_state", state);
        request.getSession().setAttribute("code_verifier", codeVerifier);
        request.getSession().setAttribute("redirect_to", redirectTo);

        log.debug("[AUTH] redirecting to authorization URL with state '{}' and codeChallenge '{}'", state, codeChallenge);
        response.sendRedirect(keycloakService.buildAuthorizationUrl(state, codeChallenge));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String savedState = (String) request.getSession().getAttribute("oauth_state");
        String codeVerifier = (String) request.getSession().getAttribute("code_verifier");
        String redirectTo = sanitizeRedirectTo((String) request.getSession().getAttribute("redirect_to"));

        if (!state.equals(savedState)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state");
            return;
        }

        request.getSession().invalidate();

        log.debug("[AUTH] exchanging code '{}' with codeVerifier '{}'", code, codeVerifier);
        Map<String, String> tokens = keycloakService.exchangeCode(code, codeVerifier);

        log.debug("[AUTH] synchronise user...");
        userService.syncUser(decodeJwtClaims(tokens.get("access_token")));

        setCookieTokens(response, tokens);

        request.getSession().removeAttribute("oauth_state");
        request.getSession().removeAttribute("code_verifier");
        request.getSession().removeAttribute("redirect_to");

        String redirectURL = "http://localhost:5173" + redirectTo;
        log.debug("[AUTH] redirecting to {}", redirectURL);
        response.sendRedirect(redirectURL);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refresh_token");
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No refresh token");
        }

        try {
            Map<String, String> tokens = keycloakService.refreshTokens(refreshToken);
            setCookieTokens(response, tokens);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("[AUTH] Refresh token expired or invalid: {}", e.getMessage());
            clearCookieTokens(response);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Session expired");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refresh_token");

        if (refreshToken != null) {
            keycloakService.revokeToken(refreshToken);
        }
        clearCookieTokens(response);
        return ResponseEntity.ok().build();
    }

    private String generateState() {
        return UUID.randomUUID().toString();
    }

    private String generateCodeVerifier() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    private String sanitizeRedirectTo(String redirectTo) {
        // Only allow relative paths starting with /
        // Reject anything that looks like an external URL
        if (redirectTo == null || !redirectTo.startsWith("/") || redirectTo.startsWith("//")) {
            return "/";
        }
        return redirectTo;
    }

    private Map<String, Object> decodeJwtClaims(String token) {
        String payload = token.split("\\.")[1];
        byte[] decoded = Base64.getUrlDecoder().decode(payload);
        String json = new String(decoded, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode JWT claims", e);
        }
    }
}
