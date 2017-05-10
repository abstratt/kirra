package com.abstratt.kirra.rest.resources;

import java.util.ArrayList;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCES_PATH)
@Produces("application/json")
@Consumes("application/json")
public class InstanceListResource {
    @POST
    public Response createInstance(@PathParam("entityName") String entityName, String newInstanceRepresentation) {
    	TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkInstanceCreationAuthorized(entityRef);
    	
        Instance toCreate = CommonHelper.buildGson(null).create().fromJson(newInstanceRepresentation, Instance.class);
        toCreate.setEntityNamespace(entityRef.getNamespace()); 
        toCreate.setEntityName(entityRef.getTypeName());
        Instance created = KirraContext.getInstanceManagement().createInstance(toCreate);
        String json = CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES, created.getObjectId()))
                .create().toJson(created);
        return Response.status(Status.CREATED).entity(json).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    public String getInstances(@PathParam("entityName") String entityName, @Context UriInfo uriInfo) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkEntityListAuthorized(entityRef);
        
        Map<String, List<Object>> criteria = new LinkedHashMap<String, List<Object>>();
        List<String> builtInParameters = Arrays.asList("includesubtypes");
        boolean includeSubtypes = false;
        for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
        	if (builtInParameters.contains(entry.getKey())) {
        		if (entry.getKey().equals("includesubtypes")) {
        			// carefully avoiding Restlet bug #640 https://github.com/restlet/restlet-framework-java/issues/640
        			includeSubtypes = Boolean.parseBoolean(entry.getValue().stream().findAny().orElse(Boolean.FALSE.toString()));        		    	
        		}
    		} else
        		criteria.put(entry.getKey(), new ArrayList<Object>(entry.getValue()));
        List<Instance> allInstances = KirraContext.getInstanceManagement().filterInstances(criteria, entityRef.getEntityNamespace(),
                entityRef.getTypeName(), true, includeSubtypes);
        InstanceList instanceList = new InstanceList(allInstances);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instanceList);
    }
}
