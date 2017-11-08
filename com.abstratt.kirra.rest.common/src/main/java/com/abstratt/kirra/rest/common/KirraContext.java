package com.abstratt.kirra.rest.common;

import java.io.File;
import java.net.URI;
import java.util.List;

import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.KirraApplication;
import com.abstratt.kirra.SchemaManagement;

public class KirraContext {

    public static URI getBaseURI() {
        return KirraContext.baseURI.get();
    }

    public static KirraApplication getApplication() {
        return KirraContext.application.get();
    }

    public static InstanceManagement getInstanceManagement() {
        return KirraContext.instanceManagement.get();
    }

    public static SchemaManagement getSchemaManagement() {
        return KirraContext.schemaManagement.get();
    }

    public static String getEnvironment() {
    	return KirraContext.environment.get();
    }
    public static Options getOptions() {
    	return KirraContext.options.get();
    }

    public static List<Upload> getUploads() {
    	return KirraContext.uploads.get();
    }
    public static void setBaseURI(URI newValue) {
        KirraContext.setOrClear(KirraContext.baseURI, newValue);
    }
    
    public static void setEnvironment(String newValue) {
        KirraContext.setOrClear(KirraContext.environment, newValue);
    }
    
    public static void setUploads(List<Upload> newValue) {
        KirraContext.setOrClear(KirraContext.uploads, newValue);
    }
    
    public static void setOptions(Options newValue) {
        KirraContext.setOrClear(KirraContext.options, newValue);
    }

    public static void setApplication(KirraApplication newValue) {
        KirraContext.setOrClear(KirraContext.application, newValue);
    }

    public static void setInstanceManagement(InstanceManagement newValue) {
        KirraContext.setOrClear(KirraContext.instanceManagement, newValue);
    }

    public static void setSchemaManagement(SchemaManagement newValue) {
        KirraContext.setOrClear(KirraContext.schemaManagement, newValue);
    }

    private static <T> void setOrClear(ThreadLocal<T> slot, T newValue) {
        if (newValue == null)
            slot.remove();
        else
            slot.set(newValue);
    }
    
    public static class Upload {
    	private final String name;
    	private final File contents;
		private final String contentType;
		private final String originalName;
		public Upload(String name, String contentType, String originalName, File contents) {
			super();
			this.name = name;
			this.contents = contents;
			this.contentType = contentType;
			this.originalName = originalName;
		}
		public String getName() {
			return name;
		}
		public File getContents() {
			return contents;
		}
		public String getContentType() {
			return contentType;
		}
		public String getOriginalName() {
			return originalName;
		}
    }
    
    public static class Options {
    	public Options(boolean isLoginRequired, boolean isLoginAllowed) {
			this.isLoginRequired = isLoginRequired;
			this.isLoginAllowed = isLoginAllowed;
		}
		boolean isLoginRequired;
    	boolean isLoginAllowed;
		public boolean isLoginRequired() {
			return isLoginRequired;
		}
		public boolean isLoginAllowed() {
			return isLoginAllowed;
		}
    }
    private static ThreadLocal<KirraApplication> application = new ThreadLocal<>();
    
    private static ThreadLocal<Options> options = new ThreadLocal<>();
    
    private static ThreadLocal<List<Upload>> uploads = new ThreadLocal<>();
    
    private static ThreadLocal<String> environment = new ThreadLocal<>();
    
    private static ThreadLocal<URI> baseURI = new ThreadLocal<>();

    private static ThreadLocal<InstanceManagement> instanceManagement = new ThreadLocal<>();

    private static ThreadLocal<SchemaManagement> schemaManagement = new ThreadLocal<>();

}
