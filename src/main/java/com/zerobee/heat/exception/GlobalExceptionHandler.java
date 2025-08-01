package com.zerobee.heat.exception;

import com.zerobee.heat.dto.CustomResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    //Incorrect Password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        CustomResponse<Object> response = new CustomResponse<>(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    //JWTs Exception
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<CustomResponse<Void>> handleExpiredJwtException(ExpiredJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED, "JWT token has expired", null));
    }
    
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<CustomResponse<Void>> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomResponse<>(HttpStatus.BAD_REQUEST, "Invalid JWT token", null));
    }
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<CustomResponse<Void>> handleSignatureException(SignatureException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new CustomResponse<>(HttpStatus.UNAUTHORIZED, "JWT signature validation failed", null));
    }
    
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CustomResponse<Void>> handleConflictException(ConflictException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomResponse<>(HttpStatus.CONFLICT, ex.getMessage(), null));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomResponse<>(HttpStatus.CONFLICT, ex.getMessage(), null));
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new CustomResponse<>(HttpStatus.NOT_FOUND, ex.getMessage(), null));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CustomResponse<>(HttpStatus.BAD_REQUEST, "Validation Failed", errors));
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message;
        if (ex.getMessage().contains("files_itinerary_id_key")) message = "File with the itinerary id already exists!";
        else message = ex.getMessage();
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new CustomResponse<>(HttpStatus.CONFLICT, message, null));
    }
    
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<CustomResponse<Void>> handleAccessDenied(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new CustomResponse<>(HttpStatus.FORBIDDEN, "Access denied: You don't have permission to perform this operation", null));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CustomResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage(), null));
    }
}
