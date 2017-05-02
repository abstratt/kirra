package com.abstratt.kirra.rest.resources;

public class ResourceLogUtils {
    private static final String ERROR = "Error";
    private static final String WARNING = "Warning";
    private static final String INFORMATION = "Info";
    private static final String DEBUG = "Debug";

    public static void log(String severity, String message, Throwable throwable) {
        System.err.println(severity + " - " + message);
        if (throwable != null)
        	throwable.printStackTrace();
    }

    public static void logError(String message, Throwable e) {
        log(ERROR, message, e);
    }

    public static void logWarning(String message, Throwable e) {
        log(WARNING, message, e);
    }

    public static void logInfo(String message, Throwable e) {
        log(INFORMATION, message, e);
    }

    public static void debug(String message) {
        if (Boolean.getBoolean(ResourceLogUtils.class.getPackage().getName()+".debug"))
        	log(DEBUG, message, null);
    }

}
