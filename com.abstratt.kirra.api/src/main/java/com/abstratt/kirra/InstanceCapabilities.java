package com.abstratt.kirra;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** 
 * The capabilities available for a given instance.
 */
public class InstanceCapabilities {
	public InstanceCapabilities() {
		this(Collections.emptyList(), Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap());
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
    
    /** Capabilities about the instance */
	public List<String> getInstance() {
		return instance;
	}
	public void setInstance(List<String> instance) {
		this.instance = instance;
    }
    /** Capabilities for each property. */
	public Map<String, List<String>> getAttributes() {
		return attributes;
	}
	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
    }
    /** Capabilities for each relationship */
	public Map<String, List<String>> getRelationships() {
		return relationships;
	}
	public void setRelationships(Map<String, List<String>> relationships) {
		this.relationships = relationships;
    }
    
    /** Capabilities for each action */
	public Map<String, List<String>> getActions() {
		return actions;
	}
	public void setActions(Map<String, List<String>> actions) {
		this.actions = actions;
	}
	
	
}
