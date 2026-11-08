package com.example.cafediary.rag;

import org.springframework.http.HttpStatus;

public class RagServiceException extends RuntimeException {
	private final HttpStatus status;

	public RagServiceException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
