package com.example.netflix_clone.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(BadCredentialException.class)
    public ResponseEntity<Map<String, Object>> handleCredentialException(BadCredentialException e) {
        log.warn("BadCredentialException: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(AccountDeactivatedException.class)
    public ResponseEntity<Map<String, Object>> handleAccountDeactivatedException(AccountDeactivatedException e) {
        log.warn("AccountDeactivatedException: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<Map<String, Object>> handleEmailNotVerified(EmailNotVerifiedException e) {
        log.warn("EmailNotVerified: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }
    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<Map<String, Object>> handleEmailSendingException(EmailSendingException e) {
        log.warn("EmailSendingException: {}", e.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn("EmailAlreadyExistsException: {}", e.getMessage());
        return buildResponse(HttpStatus.CONFLICT, e.getMessage());
    }
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCredentialException(InvalidCredentialException e) {
        log.warn("InvalidCredentialException: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRoleException(InvalidRoleException e) {
        log.warn("InvalidRoleException: {}", e.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException e) {
        log.warn("InvalidTokenException: {}", e.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("ResourceNotFoundException: {}", e.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map((DefaultMessageSourceResolvable::getDefaultMessage))
                .orElse("Invalid request");

        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(Map.of("timestamp", Instant.now(), "message", message));
    }

    @ExceptionHandler({AsyncRequestNotUsableException.class, ClientAbortException.class})
    public void handleAsyncRequestNotUsableException(Exception e) {
        log.debug("Client closed connection during streaming (expected for video seeking/buffering): {}",e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        log.warn("Exception: {}", e.getMessage(), e);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = Map.of("timestamp", Instant.now(), "error",message);
        return ResponseEntity.status(status).body(body);
    }
}
