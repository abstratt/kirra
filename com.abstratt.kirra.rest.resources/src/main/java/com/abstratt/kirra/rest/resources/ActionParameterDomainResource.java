package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.Operation.OperationKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

public abstract class ActionParameterDomainResource {
    protected String listParameterDomain(String entityName, String actionName, String parameterName, String objectId) {
		TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        SchemaManagement schemaManagement = KirraContext.getSchemaManagement();
        Entity entity = schemaManagement.getEntity(entityRef);
        ResourceHelper.ensure(entity != null, Status.NOT_FOUND);
        
        Operation action = entity.getOperation(actionName);
        ResourceHelper.ensure(action != null, Status.NOT_FOUND);
        ResourceHelper.ensure(action.getKind() == OperationKind.Action, Status.NOT_FOUND);
        
        Parameter parameter = action.getParameter(parameterName);
        ResourceHelper.ensure(parameter != null, Status.NOT_FOUND);
        
		List<Instance> domain = KirraContext.getInstanceManagement().getParameterDomain(entity, objectId, action, parameter);
        InstanceList instanceList = new InstanceList(domain);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instanceList);
	}
}
