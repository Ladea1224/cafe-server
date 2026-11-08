package com.example.cafediary.cafe;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.cafediary.rag.RagServiceException;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(CafeNotFoundException.class)
	public ResponseEntity<Map<String, String>> handleNotFound(CafeNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
	}

	@ExceptionHandler(InvalidPhotoException.class)
	public ResponseEntity<Map<String, String>> handleInvalidPhoto(InvalidPhotoException exception) {
		return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
	}

	@ExceptionHandler(RagServiceException.class)
	public ResponseEntity<Map<String, String>> handleRagService(RagServiceException exception) {
		return ResponseEntity.status(exception.getStatus()).body(Map.of("error", exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.findFirst().map(error -> error.getDefaultMessage()).orElse("Request validation failed.");
		return ResponseEntity.badRequest().body(Map.of("error", message));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, String>> handleUnreadableBody(HttpMessageNotReadableException exception) {
		return ResponseEntity.badRequest().body(Map.of("error", "Request body must be valid JSON."));
	}
}
