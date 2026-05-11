package WorkflowManager.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${app.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

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

        String redirectURL = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/auth"
                + "?response_type=code"
                + "&client_id=" + clientId
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&scope=openid profile email"
                + "&state=" + state
                + "&code_challenge=" + codeChallenge
                + "&code_challenge_method=S256";
        response.sendRedirect(redirectURL);
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code,
                         @RequestParam String state,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        String savedState = (String) request.getSession().getAttribute("oauth_state");
        String codeVerifier = (String) request.getSession().getAttribute("code_verifier");
        String redirectTo = sanitizeRedirectTo(
                (String) request.getSession().getAttribute("redirect_to")
        );

        if (!state.equals(savedState)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid state");
            return;
        }

        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "authorization_code");
        body.add("client_id",     clientId);
        body.add("client_secret", clientSecret);
        body.add("code",          code);
        body.add("redirect_uri",  redirectUri);
        body.add("code_verifier", codeVerifier);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                tokenUrl, new HttpEntity<>(body, headers), Map.class
        );

        String accessToken  = (String) tokenResponse.getBody().get("access_token");
        String refreshToken = (String) tokenResponse.getBody().get("refresh_token");

        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("access_token", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofMinutes(15))
                        .build().toString()
        );

        response.addHeader(HttpHeaders.SET_COOKIE,
                ResponseCookie.from("refresh_token", refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .sameSite("Strict")
                        .path("/auth/refresh")
                        .maxAge(Duration.ofDays(1))
                        .build().toString()
        );

        request.getSession().removeAttribute("oauth_state");
        request.getSession().removeAttribute("code_verifier");
        request.getSession().removeAttribute("redirect_to");

        response.sendRedirect("http://localhost:5173" + redirectTo);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response) {
        // 1. Call Keycloak's end_session endpoint
        String logoutUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

        // You'd need to store the id_token at login time to pass here
        // Alternatively just revoke the refresh token
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id",     clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", getRefreshTokenFromCookie(request)); // read from cookie

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            restTemplate.postForEntity(logoutUrl, new HttpEntity<>(body, headers), Void.class);
        } catch (Exception e) {
            // Don't fail logout if Keycloak call fails — still clear cookies
        }

        // 2. Clear cookies
        ResponseCookie clearAccess = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(false).path("/").maxAge(0).build();
        ResponseCookie clearRefresh = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(false).path("/auth/refresh").maxAge(0).build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearAccess.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, clearRefresh.toString());

        return ResponseEntity.ok().build();
    }

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String generateState() {
        String state = UUID.randomUUID().toString();
        return state;
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
}
