package com.sutusxxx.commons;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static com.sutusxxx.commons.CookieHelper.*;

@Slf4j
@Component
public class CookieTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String accessToken = null;

        if (request.getCookies() != null) {
            accessToken = extractCookie(request, "access_token");
        }

        if (accessToken != null && !isTokenExpired(accessToken)) {
            filterChain.doFilter(wrapWithBearer(request, accessToken), response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth/");
    }

    private HttpServletRequest wrapWithBearer(HttpServletRequest request, String token) {
        return new HttpServletRequestWrapper(request) {
            @Override
            public String getHeader(String name) {
                if ("Authorization".equalsIgnoreCase(name)) return "Bearer " + token;
                return super.getHeader(name);
            }
        };
    }

    private boolean isTokenExpired(String token) {
        try {
            String payload = token.split("\\.")[1];
            String json = new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
            Map<String, Object> claims = new ObjectMapper().readValue(json, new TypeReference<>() {
            });
            long exp = ((Number) claims.get("exp")).longValue() * 1000;
            return System.currentTimeMillis() > exp;
        } catch (Exception e) {
            return true;
        }
    }
}