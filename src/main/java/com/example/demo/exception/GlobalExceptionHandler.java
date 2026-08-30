package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ApiError> handleReservationNotFound(ReservationNotFoundException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.FORBIDDEN.value(), "Forbidden", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.FORBIDDEN.value(), "Forbidden", "Access denied", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(err);
    }

    @ExceptionHandler(com.example.demo.exception.AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthentication(com.example.demo.exception.AuthenticationException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        String path = request.getDescription(false).replace("uri=", "");
        ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Validation Failed", message, path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, HttpServletRequest request) {
        // Log the full stack trace for monitoring
        logger.error("Unhandled exception occurred", ex);
        // Do not leak internal exception messages in production responses
        ApiError err = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error", "An unexpected error occurred", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}

