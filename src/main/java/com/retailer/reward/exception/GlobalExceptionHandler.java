package com.retailer.reward.exception;

import java.util.Map;
import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> handleCustomExceptions(CustomException ex) {
		return ResponseEntity.status(ex.getStatus()).body(ex.getMessage());
	}

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(InvalidArgumentException ex) {
		log.error("Illegal Argument Exception ");
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
