package io.github.janmiesiak.productimportapi.product;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductImportException.class)
    public ResponseEntity<ApiErrorResponse> handleProductImportException(
            ProductImportException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                exception.getRowNumber(),
                exception.getField()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProductNotFoundException(
            ProductNotFoundException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                null,
                null
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiErrorResponse> handleIOException(
            IOException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                "Could not read uploaded file",
                request.getRequestURI(),
                null,
                null
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
    @ExceptionHandler(InvalidProductSearchException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidProductSearchException(
            InvalidProductSearchException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                null,
                "name"
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
