package com.abstratt.kirra.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class RelationshipJSONRepresentation {
	@JsonProperty
    public String name;
	@JsonProperty
	public String type;
	@JsonProperty
	public String typeUri;
}
