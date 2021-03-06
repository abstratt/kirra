package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.EntityCapabilities;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.ENTITY_PATH)
@Produces("application/json")
public class EntityResource {
	
	
    @GET
    public String getEntity(@PathParam("entityName") String entityName) {
        TypeRef typeRef = new TypeRef(entityName, TypeKind.Entity);
        Entity entity = KirraContext.getSchemaManagement().getEntity(typeRef);
        ResourceHelper.ensure(entity != null, () -> null, Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES)).create().toJson(entity);
    }
    @GET
    @Path(Paths.CAPABILITIES)
    public String getEntityCapabilities(@Context UriInfo uriInfo, @PathParam("entityName") String entityName) {
        TypeRef typeRef = new TypeRef(entityName, TypeKind.Entity);
    	EntityCapabilities capabilities = KirraContext.getInstanceManagement().getEntityCapabilities(typeRef);
		return CommonHelper.buildGson(uriInfo.getBaseUri()).create().toJson(capabilities);
    }
}
