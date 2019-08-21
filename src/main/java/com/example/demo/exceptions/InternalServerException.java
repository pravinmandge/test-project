package com.example.demo.exceptions;

public class InternalServerException extends Exception {
	private static final long serialVersionUID = 1L;
	private String errorMessage;

	public String getErrorMessage() {
		return errorMessage;
	}

	public InternalServerException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	public InternalServerException() {
		super();
	}
}
