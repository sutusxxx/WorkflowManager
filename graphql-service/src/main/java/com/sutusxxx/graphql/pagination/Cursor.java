package com.sutusxxx.graphql.pagination;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record Cursor(String id) {
    public static Cursor decode(String base64) {
        String decoded = new String(Base64.getDecoder().decode(base64));
        return new Cursor(decoded);
    }

    public String encode() {
        return Base64.getEncoder().encodeToString(id.getBytes(StandardCharsets.UTF_8));
    }
}
