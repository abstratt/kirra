package com.abstratt.kirra.rest.resources;

import java.net.URI;

import javax.lang.model.element.Modifier;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Path(Paths.ROOT_PATH)
@Produces("application/json")
public class IndexResource {
    public static class Index {
        public final String applicationName;
        public final String entities;
        public final String services;
        public final URI uri;

        private Index() {
            this.uri = ResourceHelper.resolve();
            this.applicationName = KirraContext.getSchemaManagement().getApplicationName();
            this.entities = ResourceHelper.resolve(Paths.ENTITIES).toString();
            this.services = ResourceHelper.resolve(Paths.SERVICES).toString();
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
