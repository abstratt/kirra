package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;

import com.abstratt.kirra.Entity;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class EntitySerializer extends TopLevelElementSerializer<Entity> {

    public EntitySerializer(URI uri) {
        super(uri);
    }

    @Override
    public JsonElement serialize(Entity element, Type type, JsonSerializationContext context) {
        JsonObject basicValues = (JsonObject) super.serialize(element, type, context);
        JsonObject asJson = new JsonObject();
        URI entityUri = getTopLevelURI(element);
        asJson.addProperty("fullName", element.getTypeRef().getFullName());
        asJson.addProperty("extentUri", CommonHelper.resolve(entityUri, Paths.INSTANCES + "/").toString());
        asJson.addProperty("entityCapabilityUri", CommonHelper.resolve(entityUri, Paths.CAPABILITIES + "/").toString());
        asJson.addProperty("instanceCapabilityUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.CAPABILITIES).toString());
        basicValues.entrySet().forEach((entry) -> asJson.add(entry.getKey(), entry.getValue()));
        asJson.addProperty("entityActionUriTemplate", CommonHelper.resolve(entityUri, Paths.ACTIONS, "(actionName)").toString());        
        asJson.addProperty("instanceUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)").toString());
        asJson.addProperty("instanceActionUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.ACTIONS, "(actionName)").toString());
        asJson.addProperty("relationshipDomainUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)", Paths.DOMAIN).toString());
        asJson.addProperty("relatedInstancesUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)").toString() + "?includesubtypes=true");
        asJson.addProperty("relatedInstanceUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)", "(relatedObjectId)").toString());        
        asJson.addProperty("instanceActionParameterDomainUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.ACTIONS, "(actionName)", Paths.PARAMETERS, "(parameterName)", Paths.DOMAIN).toString());
        asJson.addProperty("finderUriTemplate", CommonHelper.resolve(entityUri, Paths.FINDERS, "(finderName)").toString());
        return asJson;
    }

}