package com.abstratt.kirra.rest.common;

import java.net.URI;
import java.util.Map;

public class Index {
    protected String applicationName;
    protected String applicationLabel;
    protected String applicationLogo;
    protected URI entities;
    protected URI entityCapabilities;
    protected URI services;
    protected URI currentUser;
    protected URI session;
    protected Map<String, URI> currentUserRoles;
    protected URI uri;
    protected KirraContext.Options options;
	public String getApplicationName() {
		return applicationName;
	}
	public String getApplicationLabel() {
		return applicationLabel;
	}
	public String getApplicationLogo() {
		return applicationLogo;
	}
	public URI getEntities() {
		return entities;
	}
	public URI getEntityCapabilities() {
		return entityCapabilities;
	}
	public URI getServices() {
		return services;
	}
	public URI getCurrentUser() {
		return currentUser;
	}
	public URI getSession() {
		return session;
	}
	public Map<String, URI> getCurrentUserRoles() {
		return currentUserRoles;
	}
	public URI getUri() {
		return uri;
	}
	public KirraContext.Options getOptions() {
		return options;
	}
}
