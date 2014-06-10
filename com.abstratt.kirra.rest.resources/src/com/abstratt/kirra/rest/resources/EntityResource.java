package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.ENTITY_PATH)
public class EntityResource {
	@GET
	public String getEntity(@PathParam("entityName") String entityName) {

		TypeRef typeRef = new TypeRef(entityName, TypeKind.Entity);
		Entity entity = KirraContext.getSchemaManagement().getEntity(typeRef);
		ResourceHelper.ensure(entity != null, null, Status.NOT_FOUND);
		return ResourceHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES)).toJson(entity);
	}
}
