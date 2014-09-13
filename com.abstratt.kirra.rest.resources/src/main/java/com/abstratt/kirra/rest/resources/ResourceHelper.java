package com.abstratt.kirra.rest.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Instance;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Operation;
import com.abstratt.kirra.Parameter;
import com.abstratt.kirra.TypeRef.TypeKind;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ResourceHelper extends CommonHelper {

    public static void ensure(boolean condition, String message, Status status) {
        if (!condition) {
            if (status == null)
                status = Status.BAD_REQUEST;
            throw new KirraRestException(message, status, null);
        }
    }

    public static void fail(Throwable exception, Status status) {
        throw new KirraRestException(null, status == null ? Status.INTERNAL_SERVER_ERROR : status, exception);
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
        List<Object> argumentList = new ArrayList<Object>();
        
        Map<String, Object> argumentsByAllCapsNames = new LinkedHashMap<String, Object>();
        if (argumentMap != null)
            for (String key : argumentMap.keySet())
                argumentsByAllCapsNames.put(key.toUpperCase(), argumentMap.get(key));
        
        for (Parameter parameter : operation.getParameters()) {
            ResourceHelper.ensure((argumentsByAllCapsNames.containsKey(parameter.getName().toUpperCase())) || !parameter.isRequired(), "Parameter is required: " + parameter.getName(), Status.BAD_REQUEST);
            Object argumentValue = argumentsByAllCapsNames.get(parameter.getName().toUpperCase());
            if (argumentValue != null && parameter.getTypeRef().getKind() == TypeKind.Entity) {
                if (argumentValue instanceof List)
                    argumentValue = ((List) argumentValue).iterator().next();
                Map<String, Object> referenceArgument = (Map<String,Object>) argumentValue;
                String referencedObjectId = (String) referenceArgument.get("objectId");
                if (referenceArgument.containsKey("uri")) {
                    String[] segments = StringUtils.split(referenceArgument.get("uri").toString(), "/");
                    referencedObjectId = segments[segments.length - 1];
                }
                argumentValue = new Instance(parameter.getTypeRef(), referencedObjectId.toString());
            }
            argumentList.add(argumentValue);
        }
        return argumentList;
    }

}
