package com.abstratt.kirra.rest.resources;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Page;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path(Paths.FINDER_RESULTS_PATH)
@Produces("application/json")
@Consumes("application/json")
public class FinderResultResource {
	@GET
	public String findInstances(@PathParam("entityName") String entityName, @PathParam("finderName") String finderName,
			@Context UriInfo uriInfo) {
		Operation finder = getOperation(entityName, finderName);

		Map<String, Object> arguments = parseArgumentsFromQuery(uriInfo);
		List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(finder, arguments);

		List<Instance> matchingInstances = (List<Instance>) KirraContext.getInstanceManagement().executeQuery(finder,
				null, argumentList);
		InstanceList instanceList = new InstanceList(matchingInstances);
		return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES))
				.create().toJson(instanceList);
	}

	@POST
	public String findInstances(@PathParam("entityName") String entityName, @PathParam("finderName") String finderName,
			String argumentMapRepresentation) {
		Operation finder = getOperation(entityName, finderName);

		Map<String, Object> arguments = new Gson().fromJson(argumentMapRepresentation,
				new TypeToken<Map<String, Object>>() {
				}.getType());
		List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(finder, arguments);

		List<Instance> matchingInstances = (List<Instance>) KirraContext.getInstanceManagement().executeQuery(finder,
				null, argumentList);
		InstanceList instanceList = new InstanceList(matchingInstances);
		return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES))
				.create().toJson(instanceList);
	}

	@GET
	@Path(Paths.METRICS)
	public String getMetrics(@PathParam("entityName") String entityName, @PathParam("finderName") String finderName,
			@Context UriInfo uriInfo) {
		Operation finder = getOperation(entityName, finderName);
		Map<String, Object> arguments = parseArgumentsFromQuery(uriInfo);
		List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(finder, arguments);
		long count = KirraContext.getInstanceManagement().countQueryResults(finder, null, argumentList);
		Page metrics = new Page<>(Arrays.asList(count));
		return CommonHelper.buildBasicGson().create().toJson(metrics);
	}

	private Map<String, Object> parseArgumentsFromQuery(UriInfo uriInfo) {
		Map<String, Object> arguments = new LinkedHashMap<String, Object>();
		for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
			arguments.put(entry.getKey(), entry.getValue().iterator().next());
		return arguments;
	}

	private Operation getOperation(String entityName, String finderName) {
		TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
		AuthorizationHelper.checkEntityFinderAuthorized(entityRef, finderName);

		Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
		ResourceHelper.ensure(entity != null, "Entity not found", Status.NOT_FOUND);
		Operation finder = entity.getOperation(finderName);
		ResourceHelper.ensure(finder != null, "Finder not found", Status.NOT_FOUND);
		ResourceHelper.ensure(!finder.isInstanceOperation(), "Not a finder", Status.BAD_REQUEST);
		ResourceHelper.ensure(finder.getKind() == OperationKind.Finder, "Not a finder", Status.BAD_REQUEST);
		return finder;
	}

}
