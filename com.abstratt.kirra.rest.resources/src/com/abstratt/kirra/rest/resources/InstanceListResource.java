package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCES_PATH)
public class InstanceListResource {
	private class InstanceList extends Page<Instance> {
		public InstanceList(List<Instance> contents) {
			super(contents);
		}
	}
	@GET
	public String getInstances(@PathParam("entityName") String entityName) {
		TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
		List<Instance> allInstances = KirraContext.getInstanceManagement().getInstances(entityRef.getEntityNamespace(), entityRef.getTypeName(), false);
		InstanceList instanceList = new InstanceList(allInstances);
		return ResourceHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES)).toJson(instanceList);
	} 
	@POST
	public Instance createInstance(@PathParam("entityName") String entityName, Instance newInstance) {
		Instance created = KirraContext.getInstanceManagement().createInstance(newInstance);
		return created;
	}
}
