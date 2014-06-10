//package com.abstratt.kirra.rest.client;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map.Entry;
//
//import org.apache.commons.httpclient.methods.GetMethod;
//import org.codehaus.jackson.JsonNode;
//import org.codehaus.jackson.node.ArrayNode;
//import org.codehaus.jackson.node.ObjectNode;
//
//import com.abstratt.kirra.Entity;
//import com.abstratt.kirra.Instance;
//import com.abstratt.kirra.InstanceManagement;
//import com.abstratt.kirra.Operation;
//import com.abstratt.kirra.Parameter;
//import com.abstratt.kirra.Relationship;
//
//public class InstanceManagementOnREST implements InstanceManagement {
//
//	/**
//	 * The application's base URI.
//	 */
//	private URI baseUri;
//	private RestClient restClient;
//
//	public InstanceManagementOnREST(URI baseUri) {
//		this.baseUri = baseUri;
//		this.restClient = new RestClient();
//	}
//
//	@Override
//	public Instance createInstance(Instance instance) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void deleteInstance(Instance instance) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void deleteInstance(String namespace, String name, String id) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public List<?> executeOperation(Operation operation, String externalId, List<?> arguments) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Instance getCurrentUser() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Operation> getEnabledEntityActions(Entity entity) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Instance newInstance(String namespace, String name) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void validateInstance(Instance toValidate) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void linkInstances(Relationship relationship, String sourceId, String destinationId) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void unlinkInstances(Relationship relationship, String sourceId, String destinationId) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public Instance getInstance(String namespace, String name, String externalId, boolean full) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Instance> getInstances(String namespace, String name, boolean full) {
//		URI namespaceUri = URI.create(baseUri.toASCIIString() + namespace + "/");
//		GetMethod getInstances = new GetMethod(namespaceUri.resolve("instances/").resolve(namespace + "." + name + "/").toString());
//		ArrayNode instanceListNodes = (ArrayNode) restClient.executeMethod(getInstances);
//        List<Instance> instances = new ArrayList<Instance>(instanceListNodes.size());
//        for (int i = 0; i < instanceListNodes.size(); i++)
//        	instances.add(buildInstance((ObjectNode) instanceListNodes.get(i)));
//		return instances;
//	}
//
//	private Instance buildInstance(ObjectNode instanceNode) {
//		Instance instance = new Instance();
//		instance.setEntityNamespace(instanceNode.get("entityNamespace").asText());
//		instance.setEntityName(instanceNode.get("entityName").asText());
//		ObjectNode values = (ObjectNode) instanceNode.get("values");
//		Iterator<Entry<String, JsonNode>> fields = values.getFields();
//		while (fields.hasNext()) {
//			Entry<String, JsonNode> current = fields.next();
//			instance.setValue(current.getKey(), current.getValue().asText());
//		}
//		if (instanceNode.has("shorthand"))
//			instance.setShorthand(instanceNode.get("shorthand").asText());
//		return instance;
//	}
//
//	@Override
//	public List<Instance> getRelatedInstances(String namespace, String name, String externalId, String relationship, boolean full) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Instance> getParameterDomain(Entity entity, String externalId, Operation action, Parameter parameter) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<Instance> getRelationshipDomain(Entity entity, String objectId, Relationship relationship) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public Instance updateInstance(Instance instance) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void zap() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void saveContext() {
//		// TODO Auto-generated method stub
//
//	}
//}