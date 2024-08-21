package com.example.elasticsearchSpring.exception;

import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleArgumentExceptions_withMethodArgumentNotValidException_shouldReturnBadRequest() {
        // Setup mock
        FieldError fieldError = new FieldError("objectName", "fieldName", "Default message");
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        // Execute
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleArgumentExceptions(ex);

        // Verify
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("fieldName", "Default message");
    }

    @Test
    void handleArgumentExceptions_withGenericException_shouldReturnBadRequest() {
        // Setup
        Exception ex = new Exception("An error occurred");

        // Execute
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleArgumentExceptions(ex);

        // Verify
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("message", "An error occurred");
    }
}
