package com.abstratt.kirra.rest.resources;

import java.net.URI;

import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.SchemaManagement;

public class KirraContext {
    public static URI getBaseURI() {
        return KirraContext.baseURI.get();
    }

    public static InstanceManagement getInstanceManagement() {
        return KirraContext.instanceManagement.get();
    }

    public static SchemaManagement getSchemaManagement() {
        return KirraContext.schemaManagement.get();
    }

    public static void setBaseURI(URI newValue) {
        KirraContext.setOrClear(KirraContext.baseURI, newValue);
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

    private static ThreadLocal<URI> baseURI = new ThreadLocal<URI>();

    private static ThreadLocal<InstanceManagement> instanceManagement = new ThreadLocal<InstanceManagement>();

    private static ThreadLocal<SchemaManagement> schemaManagement = new ThreadLocal<SchemaManagement>();
}
