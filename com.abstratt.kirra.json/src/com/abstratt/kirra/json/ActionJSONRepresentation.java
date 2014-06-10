package com.abstratt.kirra.json;

import org.codehaus.jackson.annotate.JsonProperty;

import com.abstratt.kirra.json.InstanceJSONRepresentation.Action;

public class ActionJSONRepresentation extends Action {
	@JsonProperty
	public String uri;
}
