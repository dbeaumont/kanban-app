package com.example.bff.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
public class DownstreamExceptionHandler {

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<String> handleWebClientResponseException(WebClientResponseException ex) {
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(ex.getStatusCode());
        MediaType contentType = ex.getHeaders().getContentType();
        if (contentType != null) {
            builder.contentType(contentType);
        }
        String body = ex.getResponseBodyAsString();
        if (body == null || body.isBlank()) {
            body = ex.getMessage();
        }
        return builder.body(body);
    }

    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<String> handleWebClientRequestException(WebClientRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .contentType(MediaType.TEXT_PLAIN)
                .body("Downstream call failed: " + ex.getMessage());
    }
}
