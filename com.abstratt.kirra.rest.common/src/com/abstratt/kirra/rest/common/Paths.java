package com.abstratt.kirra.rest.common;

public interface Paths {
	String UI = "ui";
	String API = "api";
	String RETRIEVERS = "retrievers";
	String ENTITIES = "entities";
	String SERVICES = "services";

	
	String FINDERS = "finders";
	String ACTIONS = "actions";
	String EVENTS = "events";
	String INSTANCES = "instances";
	String RELATIONSHIPS = "relationships";
	String PROFILE = "profile";
	String LOGIN = "login";
	String LOGOUT = "logout";
	String DATA = "data";
	String TESTS = "tests";
	String SIGNUP = "signup";
	String PASSWORD_RESET = "passwordReset";
	String PARAMETERS = "parameters";
	String DOMAIN = "domain";
	
	String ROOT_PATH = "{application}/";
	String ENTITIES_PATH = ROOT_PATH + ENTITIES + "/";
	String ENTITY_PATH = ENTITIES_PATH + "{entityName}/";
	String INSTANCES_PATH = ENTITY_PATH + INSTANCES + "/";
	String INSTANCE_PATH = ENTITY_PATH + INSTANCES + "/{objectId}";
}