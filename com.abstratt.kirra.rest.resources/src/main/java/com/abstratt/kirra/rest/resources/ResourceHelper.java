package com.abstratt.kirra.rest.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.InstanceManagement;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.InstanceManagement.DataProfile;
import com.abstratt.kirra.InstanceManagement.PageRequest;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;

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
            ResourceHelper.ensure((argumentsByAllCapsNames.containsKey(parameter.getName().toUpperCase())) || !parameter.isRequired(), "Parameter is required: " + parameter.getName(), Status.BAD_REQUEST);
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
                    if (argumentValue instanceof Map) {
                        Map<String, Object> referenceArgument = (Map<String,Object>) argumentValue;
                        String referencedObjectId = (String) referenceArgument.get("objectId");
                        if (referenceArgument.containsKey("uri")) {
                            String[] segments = StringUtils.split(referenceArgument.get("uri").toString(), "/");
                            referencedObjectId = segments[segments.length - 1];
                        }
                        argumentValue = referencedObjectId != null ? new Instance(parameter.getTypeRef(), referencedObjectId.toString()) : null;
                    } else {
                        argumentValue = new Instance(parameter.getTypeRef(), (String) argumentValue);
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
        int defaultMaximum = 10;
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

}
