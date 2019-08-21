package com.example.demo.exceptions;

public class NotAuthorizedException extends Exception {
	private static final long serialVersionUID = 1L;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public NotAuthorizedException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public NotAuthorizedException() {
		super();
	}
}
