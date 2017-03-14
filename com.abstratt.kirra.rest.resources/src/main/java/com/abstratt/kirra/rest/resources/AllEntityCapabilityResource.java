package com.abstratt.kirra.rest.resources;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.EntityCapabilities;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.ALL_ENTITY_CAPABILITIES_PATH)
@Produces("application/json")
public class AllEntityCapabilityResource {
    @GET
    public String getAllEntityCapabilities(@Context UriInfo uriInfo) {
    	List<TypeRef> allEntities = KirraContext.getSchemaManagement().getAllEntityRefs();
    	Map<TypeRef, EntityCapabilities> allCapabilities = KirraContext.getInstanceManagement().getEntityCapabilities(allEntities);
		return CommonHelper.buildGson(uriInfo.getBaseUri()).create().toJson(allCapabilities);
    }
}
