package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.Service;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.SERVICES_PATH)
@Produces("application/json")
public class ServiceListResource {
    @GET
    public String getServices() {
        List<Service> allServices = KirraContext.getSchemaManagement().getAllServices();
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.SERVICES)).create().toJson(allServices);
    }
}
