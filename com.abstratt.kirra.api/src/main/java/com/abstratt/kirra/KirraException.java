package com.abstratt.kirra;

public class KirraException extends RuntimeException {
    public static enum Kind {
        ELEMENT_NOT_FOUND, ENTITY, EXTERNAL, OBJECT_NOT_FOUND, SCHEMA, VALIDATION
    }

    private static final long serialVersionUID = 1L;

    private String context;

    private Kind kind;

    public KirraException(String message, Kind kind) {
        this(message, null, kind, null);
    }

    public KirraException(String message, Throwable cause, Kind kind) {
        this(message, cause, kind, null);
    }

    public KirraException(String message, Throwable cause, Kind kind, String context) {
        super(message, cause);
        this.kind = kind;
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public Kind getKind() {
        return kind;
    }

    @Override
    public String getMessage() {
        if (context != null)
            return super.getMessage() + " - " + context;
        return super.getMessage();
    }
}
