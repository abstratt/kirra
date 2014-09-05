package com.abstratt.kirra.rest.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Service;
import com.abstratt.kirra.TypeRef;
import com.google.gson.GsonBuilder;

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
        map.put(TypeRef.class, TypeRefSerializer.INSTANCE);
        return buildGson(baseURI, map);
    }
    
    public static GsonBuilder buildBasicGson() {
        return CommonHelper.buildGson(null, Collections.<Class<?>, Object>singletonMap(TypeRef.class, (Object) TypeRefSerializer.INSTANCE));
    }
    
    public static GsonBuilder buildGson(URI baseURI, Map<Class<?>, ?> adapters) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.excludeFieldsWithModifiers(Modifier.PRIVATE.ordinal());
        for (Map.Entry<Class<?>, ?> entry : adapters.entrySet()) 
            gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        return gsonBuilder;
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
}
