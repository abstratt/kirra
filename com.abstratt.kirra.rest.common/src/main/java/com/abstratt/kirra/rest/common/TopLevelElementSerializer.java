package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;

import com.abstratt.kirra.TopLevelElement;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TopLevelElementSerializer<T extends TopLevelElement> implements JsonSerializer<T> {

    protected URI uri;

    public TopLevelElementSerializer(URI uri) {
        this.uri = uri;
    }

    @Override
    public JsonElement serialize(T element, Type type, JsonSerializationContext context) {
        JsonObject asJson = (JsonObject) new Gson().toJsonTree(element);
        asJson.addProperty("uri", getTopLevelURI(element).toString());
        return asJson;
    }

    protected URI getTopLevelURI(T element) {
        return CommonHelper.resolve(uri, element.getTypeRef().toString());
    }

}