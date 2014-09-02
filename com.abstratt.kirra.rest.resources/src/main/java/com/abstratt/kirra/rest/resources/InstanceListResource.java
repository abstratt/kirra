package com.abstratt.kirra.rest.resources;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;

@Path(Paths.INSTANCES_PATH)
@Produces("application/json")
@Consumes("application/json")
public class InstanceListResource {
    @POST
    public Response createInstance(@PathParam("entityName") String entityName, String newInstanceRepresentation) {
        Instance toCreate = new Gson().fromJson(newInstanceRepresentation, Instance.class);
        // flatten the structure in case the client is passing fully hidrated linked objects
        for (List<Instance> linkedInstances : toCreate.getLinks().values())
            for (Instance instance : linkedInstances)
                instance.setLinks(Collections.<String, List<Instance>>emptyMap());
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        toCreate.setEntityNamespace(entityRef.getNamespace()); 
        toCreate.setEntityName(entityRef.getTypeName());
        Instance created = KirraContext.getInstanceManagement().createInstance(toCreate);
        String json = CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES, created.getObjectId()))
                .create().toJson(created);
        return Response.status(Status.CREATED).entity(json).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    public String getInstances(@PathParam("entityName") String entityName) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        List<Instance> allInstances = KirraContext.getInstanceManagement().getInstances(entityRef.getEntityNamespace(),
                entityRef.getTypeName(), false);
        InstanceList instanceList = new InstanceList(allInstances);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instanceList);
    }
}
