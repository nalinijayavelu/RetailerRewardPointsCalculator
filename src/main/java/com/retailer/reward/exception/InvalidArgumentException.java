package com.retailer.reward.exception;

import org.springframework.http.HttpStatus;

public class InvalidArgumentException extends RuntimeException {
	private final HttpStatus status;

    public InvalidArgumentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public InvalidArgumentException(String message) {
        this(message, HttpStatus.BAD_REQUEST);
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
}

