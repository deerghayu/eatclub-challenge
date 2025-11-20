package com.eatclub.challenge.exception;

import com.eatclub.challenge.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/test");
    }

    @Test
    void handleRestaurantDataException_returnsServiceUnavailable() {
        RestaurantDataException exception = new RestaurantDataException("Failed to fetch data");

        ResponseEntity<ErrorResponse> response = handler.handleRestaurantDataException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(503);
        assertThat(response.getBody().getError()).isEqualTo("Restaurant Data Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Failed to fetch data");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/test");
    }

    @Test
    void handleInvalidTimeFormatException_returnsBadRequest() {
        InvalidTimeFormatException exception = new InvalidTimeFormatException("Invalid time format");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidTimeFormatException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Invalid Time Format");
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid time format");
    }

    @Test
    void handlePeakTimeCalculationException_returnsInternalServerError() {
        PeakTimeCalculationException exception = new PeakTimeCalculationException("Calculation failed");

        ResponseEntity<ErrorResponse> response = handler.handlePeakTimeCalculationException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Peak Time Calculation Error");
    }

    @Test
    void handleMethodArgumentTypeMismatch_returnsBadRequestWithDetails() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalid", Integer.class, "id", null, null);

        ResponseEntity<ErrorResponse> response = handler.handleMethodArgumentTypeMismatch(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("must be of type Integer");
    }

    @Test
    void handleWebClientResponseException_returnsServiceUnavailable() {
        WebClientResponseException exception = WebClientResponseException.create(
                503, "Service Unavailable", null, null, null);

        ResponseEntity<ErrorResponse> response = handler.handleWebClientResponseException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Failed to fetch restaurant data");
    }

    @Test
    void handleWebClientException_returnsServiceUnavailable() {
        WebClientException exception = new WebClientException("Connection refused") {
        };

        ResponseEntity<ErrorResponse> response = handler.handleWebClientException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("Unable to connect");
    }

    @Test
    void handleIllegalArgumentException_returnsBadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid parameter");

        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid parameter");
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception exception = new Exception("Unexpected error");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).contains("unexpected error occurred");
    }

    @Test
    void errorResponse_includesTimestamp() {
        RestaurantDataException exception = new RestaurantDataException("Test");

        ResponseEntity<ErrorResponse> response = handler.handleRestaurantDataException(exception, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}
