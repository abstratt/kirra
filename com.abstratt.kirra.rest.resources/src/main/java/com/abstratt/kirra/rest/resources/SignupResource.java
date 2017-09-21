package com.abstratt.kirra.rest.resources;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
@Path(Paths.SIGNUP_PATH)
@Produces("application/json")
public class SignupResource {
    @POST
    public Response signup(@Context HttpHeaders headers, @PathParam("roleEntityName") String entityName, String newRoleInstanceRepresentation) {
    	InstanceManagement instanceManagement = KirraContext.getInstanceManagement();
    	TypeRef entityRef = new TypeRef(entityName, TypeRef.TypeKind.Entity);
        AuthorizationHelper.checkInstanceCreationAuthorized(entityRef);
    	
    	ResourceHelper.ensure(instanceManagement.getCurrentUser() == null, "Already logged in", Status.BAD_REQUEST);
    	Instance newUserProfile = new Instance("userprofile", "UserProfile");
    	
    	List<String> credentialPairs = headers.getRequestHeader("X-Kirra-Credentials");
    	ResourceHelper.ensure(!credentialPairs.isEmpty(), "missing credentials", Status.BAD_REQUEST);
    	String decodedCredentials = new String(Base64.getDecoder().decode(credentialPairs.get(0)), StandardCharsets.UTF_8);
    	String[] credentials = decodedCredentials.split(":");
    	ResourceHelper.ensure(credentials.length == 2, "invalid format for credentials", Status.BAD_REQUEST);
    	String username = StringUtils.trimToNull(credentials[0]);
    	String password = StringUtils.trimToNull(credentials[1]);
    	ResourceHelper.ensure(username != null, "username is required", Status.BAD_REQUEST);
    	ResourceHelper.ensure(username.length() >= 6, "email is not valid", Status.BAD_REQUEST);
    	ResourceHelper.ensure(!username.startsWith("@") && !username.endsWith("@"), "email is not valid", Status.BAD_REQUEST);
        ResourceHelper.ensure(username.chars().filter(it -> it == '@').count() == 1, "email is not valid", Status.BAD_REQUEST);
    	ResourceHelper.ensure(password != null, "password is required", Status.BAD_REQUEST);
    	ResourceHelper.ensure(password.length() >= 4, "password must be at least 4 chars long", Status.BAD_REQUEST);
    	ResourceHelper.ensure(password.length() <= 256, "password is too long", Status.BAD_REQUEST);
		newUserProfile.setValue("username", username);
		newUserProfile.setValue("password", new String(password));
		
    	Instance createdUser = instanceManagement.createUser(newUserProfile);
    	Instance roleToCreate = CommonHelper.buildGson(null).create().fromJson(newRoleInstanceRepresentation, Instance.class);
    	roleToCreate.setEntityNamespace(entityRef.getEntityNamespace());
    	roleToCreate.setEntityName(entityRef.getTypeName());
    	roleToCreate.setRelated("userProfile", createdUser);
    	Instance createdRole = instanceManagement.createInstance(roleToCreate);
    	String json = CommonHelper.buildGson(ResourceHelper.resolve(true, Paths.ENTITIES, entityName, Paths.INSTANCES, createdRole.getObjectId()))
                .create().toJson(createdRole);
		return Response.status(Status.CREATED).entity(json).type(MediaType.APPLICATION_JSON).build();
    }

}
