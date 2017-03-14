package com.abstratt.kirra.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.SESSION_PATH)
public class SessionResource {
	/**
	 * Allows a user to be created.
	 * @return 
	 */
    @POST
    @Consumes("application/json")
    public Response signUp(String newProfileInstanceRepresentation) {
    	//TODO-RC what entity name to use for profiles?
    	String entityName = "entityName";
        Instance toCreate = CommonHelper.buildGson(null).create().fromJson(newProfileInstanceRepresentation, Instance.class);
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        toCreate.setEntityNamespace(entityRef.getNamespace()); 
        toCreate.setEntityName(entityRef.getTypeName());
        Instance created = KirraContext.getInstanceManagement().createUser(toCreate);
		String json = CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES, created.getObjectId()))
                .create().toJson(created);
        return Response.status(Status.CREATED).entity(json).type(MediaType.APPLICATION_JSON).build();
    }
}
