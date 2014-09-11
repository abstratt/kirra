package com.abstratt.kirra.rest.resources;

import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path(Paths.ROOT_PATH)
@Produces("application/json")
public class IndexResource {
    public static class Index {
        public final String applicationName;
        public final URI entities;
        public final URI services;
        public final URI currentUser;
        public final URI uri;

        private Index() {
            this.uri = ResourceHelper.resolve();
            this.applicationName = KirraContext.getSchemaManagement().getApplicationName();
            this.entities = ResourceHelper.resolve(Paths.ENTITIES);
            this.services = ResourceHelper.resolve(Paths.SERVICES);
            Instance currentUser = KirraContext.getInstanceManagement().getCurrentUser();
            if (currentUser != null) {
                URI entityUri = CommonHelper.resolve(KirraContext.getBaseURI(), Paths.ENTITIES, currentUser.getTypeRef().toString());
                URI instanceUri = CommonHelper.resolve(entityUri, Paths.INSTANCES, currentUser.getObjectId());
                this.currentUser = instanceUri;
            } else 
                this.currentUser = null;
        }
    }

    @GET
    public String getIndex() {
        Index index = new Index();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.PRIVATE.ordinal());
        Gson gson = gsonBuilder.create();
        return gson.toJson(index);
    }
}