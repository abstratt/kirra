package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.ENTITY_ACTION_PARAMETER_DOMAIN_PATH)
public class EntityActionParameterDomainResource extends ActionParameterDomainResource {
    @GET
    public String listEntityParameterDomain(@PathParam("entityName") String entityName, @PathParam("actionName") String actionName, @PathParam("parameterName") String parameterName) {
        return listParameterDomain(entityName, actionName, parameterName, null);
    }
}
