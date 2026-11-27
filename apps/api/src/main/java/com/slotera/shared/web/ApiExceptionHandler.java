package com.slotera.shared.web;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException exception) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "La solicitud contiene campos inválidos.");
        List<Map<String, String>> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of("field", error.getField(), "message", "Valor inválido"))
                .toList();
        problem.setProperty("fields", fields);
        return problem;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, InvalidRequestException.class,
            MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail invalidRequest(Exception exception) {
        String detail = exception instanceof InvalidRequestException ? exception.getMessage() : "El cuerpo de la solicitud no es válido.";
        return problem(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", detail);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> methodNotAllowed(HttpRequestMethodNotSupportedException exception) {
        ProblemDetail problem = problem(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "El método no está permitido para este recurso.");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(exception.getHeaders()).body(problem);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail notFound() {
        return problem(HttpStatus.NOT_FOUND, "NOT_FOUND", "No se encontró el recurso solicitado.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail forbidden() {
        return problem(HttpStatus.FORBIDDEN, "FORBIDDEN", "No tienes permiso para realizar esta acción.");
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail unexpected(Exception exception) {
        if (exception instanceof ErrorResponse response) {
            HttpStatus status = HttpStatus.resolve(response.getStatusCode().value());
            if (status != null && status.is4xxClientError()) {
                return problem(status, "HTTP_" + status.value(), "La solicitud no pudo procesarse.");
            }
        }
        log.error("No se pudo procesar una solicitud por un error interno");
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Ocurrió un error interno.");
    }

    private ProblemDetail problem(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("/problems/" + code.toLowerCase().replace('_', '-')));
        problem.setProperty("code", code);
        return problem;
    }
}
