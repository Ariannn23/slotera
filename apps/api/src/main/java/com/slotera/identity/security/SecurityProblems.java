package com.slotera.identity.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class SecurityProblems {

    private final ObjectMapper objectMapper;

    public SecurityProblems(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void unauthenticated(HttpServletRequest request, HttpServletResponse response,
                                AuthenticationException exception) throws IOException {
        response.setHeader("WWW-Authenticate", "Bearer");
        write(request, response, 401, "UNAUTHENTICATED", "Se requiere una sesión válida.");
    }

    public void forbidden(HttpServletRequest request, HttpServletResponse response,
                          AccessDeniedException exception) throws IOException {
        write(request, response, 403, "FORBIDDEN", "No tienes permiso para realizar esta acción.");
    }

    private void write(HttpServletRequest request, HttpServletResponse response, int status,
                       String code, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("type", "/problems/" + code.toLowerCase());
        body.put("title", status == 401 ? "Unauthorized" : "Forbidden");
        body.put("status", status);
        body.put("detail", detail);
        body.put("instance", request.getRequestURI());
        body.put("code", code);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
