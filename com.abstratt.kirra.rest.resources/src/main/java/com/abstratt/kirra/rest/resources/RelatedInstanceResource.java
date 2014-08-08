package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.RELATED_INSTANCE_PATH)
public class RelatedInstanceResource extends InstanceResource {
    @GET
    @Produces("application/json")
    public String getInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId, @PathParam("relationshipName") String relationshipName, @PathParam("relatedObjectId") String relatedObjectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
        
        Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
        ResourceHelper.ensure(entity != null, null, Status.NOT_FOUND);
        
        Relationship relationship = entity.getRelationship(relationshipName);
        ResourceHelper.ensure(relationship != null, null, Status.NOT_FOUND);

        Instance instance = instanceManagement.getInstance(relationship.getTypeRef().getEntityNamespace(), relationship.getTypeRef().getTypeName(),
                relatedObjectId, true);
        
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES, entityName, Paths.INSTANCES, objectId, Paths.RELATIONSHIPS, relationshipName)).create().toJson(instance);
    }
}
