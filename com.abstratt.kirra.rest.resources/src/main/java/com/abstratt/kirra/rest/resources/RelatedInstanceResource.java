package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
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

        InstanceRef relatedInstanceRef = getInstanceRef(relatedObjectId, relationship.getTypeRef());
        
		Instance instance = instanceManagement.getInstance(relatedInstanceRef.getEntityNamespace(), relatedInstanceRef.getEntityName(),
				relatedInstanceRef.getObjectId(), true);
        
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES, entityName, Paths.INSTANCES, objectId, Paths.RELATIONSHIPS, relationshipName)).create().toJson(instance);
    }
    @DELETE
    public void detachInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId, @PathParam("relationshipName") String relationshipName, @PathParam("relatedObjectId") String relatedObjectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
        
        Entity entity = KirraContext.getSchemaManagement().getEntity(entityRef);
        ResourceHelper.ensure(entity != null, null, Status.NOT_FOUND);
        
        Relationship relationship = entity.getRelationship(relationshipName);
        ResourceHelper.ensure(relationship != null, null, Status.NOT_FOUND);
        InstanceRef relatedInstanceRef = getInstanceRef(relatedObjectId, relationship.getTypeRef());
        instanceManagement.unlinkInstances(relationship, objectId, relatedInstanceRef);
    }    
    @Produces("application/json")
    @Consumes("application/json")
    @PUT
    public String attachInstance(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId, @PathParam("relationshipName") String relationshipName, @PathParam("relatedObjectId") String relatedObjectId) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        SchemaManagement schemaManagement = KirraContext.getSchemaManagement();
        Entity entity = schemaManagement.getEntity(entityRef);
        ResourceHelper.ensure(entity != null, null, Status.NOT_FOUND);
        
        Relationship relationship = entity.getRelationship(relationshipName);
        ResourceHelper.ensure(relationship != null, null, Status.NOT_FOUND);
        
        InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
        
        instanceManagement.linkInstances(relationship, objectId, relatedObjectId);
        
        Instance instance = instanceManagement.getInstance(relationship.getTypeRef().getEntityNamespace(), relationship.getTypeRef().getTypeName(),
                relatedObjectId, true);
        
        ResourceHelper.ensure(instance != null, "Instance not found", Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES, entityName, Paths.INSTANCES, objectId, Paths.RELATIONSHIPS, relationshipName)).create().toJson(instance);
    }
    private InstanceRef getInstanceRef(String objectId, TypeRef defaultType) {
        TypeRef instanceType;
        if (objectId.contains("@")) {
        	String[] components = objectId.split("@");
        	ResourceHelper.ensure(components.length == 2, null, Status.NOT_FOUND); 
        	instanceType = new TypeRef(components[0], TypeKind.Entity);
        	objectId = components[1];
        } else {
        	instanceType = defaultType;
        }
        return new InstanceRef(instanceType.getEntityNamespace(), instanceType.getTypeName(), objectId);
    }
}
