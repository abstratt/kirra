package com.abstratt.kirra.rest.common;

public interface Paths {
    String ACTIONS = "actions";
    String API = "api";
    String BLOBS = "blobs";
    String DATA = "data";
    String DOMAIN = "domain";
    String ENTITIES = "entities";
    String EVENTS = "events";
    String PARAMETERS = "parameters";
    String FINDERS = "finders";
    String RETRIEVERS = "retrievers";
    String RELATIONSHIPS = "relationships";
    String INSTANCES = "instances";
    String METRICS = "metrics";
    
    String DATA_SELECTORS = "first=(first)&maximum=(maximum)&dataprofile=(dataProfile)";

    String ENTITIES_PATH = Paths.ROOT_PATH + Paths.ENTITIES + "/";
    String CAPABILITIES = "capabilities";
    String ALL_ENTITY_CAPABILITIES_PATH = Paths.ROOT_PATH + Paths.CAPABILITIES + "/";
    String ENTITY_PATH = Paths.ENTITIES_PATH + "{entityName}/";
    String ENTITY_ACTION_PATH = ENTITY_PATH + Paths.ACTIONS + "/{actionName}";
    String INSTANCES_PATH = Paths.ENTITY_PATH + Paths.INSTANCES + "/";
    String INSTANCE_METRICS_PATH = INSTANCES_PATH + METRICS;
    String FINDER_RESULTS_PATH = Paths.ENTITY_PATH + Paths.FINDERS + "/{finderName}";
    String FINDER_RESULTS_METRICS_PATH = Paths.FINDER_RESULTS_PATH + "/" + METRICS;
    String INSTANCE_PATH = Paths.INSTANCES_PATH + "{objectId}";
    String INSTANCE_BLOB_PATH = INSTANCE_PATH + "/" + Paths.BLOBS + "/{propertyName}";
    String INSTANCE_ACTION_PATH = INSTANCE_PATH + "/" + Paths.ACTIONS + "/{actionName}";
    String RELATED_INSTANCES_PATH = INSTANCE_PATH + "/" + Paths.RELATIONSHIPS + "/{relationshipName}";
    String RELATED_INSTANCE_PATH = RELATED_INSTANCES_PATH + "/{relatedObjectId}";
    String RELATIONSHIP_PATH = INSTANCE_PATH + "/" + Paths.RELATIONSHIPS + "/{relationshipName}/";
    String RELATIONSHIP_DOMAIN_PATH = RELATIONSHIP_PATH + DOMAIN;
    String INSTANCE_ACTION_PARAMETER_DOMAIN_PATH = INSTANCE_ACTION_PATH + "/" + Paths.PARAMETERS + "/{parameterName}/" + DOMAIN;
    String ENTITY_ACTION_PARAMETER_DOMAIN_PATH = ENTITY_ACTION_PATH + "/" + Paths.PARAMETERS + "/{parameterName}/" + DOMAIN;
    String SESSION = "session";
    String LOGIN = SESSION + "/" + "login";
    String LOGOUT = SESSION + "/" + "logout";
    String ROOT_PATH = "{application}/";
    String SESSION_PATH = ROOT_PATH + SESSION + "/";
    String LOGIN_PATH = ROOT_PATH + LOGIN + "/";
    String LOGOUT_PATH = ROOT_PATH + LOGOUT + "/";
    String PASSWORD_RESET = "passwordReset";
    String SERVICE_PATH = Paths.SERVICES_PATH + "{serviceName}/";
    String SERVICES = "services";
    String SERVICES_PATH = Paths.ROOT_PATH + Paths.SERVICES + "/";
    String SIGNUP = "signup";
    String SIGNUP_PATH = Paths.ROOT_PATH + SIGNUP + "/{roleEntityName}";
    String TESTS = "tests";
    String UI = "ui";
	String TEMPLATE = "template";
	String TEMPLATE_PATH = Paths.INSTANCES_PATH + TEMPLATE;
}