package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Service;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.SERVICE_PATH)
@Produces("application/json")
public class ServiceResource {
    @GET
    public String getEntity(@PathParam("serviceName") String serviceName) {

        TypeRef typeRef = new TypeRef(serviceName, TypeKind.Service);
        Service service = KirraContext.getSchemaManagement().getService(typeRef);
        ResourceHelper.ensure(service != null, null, Status.NOT_FOUND);
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.SERVICES)).create().toJson(service);
    }
}
