package com.abstratt.kirra.rest.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Path(Paths.ENTITY_ACTION_PATH)
@Produces("application/json")
@Consumes("application/json")
public class EntityActionResource {
    @POST
    public String execute(@PathParam("entityName") String entityName, 
            @PathParam("actionName") String actionName, @Context UriInfo uriInfo, String argumentMapRepresentation) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkEntityActionAuthorized(entityRef, actionName);
        
        Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
        ResourceHelper.ensure(entity != null, "entity_not_found", Status.NOT_FOUND);
        Operation action = entity.getOperation(actionName);
        ResourceHelper.ensure(action != null, "operation_not_found", Status.NOT_FOUND);
        ResourceHelper.ensure(!action.isInstanceOperation(), "not_entity_action", Status.BAD_REQUEST);
        ResourceHelper.ensure(action.getKind() == OperationKind.Action, "not_action", Status.BAD_REQUEST);

        String selectedParameterSet = StringUtils.trimToNull(uriInfo.getQueryParameters().getFirst("parameterSet"));
        
        Map<String, Object> argumentMap = ResourceHelper.deserializeArguments(argumentMapRepresentation, action.getParameters());
        List<Object> argumentList = new ArrayList<Object>();
        for (Parameter parameter : action.getParameters()) {
            if (selectedParameterSet != null)
                if (!parameter.getParameterSets().contains(selectedParameterSet))
                    continue;
            Object argumentValue = argumentMap.get(parameter.getName());
            if (argumentValue != null && parameter.getTypeRef().getKind() == TypeKind.Entity) {
				String objectId = (argumentValue instanceof InstanceRef) ? ((InstanceRef) argumentValue).getObjectId() : ((Map<String,Object>) argumentValue).get("objectId").toString();
				argumentValue = new Instance(parameter.getTypeRef(), objectId);
			}
            argumentList.add(argumentValue);
        }
        List<?> result = KirraContext.getInstanceManagement().executeOperation(action, null, argumentList, selectedParameterSet);
        return CommonHelper.buildGson(null).create().toJson(result);
    }
}
