package com.sutusxxx.commons;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    // Get the raw keycloak ID (sub claim)
    public String getCurrentUserKeycloakId() {
        return getJwt().getSubject();
    }

    // Get the username (preferred_username claim)
    public String getCurrentUsername() {
        return getJwt().getClaim("preferred_username");
    }

    // Get any claim you want
    public <T> T getClaim(String claim) {
        return getJwt().getClaim(claim);
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken();
        }

        throw new IllegalStateException("No JWT authentication found in SecurityContext");
    }
}
