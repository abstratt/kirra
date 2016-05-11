package com.abstratt.kirra;

import java.util.List;
import java.util.Map;

public class EntityCapabilities {
	protected List<String> entity;
	protected Map<String, List<String>> queries;
	protected Map<String, List<String>> actions;
	public List<String> getEntity() {
		return entity;
	}
	public void setEntity(List<String> entity) {
		this.entity = entity;
	}
	public Map<String, List<String>> getQueries() {
		return queries;
	}
	public void setQueries(Map<String, List<String>> queries) {
		this.queries = queries;
	}
	public Map<String, List<String>> getActions() {
		return actions;
	}
	public void setActions(Map<String, List<String>> actions) {
		this.actions = actions;
	}
	
}