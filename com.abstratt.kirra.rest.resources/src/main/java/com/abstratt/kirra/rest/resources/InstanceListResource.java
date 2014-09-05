package com.abstratt.kirra.rest.resources;

import java.util.ArrayList;
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
        Instance toCreate = CommonHelper.buildGson(null).create().fromJson(newInstanceRepresentation, Instance.class);
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
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
        Map<String, List<Object>> criteria = new LinkedHashMap<String, List<Object>>();
        for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
            criteria.put(entry.getKey(), new ArrayList<Object>(entry.getValue()));
        List<Instance> allInstances = KirraContext.getInstanceManagement().filterInstances(criteria, entityRef.getEntityNamespace(),
                entityRef.getTypeName(), false);
        InstanceList instanceList = new InstanceList(allInstances);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instanceList);
    }
}
