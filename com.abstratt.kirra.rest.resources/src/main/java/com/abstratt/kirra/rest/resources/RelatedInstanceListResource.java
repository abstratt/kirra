package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Relationship;
import com.abstratt.kirra.SchemaManagement;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.RELATED_INSTANCES_PATH)
public class RelatedInstanceListResource {
    @Produces("application/json")
    @Consumes("application/json")
    @GET
    public String getRelatedInstances(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId, @PathParam("relationshipName") String relationshipName) {
        TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        SchemaManagement schemaManagement = KirraContext.getSchemaManagement();
        Entity entity = schemaManagement.getEntity(entityRef);
        ResourceHelper.ensure(entity != null, null, Status.NOT_FOUND);
        
        Relationship relationship = entity.getRelationship(relationshipName);
        ResourceHelper.ensure(relationship != null, null, Status.NOT_FOUND);
        
        List<Instance> domain = KirraContext.getInstanceManagement().getRelatedInstances(entityRef.getNamespace(), entityRef.getTypeName(), objectId, relationshipName, true);
        InstanceList instanceList = new InstanceList(domain);
        return CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).create().toJson(instanceList);
    }
}
