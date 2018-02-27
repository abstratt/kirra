package com.abstratt.kirra.rest.resources;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path(Paths.INSTANCE_ACTION_PATH)
public class InstanceActionResource {
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public String execute(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId,
            @PathParam("actionName") String actionName, @Context UriInfo uriInfo, String argumentMapRepresentation) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
        ResourceHelper.ensure(entity != null, "Entity not found", Status.NOT_FOUND);
        Operation action = entity.getOperation(actionName);
        ResourceHelper.ensure(action != null, "Action not found", Status.NOT_FOUND);
        ResourceHelper.ensure(action.isInstanceOperation(), "Not an instance action", Status.BAD_REQUEST);
        ResourceHelper.ensure(action.getKind() == OperationKind.Action, "Not an action", Status.BAD_REQUEST);

        String selectedParameterSet = StringUtils.trimToNull(uriInfo.getQueryParameters().getFirst("parameterSet"));

        Instance instance = KirraContext.getInstanceManagement().getInstance(entityRef.getEntityNamespace(), entityRef.getTypeName(),
                objectId, true);
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        AuthorizationHelper.checkInstanceActionAuthorized(entityRef, objectId, actionName);
        
        Map<String, Object> argumentMap = ResourceHelper.deserializeArguments(argumentMapRepresentation, action.getParameters());
        List<Object> argumentList = ResourceHelper.matchArgumentsToParameters(action, argumentMap, selectedParameterSet);
        List<?> result = KirraContext.getInstanceManagement().executeOperation(action, objectId, argumentList, selectedParameterSet);
        return CommonHelper.buildGson(null).create().toJson(result);
    }
}
