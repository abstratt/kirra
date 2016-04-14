package com.abstratt.kirra.rest.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.rest.common.Paths;

/**
 * This class only exists so the requests don't fail with 404. Handling of login/logout are done elsewhere.
 */
@Path(Paths.LOGOUT_PATH)
@Produces("application/json")
public class LogoutResource {
    @GET
    public Response logout(@Context UriInfo uriInfo) {
    	URI requestUri = uriInfo.getRequestUri();
    	boolean trailingSlash = requestUri.getPath().endsWith("/");
    	URI applicationUri = trailingSlash ? requestUri.resolve("..") : requestUri.resolve(".");
		return Response.temporaryRedirect(applicationUri).build();
    }
}
