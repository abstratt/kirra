package com.abstratt.kirra.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.rest.common.Paths;

/**
 * This class only exists so the requests don't fail with 404. Handling of login/logout are done elsewhere.
 */
@Path(Paths.LOGIN_PATH)
@Produces("application/json")
public class LoginResource {
    @GET
    public void login() {
    }
    
    @POST
    public void login(String argumentMapRepresentation) {
    }
}
