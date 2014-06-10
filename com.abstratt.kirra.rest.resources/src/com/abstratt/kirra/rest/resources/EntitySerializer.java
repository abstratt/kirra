package com.abstratt.kirra.rest.resources;

import java.lang.reflect.Type;
import java.net.URI;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.rest.common.Paths;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class EntitySerializer extends TopLevelElementSerializer<Entity> {

	public EntitySerializer(URI uri) {
	    super(uri);
	}

	@Override
	public JsonElement serialize(Entity element, Type type, JsonSerializationContext context) {
		JsonObject asJson = (JsonObject) super.serialize(element, type, context);
		URI entityUri = getTopLevelURI(element);
		asJson.addProperty("extentUri", ResourceHelper.resolve(entityUri, Paths.INSTANCES).toString());
		return asJson;
	}

}