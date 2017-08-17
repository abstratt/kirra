package com.abstratt.kirra.rest.resources;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

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
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceManagement.Page;
import com.abstratt.kirra.InstanceManagement.PageRequest;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
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

		Map<String, Object> arguments = new LinkedHashMap<>();
		PageRequest pageRequest = ResourceHelper.processQuery(uriInfo, (key, values) -> arguments.put(key, values.iterator().next()));
		List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(finder, arguments);

        List<Instance> matchingInstances = (List<Instance>) KirraContext.getInstanceManagement().executeQuery(finder,
				null, argumentList, pageRequest);
		InstanceList instanceList = new InstanceList(matchingInstances, pageRequest.getFirst(), matchingInstances.size());
		return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES))
				.create().toJson(instanceList);
	}

	@POST
	public String findInstances(@PathParam("entityName") String entityName, @PathParam("finderName") String finderName,
			String requestBodyAsString,
            @Context UriInfo uriInfo) {
		Operation finder = getOperation(entityName, finderName);

		PageRequest pageRequest = ResourceHelper.processQuery(uriInfo, (key, values) -> {});
        Map<String, Object> request = new Gson().fromJson(requestBodyAsString,
                new TypeToken<Map<String, Object>>() {
                }.getType());
		Map<String, Object> arguments = (Map<String, Object>) request.get("arguments");
		List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(finder, arguments);

		List<String> subset = (List<String>) request.get("subset");
		if (subset != null) {
		    pageRequest = new PageRequest(0L, Integer.MAX_VALUE, pageRequest.getDataProfile(), pageRequest.getIncludeSubtypes());
		}
		List<Instance> matchingInstances = (List<Instance>) KirraContext.getInstanceManagement().executeQuery(finder,
		        null, argumentList, pageRequest);
		if (subset != null) {
		    Set<InstanceRef> instancesToInclude = subset.stream().map(it -> InstanceRef.parse(it, null)).collect(Collectors.toSet());
		    matchingInstances = matchingInstances.stream().filter(it -> instancesToInclude.contains(it.getReference())).collect(Collectors.toList());
		}
		
		InstanceList instanceList = new InstanceList(matchingInstances, pageRequest.getFirst(), matchingInstances.size());
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
