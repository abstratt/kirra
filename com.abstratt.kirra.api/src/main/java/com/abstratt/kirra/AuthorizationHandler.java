package com.abstratt.kirra;

import java.util.List;
import java.util.function.Supplier;

public class AuthorizationHandler {
	private InstanceManagement instanceManagement;

	public AuthorizationHandler(InstanceManagement instanceManagement) {
		super();
		this.instanceManagement = instanceManagement;
	}

	private Supplier<List<String>> supplyEntityCapabilities(TypeRef entityRef) {
		return () -> getEntityCapabilities(entityRef).getEntity();
	}

	private Supplier<List<String>> supplyEntityQueryCapabilities(TypeRef entityRef, String queryName) {
		return () -> getEntityCapabilities(entityRef).getQueries().get(queryName);
	}

	private Supplier<List<String>> supplyEntityActionCapabilities(TypeRef entityRef, String actionName) {
		return () -> getEntityCapabilities(entityRef).getActions().get(actionName);
	}

	private Supplier<List<String>> supplyInstanceCapabilities(TypeRef entityRef, String objectId) {
		return () -> getInstanceCapabilities(entityRef, objectId).getInstance();
	}

	private Supplier<List<String>> supplyInstanceActionCapabilities(TypeRef entityRef, String objectId,
			String actionName) {
		return () -> getInstanceCapabilities(entityRef, objectId).getActions().get(actionName);
	}

	private EntityCapabilities getEntityCapabilities(TypeRef entityRef) {
		EntityCapabilities entityCapabilities = instanceManagement.getEntityCapabilities(entityRef);
		checkAuthorized(entityCapabilities != null);
		return entityCapabilities;
	}

	private InstanceCapabilities getInstanceCapabilities(TypeRef entityRef, String objectId) {
		InstanceCapabilities instanceCapabilities = instanceManagement.getInstanceCapabilities(entityRef, objectId);
		checkAuthorized(instanceCapabilities != null);
		return instanceCapabilities;
	}

	private boolean isRestricted() {
		return instanceManagement.isRestricted();
	}

	private boolean isLoggedIn() {
		return instanceManagement.getCurrentUser() != null;
	}

	private void checkAuthorized(boolean authorized) {
		KirraException.ensure(authorized,
				isLoggedIn() ? KirraErrorCode.NO_AUTHORIZATION : KirraErrorCode.AUTHENTICATION_REQUIRED);
	}

	private void checkCapability(Supplier<List<String>> provider, String capabilityName) {
		if (!isRestricted())
			return;
		List<String> capabilities = provider.get();
		// no capabilities specified means everything is allowed (easier to implement)
		boolean authorized = capabilities == null || capabilities.contains(capabilityName);
		checkAuthorized(authorized);
	}

	public void checkInstanceReadAuthorized(TypeRef entityRef, String objectId) {
		checkCapability(supplyInstanceCapabilities(entityRef, objectId), "Read");
	}

	public void checkInstanceUpdateAuthorized(TypeRef entityRef, String objectId) {
		checkCapability(supplyInstanceCapabilities(entityRef, objectId), "Update");
	}

	public void checkInstanceDeleteAuthorized(TypeRef entityRef, String objectId) {
		checkCapability(supplyInstanceCapabilities(entityRef, objectId), "Delete");
	}

	public void checkInstanceActionAuthorized(TypeRef entityRef, String objectId, String actionName) {
		checkCapability(supplyInstanceActionCapabilities(entityRef, objectId, actionName), "Call");
	}

	public void checkEntityListAuthorized(TypeRef entityRef) {
		checkCapability(supplyEntityCapabilities(entityRef), "List");
	}

	public void checkInstanceCreationAuthorized(TypeRef entityRef) {
		checkCapability(supplyEntityCapabilities(entityRef), "Create");
	}

	public void checkEntityActionAuthorized(TypeRef entityRef, String actionName) {
		checkCapability(supplyEntityActionCapabilities(entityRef, actionName), "Call");
	}

	public void checkEntityQueryAuthorized(TypeRef entityRef, String finderName) {
		checkCapability(supplyEntityQueryCapabilities(entityRef, finderName), "Call");
	}
}
