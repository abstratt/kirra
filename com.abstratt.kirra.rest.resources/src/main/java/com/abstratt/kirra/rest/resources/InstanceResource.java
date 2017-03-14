package com.abstratt.kirra.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceCapabilities;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCE_PATH)
@Produces("application/json")
public class InstanceResource {
    @GET
    public String getInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
        Instance instance = "_template".equals(objectId) ? instanceManagement.newInstance(entityRef.getEntityNamespace(), entityRef.getTypeName()) : instanceManagement.getInstance(entityRef.getEntityNamespace(), entityRef.getTypeName(),
                objectId, true);
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instance);
    }
    
    @DELETE
    public void deleteInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
        instanceManagement.deleteInstance(entityRef.getEntityNamespace(), entityRef.getTypeName(), objectId);
    }

    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String updateInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId,
            String existingInstanceRepresentation) {
        TypeRef typeRef = new TypeRef(entityName, TypeKind.Entity);
        Instance toUpdate = CommonHelper.buildGson(null).create().fromJson(existingInstanceRepresentation, Instance.class);
        toUpdate.setObjectId(objectId);
        toUpdate.setEntityName(typeRef.getTypeName());
        toUpdate.setEntityNamespace(typeRef.getNamespace());
        Instance updated = KirraContext.getInstanceManagement().updateInstance(toUpdate);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES))
                .create().toJson(updated);
    }
    @GET
    @Path(Paths.CAPABILITIES)
    public String getInstanceCapabilities(@Context UriInfo uriInfo, @PathParam("entityName") String entityName, @PathParam("objectId") String objectId) {
        TypeRef typeRef = new TypeRef(entityName, TypeKind.Entity);
        Entity entity = KirraContext.getSchemaManagement().getEntity(typeRef);
    	InstanceCapabilities capabilities = KirraContext.getInstanceManagement().getInstanceCapabilities(entity, objectId);
    	ResourceHelper.ensure(capabilities != null, "Instance not found", Status.NOT_FOUND);
    	return CommonHelper.buildGson(uriInfo.getBaseUri()).create().toJson(capabilities);
    }
}
