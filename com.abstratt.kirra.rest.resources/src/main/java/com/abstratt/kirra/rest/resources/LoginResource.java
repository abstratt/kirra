package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

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
    public void login() {
    	ensureLoggedIn();
    }


	@POST
    public void login(String argumentMapRepresentation) {
		ensureLoggedIn();
    }
	
    
    private void ensureLoggedIn() {
    	Instance currentUser = KirraContext.getInstanceManagement().getCurrentUser();
    	ResourceHelper.ensure(currentUser != null, "User not logged in", Status.UNAUTHORIZED);
	}	
}
