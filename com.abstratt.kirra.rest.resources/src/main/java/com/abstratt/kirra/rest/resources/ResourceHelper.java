package com.abstratt.kirra.rest.resources;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;

import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.rest.common.CommonHelper;
import com.abstratt.kirra.rest.common.KirraContext;

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
}
