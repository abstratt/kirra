package com.abstratt.kirra.rest.resources;

import javax.ws.rs.core.Response.Status;

public class KirraRestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Status status;
	public KirraRestException(String message, Status status, Throwable cause) {
		super(message, cause);
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
}
