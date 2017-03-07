package com.abstratt.kirra;

import java.net.URI;
import java.util.Properties;

/**
 * This API allows manipulation of both entity data and metadata.
 */
public interface Repository extends SchemaManagement, InstanceManagement {
    public void dispose();

    public Properties getProperties();

    public URI getRepositoryURI();

    public void initialize();

    public boolean isFiltering();

    public boolean isOpen();

    public boolean isValidating();

    public void setFiltering(boolean filtering);

    // REPOSITORY CONFIGURATION
    /**
     * Determines the data repository this instance should connect to.
     */
    public void setRepositoryURI(URI uri) throws KirraException;

    public void setValidating(boolean isValidating);
    
    public default void setPopulating(boolean isInitializing) {
    	
    }
    
    public default boolean isPopulating() {
    	return false;
    }
}