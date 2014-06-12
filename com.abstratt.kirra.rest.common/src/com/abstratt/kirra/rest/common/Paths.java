package com.abstratt.kirra.rest.common;

public interface Paths {
    String ACTIONS = "actions";
    String API = "api";
    String DATA = "data";
    String DOMAIN = "domain";
    String ENTITIES = "entities";

    String ENTITIES_PATH = Paths.ROOT_PATH + Paths.ENTITIES + "/";
    String ENTITY_PATH = Paths.ENTITIES_PATH + "{entityName}/";
    String EVENTS = "events";
    String FINDERS = "finders";
    String INSTANCE_PATH = Paths.ENTITY_PATH + Paths.INSTANCES + "/{objectId}";
    String INSTANCE_ACTION_PATH = INSTANCE_PATH + "/" + Paths.ACTIONS + "/{actionName}";
    String INSTANCES = "instances";
    String INSTANCES_PATH = Paths.ENTITY_PATH + Paths.INSTANCES + "/";
    String LOGIN = "login";
    String LOGOUT = "logout";
    String PARAMETERS = "parameters";
    String PASSWORD_RESET = "passwordReset";
    String PROFILE = "profile";
    String RELATIONSHIPS = "relationships";
    String RETRIEVERS = "retrievers";

    String ROOT_PATH = "{application}/";
    String SERVICE_PATH = Paths.SERVICES_PATH + "{serviceName}/";
    String SERVICES = "services";
    String SERVICES_PATH = Paths.ROOT_PATH + Paths.SERVICES + "/";
    String SIGNUP = "signup";
    String TESTS = "tests";
    String UI = "ui";
}