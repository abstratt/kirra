package com.abstratt.kirra.rest.resources;

import java.util.function.Supplier;

import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.KirraException.Kind;

public class KirraRestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Status status;
	private final Kind kind;

	public KirraRestException(KirraErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = mapKindToStatus(errorCode.getKind());
		this.kind = errorCode.getKind();
	}

	public KirraRestException(String message, Status status, Throwable cause) {
		super(message, cause);
		this.status = status;
		if (cause instanceof KirraException) {
			this.kind = ((KirraException) cause).getKind();
		} else {
			kind = null;
		}
	}

	public Kind getKind() {
		return kind;
	}

	public static void ensure(boolean condition, Status status, Supplier<String> message) {
		if (!condition) {
			throw new KirraRestException(message.get(), status, null);
		}
	}

	public Status getStatus() {
		return status;
	}

	public static void ensure(boolean condition, KirraErrorCode errorCode) {
		if (!condition) {
			throw new KirraRestException(errorCode);
		}
	}

	private static Status mapKindToStatus(Kind kind) {
		switch (kind) {
		case AUTHORIZATION:
			return Status.FORBIDDEN;
		case AUTHENTICATION:
			return Status.UNAUTHORIZED;
		case VALIDATION:
			return Status.BAD_REQUEST;
		case ELEMENT_NOT_FOUND:
		case OBJECT_NOT_FOUND:
			return Status.NOT_FOUND;
		}
		return Status.BAD_REQUEST;
	}
}
