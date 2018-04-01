package com.abstratt.kirra;

import java.util.function.Supplier;

public class KirraException extends RuntimeException {
    public static enum Kind {
        ELEMENT_NOT_FOUND, ENTITY, EXTERNAL, OBJECT_NOT_FOUND, SCHEMA, VALIDATION, AUTHORIZATION, INTERNAL, AUTHENTICATION
    }

    private static final long serialVersionUID = 1L;

    private String context;

    private Kind kind;

    private String symbol;
    
    public static void ensure(boolean condition, Kind kind, Supplier<String> message) {
    	if (!condition) {
    		throw new KirraException(message.get(), kind);
    	}
    }
    
    public static void ensure(boolean condition, KirraErrorCode errorCode) {
    	if (!condition) {
    		throw new KirraException(errorCode.getMessage(), errorCode.getKind());
    	}
    }

    public KirraException(String message, Kind kind) {
        this(message, null, kind, null, null);
    }

    public KirraException(String message, Throwable cause, Kind kind) {
        this(message, cause, kind, null, null);
    }

    public KirraException(String message, Throwable cause, Kind kind, String context, String symbol) {
        super(message, cause);
        this.kind = kind;
        this.context = context;
        this.symbol = symbol;
    }

    public String getContext() {
        return context;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    public String getSymbol() {
        return symbol;
    }
}
