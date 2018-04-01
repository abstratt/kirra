package com.abstratt.kirra.rest.resources;

import java.util.List;
import java.util.function.Supplier;

import com.abstratt.kirra.EntityCapabilities;
import com.abstratt.kirra.InstanceCapabilities;
import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.KirraContext;

public class AuthorizationHelper {
	public static void checkInstanceReadAuthorized(TypeRef entityRef, String objectId) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkInstanceReadAuthorized(entityRef, objectId);
	}

	public static void checkInstanceUpdateAuthorized(TypeRef entityRef, String objectId) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkInstanceUpdateAuthorized(entityRef,
				objectId);
	}

	public static void checkInstanceDeleteAuthorized(TypeRef entityRef, String objectId) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkInstanceDeleteAuthorized(entityRef,
				objectId);
	}

	public static void checkInstanceActionAuthorized(TypeRef entityRef, String objectId, String actionName) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkInstanceActionAuthorized(entityRef,
				objectId, actionName);
	}

	public static void checkEntityListAuthorized(TypeRef entityRef) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkEntityListAuthorized(entityRef);
	}

	public static void checkInstanceCreationAuthorized(TypeRef entityRef) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkInstanceCreationAuthorized(entityRef);
	}

	public static void checkEntityActionAuthorized(TypeRef entityRef, String actionName) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkEntityActionAuthorized(entityRef,
				actionName);
	}

	public static void checkEntityQueryAuthorized(TypeRef entityRef, String finderName) {
		KirraContext.getInstanceManagement().getAuthorizationHandler().checkEntityQueryAuthorized(entityRef,
				finderName);
	}
}
