package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;

import com.abstratt.kirra.TypeRef;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TypeRefSerializer implements JsonSerializer<TypeRef>  {

    public static final TypeRefSerializer INSTANCE = new TypeRefSerializer();
    private Gson gson;

    private TypeRefSerializer() {
        this.gson = new Gson();
    }

    @Override
    public JsonElement serialize(TypeRef typeRef, Type type, JsonSerializationContext context) {
        JsonObject asJson = (JsonObject) gson.toJsonTree(typeRef);
        asJson.addProperty("fullName", typeRef.getFullName());
        return asJson;
    }

}
