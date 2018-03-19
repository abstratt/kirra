package com.abstratt.kirra.rest.client;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.Index;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.reflect.TypeToken;

public class InstanceManagementOnREST implements InstanceManagement {

	/**
	 * The application's base URI.
	 */
	private URI baseUri;
	private RestClient restClient;

	public InstanceManagementOnREST(URI baseUri) {
		this(new RestClient(baseUri.resolve("/")), baseUri);
	}

	public InstanceManagementOnREST(RestClient restClient, URI baseUri) {
		this.baseUri = baseUri;
		this.restClient = restClient;
	}

	@Override
	public Instance createInstance(Instance instance) {
		return restClient.post(baseUri, instance, (Type) Instance.class,
				Paths.ENTITIES, instance.getTypeRef().toString(),
				Paths.INSTANCES, "");
	}
	
	@Override
	public void deleteInstance(Instance instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteInstance(String namespace, String name, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<?> executeOperation(Operation operation, String externalId,
			List<?> arguments) {
		List<Parameter> parameters = operation.getParameters();
		if (parameters.size() != arguments.size())
			throw new KirraException("Mismatch", Kind.SCHEMA);
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		for (int i = 0; i < parameters.size(); i++)
			values.put(parameters.get(i).getName(), arguments.get(i));
		String[] instanceActionPath = new String[] { Paths.ENTITIES,
				operation.getOwner().getFullName(), Paths.INSTANCES,
				externalId, Paths.ACTIONS, operation.getName() };
		String[] entityActionPath = new String[] { Paths.ENTITIES,
				operation.getOwner().getFullName(), Paths.ACTIONS, operation.getName() };
		return (List<?>) restClient.post(baseUri, values, List.class, 
				operation.isInstanceOperation() ? instanceActionPath : entityActionPath);
	}

	@Override
	public Instance getCurrentUser() {
		Index index = get(baseUri, Index.class);
		Instance currentUser = Optional.ofNullable(index.getCurrentUser()).map(it -> (Instance) get(baseUri.resolve(it.getPath()), Instance.class)).orElse(null);
		return currentUser;
	}
	
	@Override
	public List<Instance> getCurrentUserRoles() {
		Index index = get(baseUri, Index.class);
		return index.getCurrentUserRoles().values().stream().map(it -> (Instance) get(baseUri.resolve(it.getPath()), Instance.class)).collect(Collectors.toList());
	}

	@Override
	public List<Operation> getEnabledEntityActions(Entity entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Instance getInstance(String namespace, String name,
			String externalId, DataProfile profile) {
		return get(baseUri, Instance.class, Paths.ENTITIES,
				TypeRef.toString(namespace, name), Paths.INSTANCES, externalId);
	}

	@Override
	public List<Instance> getInstances(String namespace, String name,
			DataProfile profile) {
		Page<Instance> page = get(baseUri,
				new TypeToken<Page<Instance>>() {
				}.getType(), Paths.ENTITIES, TypeRef.toString(namespace, name),
				Paths.INSTANCES);
		return page.contents;
	}
	
	private <T> T get(URI baseUri, Type type, String... segments) {
		return get(baseUri, type, HttpStatus.SC_OK, segments);
	}
	
	private <T> T get(URI baseUri, Type type, Integer expected, String... segments) {
		URI uri = segments.length > 0 ? baseUri.resolve(StringUtils.join(segments, "/")) : baseUri;
		return restClient.executeMethod(new GetMethod(uri.toString()), type, expected);
	}

	@Override
	public List<Instance> filterInstances(Map<String, List<Object>> criteria, String namespace, String name, DataProfile profile) {
	    return getInstances(namespace, name, profile);
	}

	@Override
	public List<Instance> getParameterDomain(Entity entity, String externalId,
			Operation action, Parameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getRelatedInstances(String namespace, String name,
			String externalId, String relationship, DataProfile profile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Instance> getRelationshipDomain(Entity entity, String objectId,
			Relationship relationship) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void linkInstances(Relationship relationship, String sourceId,
			InstanceRef destinationRef) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void unlinkInstances(Relationship relationship, String sourceId,
			InstanceRef destinationRef) {
		// TODO Auto-generated method stub
	}

	@Override
	public Instance newInstance(String namespace, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveContext() {
		// TODO Auto-generated method stub

	}


	@Override
	public Instance updateInstance(Instance instance) {
		return restClient.put(baseUri, instance, Paths.ENTITIES, instance
				.getTypeRef().toString(), Paths.INSTANCES, instance
				.getObjectId());
	}

	@Override
	public void validateInstance(Instance toValidate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void zap() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean isRestricted() {
	    return false;
	}
}