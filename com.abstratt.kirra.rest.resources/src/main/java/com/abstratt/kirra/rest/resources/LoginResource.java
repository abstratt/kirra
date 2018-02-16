package com.abstratt.kirra.rest.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

/**
 * This class only exists so the requests don't fail with 404. Handling of login/logout are done elsewhere.
 */
@Path(Paths.LOGIN_PATH)
@Produces("application/json")
public class LoginResource {
    @GET
    public Response loginAsGET(@Context UriInfo uriInfo) {
    	ensureLoggedIn();
		return Response.ok().build();
    }


	@POST
	@Consumes("application/json")
    public Response login(@Context UriInfo uriInfo, String argumentMapRepresentation) {
		ensureLoggedIn();
		return Response.ok().build();
    }
	
	@POST
    public Response loginAsEmptyPOST(@Context UriInfo uriInfo) {
		ensureLoggedIn();
		return Response.ok().build();
    }
	
    
    private Response response() {
		return Response.temporaryRedirect(KirraContext.getBaseURI()).build();
	}

	private void ensureLoggedIn() {
    	Instance currentUser = KirraContext.getInstanceManagement().getCurrentUser();
    	ResourceHelper.ensure(currentUser != null, "login_failed", Status.UNAUTHORIZED);
	}	
}
