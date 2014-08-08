package com.abstratt.kirra.rest.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.Paths;

@Path(Paths.ENTITIES_PATH)
@Produces("application/json")
public class EntityListResource {
    @GET
    public String getEntities() {
        List<Entity> allEntities = KirraContext.getSchemaManagement().getAllEntities();
        return CommonHelper.buildGson(ResourceHelper.resolve(Paths.ENTITIES)).create().toJson(allEntities);
    }
}
