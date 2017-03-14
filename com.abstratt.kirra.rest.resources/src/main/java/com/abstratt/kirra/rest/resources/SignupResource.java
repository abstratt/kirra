package com.abstratt.kirra.rest.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.SIGNUP_PATH)
@Produces("application/json")
public class SignupResource {
    @POST
    @Consumes("application/json")
    public void signup(String profileRepresentation) {
    	InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
    	Instance userInfo = CommonHelper.buildGson(null).create().fromJson(profileRepresentation, Instance.class);
    	Instance createdUser = instanceManagement.createUser(userInfo);
    }
}
