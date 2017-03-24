package com.abstratt.kirra;

import java.util.List;
import java.util.Map;

public class InstanceCapabilities {
	public InstanceCapabilities() {
		super();
	}
	public InstanceCapabilities(List<String> instance, Map<String, List<String>> attributes,
			Map<String, List<String>> relationships, Map<String, List<String>> actions) {
		super();
		this.instance = instance;
		this.attributes = attributes;
		this.relationships = relationships;
		this.actions = actions;
	}
	protected List<String> instance;
	protected Map<String, List<String>> attributes;
	protected Map<String, List<String>> relationships;
	protected Map<String, List<String>> actions;
	public List<String> getInstance() {
		return instance;
	}
	public void setInstance(List<String> instance) {
		this.instance = instance;
	}
	public Map<String, List<String>> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
	}
	public Map<String, List<String>> getRelationships() {
		return relationships;
	}
	public void setRelationships(Map<String, List<String>> relationships) {
		this.relationships = relationships;
	}
	public Map<String, List<String>> getActions() {
		return actions;
	}
	public void setActions(Map<String, List<String>> actions) {
		this.actions = actions;
	}
	
	
}
