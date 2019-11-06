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
        String extentUri = CommonHelper.resolve(entityUri, Paths.INSTANCES + "/").toString();
        asJson.addProperty("extentUri", extentUri);
        asJson.addProperty("extentUriTemplate", extentUri + "?" + Paths.DATA_SELECTORS);
        asJson.addProperty("extentMetricUri", CommonHelper.resolve(entityUri, Paths.INSTANCES, Paths.METRICS).toString());
        asJson.addProperty("entityCapabilityUri", CommonHelper.resolve(entityUri, Paths.CAPABILITIES + "/").toString());
        asJson.addProperty("instanceCapabilityUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.CAPABILITIES).toString());
        basicValues.entrySet().forEach((entry) -> asJson.add(entry.getKey(), entry.getValue()));
        asJson.addProperty("entityActionUriTemplate", CommonHelper.resolve(entityUri, Paths.ACTIONS, "(actionName)", "?parameterSet=(parameterSet)").toString());        
        asJson.addProperty("instanceUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)").toString());
        asJson.addProperty("instanceTemplateUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "_template").toString());
        asJson.addProperty("instanceNewBlobUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.BLOBS, "(propertyName)").toString());
        asJson.addProperty("instanceExistingBlobUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.BLOBS, "(propertyName)", "(token)").toString());        
        asJson.addProperty("instanceActionUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.ACTIONS, "(actionName)", "?parameterSet=(parameterSet)").toString());
        asJson.addProperty("relationshipDomainUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)", Paths.DOMAIN).toString());
        asJson.addProperty("relatedInstancesUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)").toString() + "?includesubtypes=true");
        asJson.addProperty("relatedInstanceUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.RELATIONSHIPS, "(relationshipName)", "(relatedObjectId)").toString());        
        asJson.addProperty("instanceActionParameterDomainUriTemplate", CommonHelper.resolve(entityUri, Paths.INSTANCES, "(objectId)", Paths.ACTIONS, "(actionName)", Paths.PARAMETERS, "(parameterName)", Paths.DOMAIN).toString());
        asJson.addProperty("entityActionParameterDomainUriTemplate", CommonHelper.resolve(entityUri, Paths.ACTIONS, "(actionName)", Paths.PARAMETERS, "(parameterName)", Paths.DOMAIN).toString());
        asJson.addProperty("finderUriTemplate", CommonHelper.resolve(entityUri, Paths.FINDERS, "(finderName)").toString() + "?" + Paths.DATA_SELECTORS);
        asJson.addProperty("finderMetricUriTemplate", CommonHelper.resolve(entityUri, Paths.FINDERS, "(finderName)", Paths.METRICS).toString());
        return asJson;
    }

}