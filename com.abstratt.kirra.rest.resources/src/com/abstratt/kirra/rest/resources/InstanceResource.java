package com.abstratt.kirra.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCE_PATH)
@Consumes("application/json")
@Produces("application/json")
public class InstanceResource {
    @GET
    public String getInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        Instance instance = KirraContext.getInstanceManagement().getInstance(entityRef.getEntityNamespace(), entityRef.getTypeName(),
                objectId, true);
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES, entityName, Paths.INSTANCES)).toJson(instance);
    }
}
