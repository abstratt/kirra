package com.abstratt.kirra.rest.resources;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.KirraContext.Options;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path(Paths.ROOT_PATH)
@Produces("application/json")
public class IndexResource {
    public static class Index {
        public final String applicationName;
        public final String applicationLabel;
        public final String applicationLogo;
        public final URI entities;
        public final URI entityCapabilities;
        public final URI services;
        public final URI currentUser;
        public final URI session;
        public final Map<String, URI> currentUserRoles;
        public final URI uri;
        public final KirraContext.Options options;

        private Index() {
            this.uri = ResourceHelper.resolve();
            this.applicationName = KirraContext.getSchemaManagement().getApplicationName();
            this.applicationLabel = KirraContext.getSchemaManagement().getApplicationLabel();
            this.applicationLogo = KirraContext.getSchemaManagement().getApplicationLogo();
            this.entityCapabilities = ResourceHelper.resolve(Paths.CAPABILITIES);
            this.entities = ResourceHelper.resolve(Paths.ENTITIES);
            this.services = ResourceHelper.resolve(Paths.SERVICES);
            this.session = ResourceHelper.resolve(Paths.SESSION);
            Instance currentUser = KirraContext.getInstanceManagement().getCurrentUser();
            List<Instance> currentUserRoles = KirraContext.getInstanceManagement().getCurrentUserRoles();
            if (currentUser != null) {
                URI instanceUri = extractInstanceURI(currentUser);
                this.currentUser = instanceUri;
            } else {
                this.currentUser = null;
            }
            if (currentUserRoles != null) {
            	this.currentUserRoles = currentUserRoles.stream()
            			.collect(Collectors.toMap(r -> r.getTypeRef().toString(), r-> extractInstanceURI(r)));
            } else {
            	this.currentUserRoles = null;
            }
            this.options = KirraContext.getOptions();
        }

    }

    private static URI extractInstanceURI(Instance currentUser) {
    	URI entityUri = CommonHelper.resolve(KirraContext.getBaseURI(), Paths.ENTITIES, currentUser.getTypeRef().toString());
    	URI instanceUri = CommonHelper.resolve(entityUri, Paths.INSTANCES, currentUser.getObjectId());
    	return instanceUri;
    }
    @GET
    public String getIndex() {
        Index index = new Index();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.PRIVATE.ordinal());
        Gson gson = gsonBuilder.create();
        return gson.toJson(index);
    }
    
    @GET
    @Path(Paths.SESSION_PATH)
    public Response getSession() {
    	Instance currentUser = KirraContext.getInstanceManagement().getCurrentUser();
        if (currentUser != null) {
        	URI userUri = extractInstanceURI(currentUser);
        	return Response.temporaryRedirect(userUri).build();
        }
        return Response.status(Status.FORBIDDEN).build();
    }
}