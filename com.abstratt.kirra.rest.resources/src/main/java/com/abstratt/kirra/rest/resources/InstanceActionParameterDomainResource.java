package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.INSTANCE_ACTION_PARAMETER_DOMAIN_PATH)
public class InstanceActionParameterDomainResource extends ActionParameterDomainResource {
	@Produces("application/json")
	@GET
    public String listInstanceParameterDomain(@PathParam("entityName") String entityName, @PathParam("objectId") String objectId, @PathParam("actionName") String actionName, @PathParam("parameterName") String parameterName) {
        return listParameterDomain(entityName, actionName, parameterName, objectId);
    }
}
