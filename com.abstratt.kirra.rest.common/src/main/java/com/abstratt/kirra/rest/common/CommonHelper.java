package com.abstratt.kirra.rest.common;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
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
    
    public interface DateTimeSerialization<T extends TemporalAccessor> extends JsonDeserializer<T>, JsonSerializer<T> {
    	public DateTimeFormatter[] formats();
    	
		public default JsonElement serialize(T arg0, Type arg1, JsonSerializationContext arg2) {
			if (arg0 == null)
				return null;
			return new JsonPrimitive(
					formats()[0].format(arg0));
		}
    	
		public default T deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
				throws JsonParseException {
			if (arg0 == null)
				return null;
			DateTimeParseException first = null;
			for (DateTimeFormatter format : formats()) {
				try {
					return parse(arg0.getAsString(), format);
				} catch (DateTimeParseException e) {
					if (first == null) 
						first = e;
				}
			}
			throw first;
		}

		public T parse(String asString, DateTimeFormatter format);
    	
    }

	public static class LocalDateTimeSerialization implements DateTimeSerialization<LocalDateTime> {
		public final static DateTimeFormatter[] FORMATS = {DateTimeFormatter.ISO_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_INSTANT};
		@Override
		public LocalDateTime parse(String asString, DateTimeFormatter format) {
			return LocalDateTime.parse(asString, format);
		}
		@Override
		public DateTimeFormatter[] formats() {
			return FORMATS;
		}
	}
	
	public static class LocalTimeSerialization implements DateTimeSerialization<LocalTime> {
		public final static DateTimeFormatter[] FORMATS = {DateTimeFormatter.ISO_TIME, DateTimeFormatter.ISO_LOCAL_TIME, DateTimeFormatter.ISO_OFFSET_TIME, DateTimeFormatter.ISO_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_INSTANT};
		@Override
		public DateTimeFormatter[] formats() {
			return FORMATS; 
		}
		@Override
		public LocalTime parse(String asString, DateTimeFormatter format) {
			return LocalDateTime.parse(asString, format).toLocalTime();
		}
	}
	
	public static class LocalDateSerialization implements DateTimeSerialization<LocalDate> {
		public final static DateTimeFormatter[] FORMATS = {DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ISO_OFFSET_DATE, DateTimeFormatter.ISO_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_INSTANT};
		@Override
		public DateTimeFormatter[] formats() {
			return FORMATS; 
		}
		@Override
		public LocalDate parse(String asString, DateTimeFormatter format) {
			return LocalDate.parse(asString, format);
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
