package com.abstratt.kirra.rest.common;

public interface Paths {
    String ACTIONS = "actions";
    String API = "api";
    String DATA = "data";
    String DOMAIN = "domain";
    String ENTITIES = "entities";
    String EVENTS = "events";
    String PARAMETERS = "parameters";
    String FINDERS = "finders";
    String RETRIEVERS = "retrievers";
    String RELATIONSHIPS = "relationships";
    String INSTANCES = "instances";

    String ENTITIES_PATH = Paths.ROOT_PATH + Paths.ENTITIES + "/";
    String ENTITY_PATH = Paths.ENTITIES_PATH + "{entityName}/";
    String ENTITY_ACTION_PATH = ENTITY_PATH + Paths.ACTIONS + "/{actionName}";
    String INSTANCES_PATH = Paths.ENTITY_PATH + Paths.INSTANCES + "/";
    String FINDER_RESULTS_PATH = Paths.ENTITY_PATH + Paths.FINDERS + "/{finderName}";
    String INSTANCE_PATH = Paths.INSTANCES_PATH + "{objectId}";
    String INSTANCE_ACTION_PATH = INSTANCE_PATH + "/" + Paths.ACTIONS + "/{actionName}";
    String RELATED_INSTANCES_PATH = INSTANCE_PATH + "/" + Paths.RELATIONSHIPS + "/{relationshipName}";
    String RELATED_INSTANCE_PATH = RELATED_INSTANCES_PATH + "/{relatedObjectId}";
    String RELATIONSHIP_PATH = INSTANCE_PATH + "/" + Paths.RELATIONSHIPS + "/{relationshipName}/";
    String RELATIONSHIP_DOMAIN_PATH = RELATIONSHIP_PATH + DOMAIN;
    String INSTANCE_ACTION_PARAMETER_DOMAIN_PATH = INSTANCE_ACTION_PATH + "/" + Paths.PARAMETERS + "/{parameterName}/" + DOMAIN;
    String LOGIN = "login";
    String LOGOUT = "logout";
    String PASSWORD_RESET = "passwordReset";
    String PROFILE = "profile";

    String ROOT_PATH = "{application}/";
    String SERVICE_PATH = Paths.SERVICES_PATH + "{serviceName}/";
    String SERVICES = "services";
    String SERVICES_PATH = Paths.ROOT_PATH + Paths.SERVICES + "/";
    String SIGNUP = "signup";
    String TESTS = "tests";
    String UI = "ui";
}