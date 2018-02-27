package com.abstratt.kirra.rest.resources;

public class ErrorDTO {
	private String message;

	public ErrorDTO() {
	}

	public ErrorDTO(String message) {
		super();
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
