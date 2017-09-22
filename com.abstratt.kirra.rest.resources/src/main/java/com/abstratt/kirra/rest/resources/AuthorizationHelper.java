package com.abstratt.kirra.rest.resources;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.EntityCapabilities;
import com.abstratt.kirra.InstanceCapabilities;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.KirraContext;

public class AuthorizationHelper {
	private static EntityCapabilities getEntityCapabilities(TypeRef entityRef) {
		EntityCapabilities entityCapabilities = KirraContext.getInstanceManagement().getEntityCapabilities(entityRef);
		checkAuthorized(entityCapabilities != null);
        return entityCapabilities;
	}
	
	private static boolean isRestricted() {
	    return KirraContext.getInstanceManagement().isRestricted();
	}

	private static InstanceCapabilities getInstanceCapabilities(TypeRef entityRef, String objectId) {
		InstanceCapabilities instanceCapabilities = KirraContext.getInstanceManagement().getInstanceCapabilities(entityRef, objectId);
		checkAuthorized(instanceCapabilities != null);
        return instanceCapabilities;
	}
	
	private static boolean isLoggedIn() {
		return KirraContext.getInstanceManagement().getCurrentUser() != null;
	}
	
	private static void checkAuthorized(boolean authorized) {
	    ResourceHelper.ensure(authorized, isLoggedIn() ? "User is logged in but does not have permission" : "Action requires permission and user is not logged in", isLoggedIn() ? Status.FORBIDDEN : Status.UNAUTHORIZED);
	}

	public static void checkInstanceReadAuthorized(TypeRef entityRef, String objectId) {
	    if (!isRestricted())
            return;
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance(); 
		boolean authorized = instanceCapabilities.contains("Read");
		checkAuthorized(authorized);
	}

	public static void checkInstanceUpdateAuthorized(TypeRef entityRef, String objectId) {
	    if (!isRestricted())
            return;
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance();
		boolean authorized = instanceCapabilities.contains("Update");
		checkAuthorized(authorized);
	}
	
	public static void checkInstanceDeleteAuthorized(TypeRef entityRef, String objectId) {
	    if (!isRestricted())
            return;
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance();
		boolean authorized = instanceCapabilities.contains("Delete");
		checkAuthorized(authorized);
	}

	public static void checkInstanceActionAuthorized(TypeRef entityRef, String objectId, String actionName) {
	    if (!isRestricted())
            return;
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> actionCapabilities = capabilities.getActions().getOrDefault(actionName, Collections.emptyList());
		boolean authorized = actionCapabilities.contains("Call");
		checkAuthorized(authorized);
	}
	
	public static void checkEntityListAuthorized(TypeRef entityRef) {
	    if (!isRestricted())
	        return;
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> entityCapabilities = capabilities.getEntity();
		boolean authorized = entityCapabilities.contains("List");
		checkAuthorized(authorized);
	}
	
	public static void checkInstanceCreationAuthorized(TypeRef entityRef) {
	    if (!isRestricted())
            return;
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> entityCapabilities = capabilities.getEntity();
		boolean authorized = entityCapabilities.contains("Create");
		checkAuthorized(authorized);
	}

	public static void checkEntityActionAuthorized(TypeRef entityRef, String actionName) {
	    if (!isRestricted())
            return;
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> queryCapabilities = capabilities.getActions().getOrDefault(actionName, Collections.emptyList());
		boolean authorized = queryCapabilities.contains("StaticCall");
		checkAuthorized(authorized);
	}

	public static void checkEntityFinderAuthorized(TypeRef entityRef, String finderName) {
	    if (!isRestricted())
            return;
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> queryCapabilities = capabilities.getQueries().getOrDefault(finderName, Collections.emptyList());
		boolean authorized = queryCapabilities.contains("StaticCall");
		checkAuthorized(authorized);
	}

}
