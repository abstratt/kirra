package com.abstratt.kirra.rest.resources;

import java.lang.reflect.Type;
import java.net.URI;

import com.abstratt.kirra.Instance;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class InstanceSerializer implements JsonSerializer<Instance> {

	private URI instancesUri;

	public InstanceSerializer(URI uri) {
		this.instancesUri = uri;
	}

	@Override
	public JsonElement serialize(Instance element, Type type, JsonSerializationContext context) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mmZ");
		JsonObject asJson = (JsonObject) gsonBuilder.create().toJsonTree(element);
		asJson.addProperty("uri", ResourceHelper.resolve(instancesUri, element.getObjectId()).toString());
		asJson.addProperty("entityUri", ResourceHelper.resolve(instancesUri, "..").toString());
		return asJson;
	}

}