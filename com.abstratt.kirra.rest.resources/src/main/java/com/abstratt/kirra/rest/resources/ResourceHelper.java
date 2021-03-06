package com.abstratt.kirra.rest.resources;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.InstanceProtocol;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.Property;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceManagement.PageRequest;
import com.abstratt.kirra.InstanceRef;
import com.abstratt.kirra.KirraErrorCode;
import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.abstratt.kirra.rest.common.SerializationHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public class ResourceHelper extends CommonHelper {

    public static void ensure(boolean condition, Status status) {
        ensure(condition, (String) null, status);
    }
    
    public static void ensure(boolean condition, String message, Status status) {
        ensure(condition, () -> message, status);
    }
    
    public static void ensure(boolean condition, Supplier<String> message, Status status) {
        if (!condition) {
            if (status == null)
                status = Status.BAD_REQUEST;
            throw new KirraRestException(message == null ? null : message.get(), status, null);
        }
    }
    
    public static void ensure(boolean condition, KirraErrorCode errorCode) {
        KirraRestException.ensure(condition, errorCode);
    }

	public static void fail(Throwable exception, Status status) {
    	fail(exception, null, status);
    }
    
    public static void fail(Throwable exception, String message, Status status) {
        throw new KirraRestException(message, status == null ? Status.INTERNAL_SERVER_ERROR : status, exception);
    }

    public static URI resolve(boolean directory, String... segments) {
        if (directory && segments.length > 0)
            segments[segments.length - 1] += '/';
        return CommonHelper.resolve(KirraContext.getBaseURI(), segments);
    }

    public static URI resolve(String... segments) {
        return ResourceHelper.resolve(true, segments);
    }

    public static <T extends NamedElement<?>> Map<String, T> toNamedElementMap(List<T> allEntities) {
        LinkedHashMap<String, T> namedContents = new LinkedHashMap<String, T>();
        for (T namedElement : allEntities)
            namedContents.put(namedElement.getName(), namedElement);
        return namedContents;
    }
    


    public static List<Object> matchArgumentsToParameters(Operation operation, Map<String, Object> argumentMap) {
        return matchArgumentsToParameters(operation, argumentMap, null);
    }
    public static List<Object> matchArgumentsToParameters(Operation operation, Map<String, Object> argumentMap, String selectedParameterSet) {
        List<Object> argumentList = new ArrayList<Object>();
        
        ResourceHelper.ensure(selectedParameterSet == null || operation.getParameterSet(selectedParameterSet) != null, () -> "Invalid parameterSet: " + selectedParameterSet, Status.BAD_REQUEST);
        
        Map<String, Object> argumentsByAllCapsNames = new LinkedHashMap<String, Object>();
        if (argumentMap != null)
            for (String key : argumentMap.keySet())
                argumentsByAllCapsNames.put(key.toUpperCase(), argumentMap.get(key));
        
        for (Parameter parameter : operation.getParameters()) {
            if (selectedParameterSet != null)
                if (!parameter.getParameterSets().contains(selectedParameterSet))
                    continue;
            ResourceHelper.ensure((argumentsByAllCapsNames.containsKey(parameter.getName().toUpperCase())) || !parameter.isRequired(), "Parameter is required: " + parameter.getLabel(), Status.BAD_REQUEST);
            Object argumentValue = argumentsByAllCapsNames.get(parameter.getName().toUpperCase());
            if (argumentValue != null && parameter.getTypeRef().getKind() == TypeKind.Entity) {
                if (argumentValue instanceof List)
                    argumentValue = ((List<?>) argumentValue).iterator().next();
                if (parameter.getEffect() == Parameter.Effect.Creation) {
                    Map<String, Object> newInstanceAsMap = (Map<String,Object>) argumentValue;
                    Entity parameterEntity = KirraContext.getSchemaManagement().getEntity(parameter.getTypeRef());
                    Instance newInstance = new Instance(parameter.getTypeRef(), null);
                    newInstance.setValues((Map<String, Object>) newInstanceAsMap.get("values"));
                    Map<String, Instance> argumentLinks = new LinkedHashMap<>();
                    ((Map<String, Object>) newInstanceAsMap.get("links")).forEach((linkName, linkDetails) -> {
                        argumentLinks.put(linkName, new Instance(parameterEntity.getRelationship(linkName).getTypeRef(), ((Map<String, String>) linkDetails).get("objectId")));
                    });
                    newInstance.setLinks(argumentLinks);
                    argumentValue = newInstance;
                } else {
                    if (argumentValue instanceof InstanceProtocol) {
                        String referencedObjectId = ((InstanceProtocol) argumentValue).getObjectId();
                        argumentValue = referencedObjectId != null ? new Instance(parameter.getTypeRef(), referencedObjectId.toString()) : null;
                    } else if (argumentValue instanceof String) {
                        argumentValue = new Instance(parameter.getTypeRef(), (String) argumentValue);
                    } else {
                    	final Object argValue = argumentValue;
                    	KirraRestException.ensure(false, Status.INTERNAL_SERVER_ERROR, () -> ("Unexpected argument type: " + argValue.getClass().getSimpleName() + " for " + argValue));
                    }
                }
            }
            argumentList.add(argumentValue);
        }
        return argumentList;
    }
    

    public static PageRequest processQuery(UriInfo uriInfo, BiConsumer<String, List<Object>> argumentConsumer) {
        boolean includeSubtypes = false;
        int defaultFirst = 0;
        long first = defaultFirst;
        int defaultMaximum = Integer.MAX_VALUE;
        int maximum = defaultMaximum;
        DataProfile dataProfile = DataProfile.Full;
        for (Entry<String, List<String>> entry : uriInfo.getQueryParameters().entrySet())
            if (entry.getKey().equals("includesubtypes")) {
                // carefully avoiding Restlet bug #640 https://github.com/restlet/restlet-framework-java/issues/640
                includeSubtypes = Boolean.parseBoolean(entry.getValue().stream().findAny().orElse(Boolean.FALSE.toString()));                       
            } else if (entry.getKey().equalsIgnoreCase("first")) {
                first = entry.getValue().stream().map(it -> NumberUtils.toLong(it, defaultFirst)).findAny().orElse(first);                       
            } else if (entry.getKey().equalsIgnoreCase("maximum")) {
                maximum = entry.getValue().stream().map(it -> NumberUtils.toInt(it, defaultMaximum)).findAny().orElse(maximum);
            } else if (entry.getKey().equalsIgnoreCase("dataprofile")) {
                dataProfile = entry.getValue().stream().map(it -> InstanceManagement.DataProfile.from(it)).findAny().orElse(dataProfile);             
            } else
                argumentConsumer.accept(entry.getKey(), new ArrayList<Object>(entry.getValue()));
        PageRequest pageRequest = new PageRequest(first, maximum, dataProfile, includeSubtypes);
        return pageRequest;
    }

	public static Map<String, Object> deserializeArguments(String argumentMapRepresentation,
			List<Parameter> parameters) {
		Map<String, Parameter> parametersAsMap = parameters.stream().collect(Collectors.toMap(it -> it.getName(), it -> it));
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Map.class, new JsonDeserializer<Map<String, Object>>() {
			@Override
			public Map<String, Object> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
					throws JsonParseException {
				JsonObject asJsonObject = jsonElement.getAsJsonObject();
				Map<String, Object> result = new LinkedHashMap<>();
				for (Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
	                Parameter parameter = parametersAsMap.get(entry.getKey());
	                Type targetType = SerializationHelper.getTargetType(parameter.getTypeRef());
					Object value = context.deserialize(entry.getValue(), targetType);
	                result.put(parameter.getName(), value);
				}
				return result;
			}
		});
		CommonHelper.registerDefaultSerializers(gsonBuilder);
		Gson gson = gsonBuilder.create();
		Map<String, Object> fromJson = gson.fromJson(argumentMapRepresentation, new TypeToken<Map<String, Object>>() {
        }.getType());
		return fromJson;		
	}

}
