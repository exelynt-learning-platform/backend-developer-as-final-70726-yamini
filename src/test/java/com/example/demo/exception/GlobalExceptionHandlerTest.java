package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleNotFound_Returns404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource 1 not found");
        ResponseEntity<ApiError> response = exceptionHandler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Resource 1 not found");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
    }

    @Test
    void handleReservationNotFound_Returns404() {
        ReservationNotFoundException ex = new ReservationNotFoundException("Reservation 10 not found");
        ResponseEntity<ApiError> response = exceptionHandler.handleReservationNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("Reservation 10 not found");
    }

    @Test
    void handleUserNotFound_Returns404() {
        UserNotFoundException ex = new UserNotFoundException("User not found");
        ResponseEntity<ApiError> response = exceptionHandler.handleUserNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleUnauthorized_Returns403() {
        UnauthorizedException ex = new UnauthorizedException("Access denied");
        ResponseEntity<ApiError> response = exceptionHandler.handleUnauthorized(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    @Test
    void handleAuthentication_Returns401() {
        AuthenticationException ex = new AuthenticationException("Bad credentials");
        ResponseEntity<ApiError> response = exceptionHandler.handleAuthentication(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Bad credentials");
    }

    @Test
    void handleBadRequest_Returns400() {
        BadRequestException ex = new BadRequestException("Invalid input");
        ResponseEntity<ApiError> response = exceptionHandler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid input");
    }

    @Test
    void handleValidation_Returns400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "email", "must be a well-formed email address");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        WebRequest webRequest = mock(WebRequest.class);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");

        ResponseEntity<Object> response = exceptionHandler.handleValidation(ex, webRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ApiError.class);
        ApiError err = (ApiError) response.getBody();
        assertThat(err.getMessage()).contains("must be a well-formed email address");
    }

    @Test
    void handleAll_Returns500() {
        Exception ex = new RuntimeException("Unexpected error");
        ResponseEntity<ApiError> response = exceptionHandler.handleAll(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
    }
}
