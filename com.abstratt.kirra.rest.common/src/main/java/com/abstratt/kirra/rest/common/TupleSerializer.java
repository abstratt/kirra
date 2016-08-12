package com.abstratt.kirra.rest.common;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Tuple;
import com.abstratt.kirra.TypeRef;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TupleSerializer implements JsonSerializer<Tuple> {

    public TupleSerializer() {
    }
    
    private static final List<String> EXCLUDED_FIELDS = Arrays.<String>asList(new String[] {
            "typeRef"});

    @Override
    public JsonElement serialize(Tuple  tuple, Type type, JsonSerializationContext context) {
        Gson gson = CommonHelper.buildBasicGson().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipClass(Class<?> arg0) {
                return false;
            }
            @Override
            public boolean shouldSkipField(FieldAttributes arg0) {
                return EXCLUDED_FIELDS.contains(arg0.getName());
            }
            
        }).create();
        JsonObject asJson = (JsonObject) gson.toJsonTree(tuple.getValues());
        return asJson;
    }
}