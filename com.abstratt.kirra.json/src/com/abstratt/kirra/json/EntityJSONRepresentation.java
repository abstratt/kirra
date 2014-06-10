package com.abstratt.kirra.json;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.abstratt.kirra.Property;

public class EntityJSONRepresentation {
	public boolean topLevel;
	@JsonProperty
	public String namespace;
	@JsonProperty
	public String name;
	@JsonProperty
	public String description;
	@JsonProperty
	public String label;
	@JsonProperty
	public String uri;
	@JsonProperty
	public String rootUri;
	@JsonProperty
	public String instances;
	public String template;
	@JsonProperty
	public List<ActionJSONRepresentation> actions = new ArrayList<ActionJSONRepresentation>();
	@JsonProperty
	public List<QueryJSONRepresentation> finders = new ArrayList<QueryJSONRepresentation>();
	@JsonProperty
	public List<Property> properties = new ArrayList<Property>();
	public List<RelationshipJSONRepresentation> relationships = new ArrayList<RelationshipJSONRepresentation>();
}