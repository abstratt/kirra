package com.abstratt.kirra.rest.resources;

import java.net.URI;

import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.SchemaManagement;

public class KirraContext {
	private static ThreadLocal<InstanceManagement> instanceManagement = new ThreadLocal<InstanceManagement>();
	private static ThreadLocal<SchemaManagement> schemaManagement = new ThreadLocal<SchemaManagement>();
    private static ThreadLocal<URI> baseURI = new ThreadLocal<URI>();
    
	public static SchemaManagement getSchemaManagement() {
		return schemaManagement.get();
	}

	public static void setSchemaManagement(SchemaManagement newValue) {
		setOrClear(schemaManagement, newValue);
	}

	public static InstanceManagement getInstanceManagement() {
		return instanceManagement.get();
	}

	public static void setInstanceManagement(InstanceManagement newValue) {
		setOrClear(instanceManagement, newValue);
	}
	
	public static URI getBaseURI() {
		return baseURI.get();
	}

	public static void setBaseURI(URI newValue) {
		setOrClear(baseURI, newValue);
	}
	
	private static <T> void setOrClear(ThreadLocal<T> slot, T newValue) {
		if (newValue == null)
			slot.remove();
		else
			slot.set(newValue);
	}
}
