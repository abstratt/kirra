package com.abstratt.kirra.rest.common;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Blob;
import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Service;
import com.abstratt.kirra.Tuple;
import com.abstratt.kirra.TypeRef;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CommonHelper {
	public static URI asDirUri(URI dirURI) {
        if (dirURI.getPath().endsWith("/"))
            return dirURI;
        return URI.create(dirURI.toString() + "/");
    }

    public static GsonBuilder buildGson(URI baseURI) {
        Map<Class<?>, Object> map = new LinkedHashMap<Class<?>, Object>();
        map.put(Entity.class, new EntitySerializer(baseURI));
        map.put(Service.class, new TopLevelElementSerializer<Service>(baseURI));
        map.put(Instance.class, new InstanceSerializer());
        map.put(Tuple.class, new TupleSerializer());
        map.put(TypeRef.class, TypeRefSerializer.INSTANCE);
        return buildGson(baseURI, map);
    }
    
    public static GsonBuilder buildBasicGson() {
        return CommonHelper.buildGson(null, Collections.<Class<?>, Object>singletonMap(TypeRef.class, (Object) TypeRefSerializer.INSTANCE));
    }
    
    public static GsonBuilder buildGson(URI baseURI, Map<Class<?>, ?> adapters) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.setPrettyPrinting();
        registerDefaultSerializers(gsonBuilder);
        gsonBuilder.excludeFieldsWithModifiers(Modifier.PRIVATE);
        gsonBuilder.excludeFieldsWithModifiers(Modifier.STATIC);
        for (Map.Entry<Class<?>, ?> entry : adapters.entrySet()) 
            gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        return gsonBuilder;
    }

	protected static void registerDefaultSerializers(GsonBuilder gsonBuilder) {
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerialization());
        gsonBuilder.registerTypeAdapter(LocalTime.class, new LocalTimeSerialization());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerialization());
        gsonBuilder.registerTypeAdapter(byte[].class, new ByteArraySerialization());
        gsonBuilder.registerTypeAdapter(Blob.class, new BlobSerialization());
	}

    public static URI resolve(URI base, String... segments) {
        if (!base.getPath().endsWith("/"))
            try {
                base = new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), base.getPath() + "/", base.getQuery(),
                        base.getFragment());
            } catch (URISyntaxException e) {
                // not expected
                throw new RuntimeException(e);
            }
        return base.resolve(StringUtils.join(segments, "/"));
    }
    

	public static class LocalDateTimeSerialization implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {
		@Override
		public JsonElement serialize(LocalDateTime arg0, Type arg1, JsonSerializationContext arg2) {
			if (arg0 == null)
				return null;
			return new JsonPrimitive(
					DateTimeFormatter.ISO_DATE.format(arg0));
		}
		
		@Override
		public LocalDateTime deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			if (arg0 == null)
				return null;
			return LocalDateTime.parse(arg0.getAsString(), DateTimeFormatter.ISO_DATE);
		}
	}
	
	public static class LocalTimeSerialization implements JsonDeserializer<LocalTime>, JsonSerializer<LocalTime> {
		@Override
		public JsonElement serialize(LocalTime arg0, Type arg1, JsonSerializationContext arg2) {
			if (arg0 == null)
				return null;
			return new JsonPrimitive(
					DateTimeFormatter.ISO_TIME.format(arg0));
		}
		
		@Override
		public LocalTime deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			if (arg0 == null)
				return null;
			return LocalTime.parse(arg0.getAsString(), DateTimeFormatter.ISO_TIME);
		}
	}
	
	public static class LocalDateSerialization implements JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {
		@Override
		public JsonElement serialize(LocalDate arg0, Type arg1, JsonSerializationContext arg2) {
			if (arg0 == null)
				return null;
			return new JsonPrimitive(
					DateTimeFormatter.ISO_DATE.format(arg0));
		}
		
		@Override
		public LocalDate deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			if (arg0 == null)
				return null;
			return LocalDate.parse(arg0.getAsString(), DateTimeFormatter.ISO_DATE);
		}
	}		
	
    public static class BlobSerialization implements JsonDeserializer<Blob>, JsonSerializer<Blob> {
        @Override
        public JsonElement serialize(Blob arg0, Type arg1, JsonSerializationContext arg2) {
            if (arg0 == null)
                return null;
            return arg2.serialize(arg0.toMap());
        }
        
        @Override
        public Blob deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
                throws JsonParseException {
            if (arg0 == null)
                return null;
            return Blob.fromMap(arg2.deserialize(arg0, Map.class));
        }
    }   
	
	public static class ByteArraySerialization implements JsonDeserializer<byte[]>, JsonSerializer<byte[]> {
		@Override
		public JsonElement serialize(byte[] src, Type arg1, JsonSerializationContext arg2) {
			if (src == null)
				return null;
			return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
		}
		
		@Override
		public byte[] deserialize(JsonElement src, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			if (src == null)
				return null;
			return Base64.getDecoder().decode(src.getAsString());
		}
	}

    
}
