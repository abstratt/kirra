package com.abstratt.kirra.json;

import org.codehaus.jackson.annotate.JsonProperty;

public class EntityLinkJSONRepresentation {
	@JsonProperty
	public String namespace;
	@JsonProperty
	public String name;
	@JsonProperty
	public String uri;
	@JsonProperty
	public String description;
	@JsonProperty
	public String label;
	public String extentUri;
}