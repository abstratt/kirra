package com.abstratt.kirra.json;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class InstanceJSONRepresentation extends TupleJSONRepresentation {
    public static class ActionParameter {
    	public String domainUri;
    }
	
	public static class Action {
		public String uri;
		public boolean enabled;
		public Map<String, ActionParameter> parameters;
	}
	
	public static class MultipleLink {
		public String uri;
		public String domainUri;
	}
	
	public static class SingleLink {
		public String uri;
		public String domainUri;
		public String shorthand;
	}

	@JsonProperty
	public String shorthand;
	@JsonProperty
	public String uri;
	@JsonProperty
	public String type;
	@JsonProperty
	public Map<String, MultipleLink> links;
	@JsonProperty
	public Map<String, Action> actions;
	public String entityNamespace;
	public String entityName;

}