package com.abstratt.kirra.json;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonProperty;

public class InvocationJSONRepresentation {
	@JsonProperty
	public Map<String, Object> arguments;
}