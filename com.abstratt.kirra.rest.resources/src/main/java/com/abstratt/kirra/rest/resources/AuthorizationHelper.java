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
		return KirraContext.getInstanceManagement().getEntityCapabilities(entityRef);
	}

	private static InstanceCapabilities getInstanceCapabilities(TypeRef entityRef, String objectId) {
		return KirraContext.getInstanceManagement().getInstanceCapabilities(entityRef, objectId);
	}
	
	private static boolean isLoggedIn() {
		return KirraContext.getInstanceManagement().getCurrentUser() != null;
	}
	
	public static void checkInstanceReadAuthorized(TypeRef entityRef, String objectId) {
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance(); 
		boolean authorized = instanceCapabilities.contains("Read");
		checkAuthorized(authorized);
	}

	private static void checkAuthorized(boolean authorized) {
		ResourceHelper.ensure(authorized, isLoggedIn() ? "User is logged in but does not have permission" : "Action requires permission and user is not logged in", isLoggedIn() ? Status.FORBIDDEN : Status.UNAUTHORIZED);
	}
	
	public static void checkInstanceUpdateAuthorized(TypeRef entityRef, String objectId) {
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance();
		boolean authorized = instanceCapabilities.contains("Update");
		checkAuthorized(authorized);
	}
	
	public static void checkInstanceDeleteAuthorized(TypeRef entityRef, String objectId) {
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> instanceCapabilities = capabilities.getInstance();
		boolean authorized = instanceCapabilities.contains("Delete");
		checkAuthorized(authorized);
	}

	public static void checkInstanceActionAuthorized(TypeRef entityRef, String objectId, String actionName) {
		InstanceCapabilities capabilities = getInstanceCapabilities(entityRef, objectId);
		List<String> actionCapabilities = capabilities.getActions().getOrDefault(actionName, Collections.emptyList());
		boolean authorized = actionCapabilities.contains("Call");
		checkAuthorized(authorized);
	}
	
	public static void checkEntityListAuthorized(TypeRef entityRef) {
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> entityCapabilities = capabilities.getEntity();
		boolean authorized = entityCapabilities.contains("List");
		checkAuthorized(authorized);
	}
	
	public static void checkInstanceCreationAuthorized(TypeRef entityRef) {
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> entityCapabilities = capabilities.getEntity();
		boolean authorized = entityCapabilities.contains("Create");
		checkAuthorized(authorized);
	}

	public static void checkEntityActionAuthorized(TypeRef entityRef, String actionName) {
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> queryCapabilities = capabilities.getActions().getOrDefault(actionName, Collections.emptyList());
		boolean authorized = queryCapabilities.contains("StaticCall");
		checkAuthorized(authorized);
	}

	public static void checkEntityFinderAuthorized(TypeRef entityRef, String finderName) {
		EntityCapabilities capabilities = getEntityCapabilities(entityRef);
		List<String> queryCapabilities = capabilities.getQueries().getOrDefault(finderName, Collections.emptyList());
		boolean authorized = queryCapabilities.contains("StaticCall");
		checkAuthorized(authorized);
	}

}
