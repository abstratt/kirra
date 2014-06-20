package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.Page;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;

@Path(Paths.INSTANCES_PATH)
@Produces("application/json")
@Consumes("application/json")
public class InstanceListResource {
    private class InstanceList extends Page<Instance> {
        public InstanceList(List<Instance> contents) {
            super(contents);
        }
    }

    @POST
    public String createInstance(@PathParam("entityName") String entityName, String newInstanceRepresentation) {
        Instance toCreate = new Gson().fromJson(newInstanceRepresentation, Instance.class);
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        toCreate.setEntityNamespace(entityRef.getNamespace()); 
        toCreate.setEntityName(entityRef.getTypeName());
        Instance created = KirraContext.getInstanceManagement().createInstance(toCreate);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES, created.getObjectId()))
                .toJson(created);
    }

    @GET
    public String getInstances(@PathParam("entityName") String entityName) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        List<Instance> allInstances = KirraContext.getInstanceManagement().getInstances(entityRef.getEntityNamespace(),
                entityRef.getTypeName(), false);
        InstanceList instanceList = new InstanceList(allInstances);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).toJson(instanceList);
    }
}
