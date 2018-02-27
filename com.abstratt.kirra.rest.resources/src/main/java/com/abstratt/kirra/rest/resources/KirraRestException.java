package com.abstratt.kirra.rest.resources;

import java.util.function.Supplier;

import javax.ws.rs.core.Response.Status;

public class KirraRestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final Status status;

    public KirraRestException(String message, Status status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
    
    public static void ensure(boolean condition, Status status, Supplier<String> message) {
    	if (!condition) {
    		throw new KirraRestException(message.get(), status, null);
    	}
    }

    public Status getStatus() {
        return status;
    }
}
