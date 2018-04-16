package com.abstratt.kirra.rest.common;

import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.KirraException.Kind;

public class ErrorDTO {
	private String message;
	private String errorCode;
	private Kind kind;

	public ErrorDTO() {
	}

	public ErrorDTO(KirraErrorCode errorCode) {
		this.errorCode = errorCode.name();
		this.kind = errorCode.getKind();
		this.message = errorCode.getMessage();
	}
	public ErrorDTO(String message) {
		super();
		this.message = message;
	}
	
	public ErrorDTO(String message, Kind kind) {
		super();
		this.message = message;
		this.kind = kind;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}
	
	public Kind getKind() {
		return kind;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
