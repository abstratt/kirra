package com.abstratt.kirra;

public class KirraException extends RuntimeException {
    public static enum Kind {
        ELEMENT_NOT_FOUND, ENTITY, EXTERNAL, OBJECT_NOT_FOUND, SCHEMA, VALIDATION
    }

    private static final long serialVersionUID = 1L;

    private String context;

    private Kind kind;

    private String symbol;

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
