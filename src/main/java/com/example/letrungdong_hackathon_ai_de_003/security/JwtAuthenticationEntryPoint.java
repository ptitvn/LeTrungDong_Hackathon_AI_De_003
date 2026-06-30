package com.example.letrungdong_hackathon_ai_de_003.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String errorCode;
        String message;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            errorCode = "MISSING_TOKEN";
            message = "Authorization header is required. Please provide a valid Bearer token.";
        } else if (!authHeader.startsWith("Bearer ")) {
            errorCode = "INVALID_TOKEN_FORMAT";
            message = "Authorization header must start with 'Bearer '.";
        } else {
            errorCode = "INVALID_OR_EXPIRED_TOKEN";
            message = "The provided token is invalid, expired, or has been tampered with.";
        }

        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", errorCode);
        errorBody.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}
