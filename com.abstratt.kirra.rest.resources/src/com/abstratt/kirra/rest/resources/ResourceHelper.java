package com.abstratt.kirra.rest.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.NamedElement;
import com.abstratt.kirra.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ResourceHelper {
	public static URI asDirUri(URI dirURI) {
		if (dirURI.getPath().endsWith("/"))
			return dirURI;
		return URI.create(dirURI.toString() + "/");
	}
	
	public static URI resolve(URI base, String... segments) {
		if (!base.getPath().endsWith("/"))
			try {
				base = new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), base.getPath() + "/", base.getQuery(), base.getFragment());
			} catch (URISyntaxException e) {
				// not expected
				throw new RuntimeException(e);
			}
		return base.resolve(StringUtils.join(segments, "/"));
		
	}

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

	public static Gson buildGson(URI baseURI) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		gsonBuilder.setPrettyPrinting();
		gsonBuilder.excludeFieldsWithModifiers(Modifier.PRIVATE.ordinal());
		gsonBuilder.registerTypeAdapter(Entity.class, new EntitySerializer(baseURI));
		gsonBuilder.registerTypeAdapter(Service.class, new TopLevelElementSerializer<Service>(baseURI));
		gsonBuilder.registerTypeAdapter(Instance.class, new InstanceSerializer(baseURI));
		return gsonBuilder.create();
	}


	public static <T extends NamedElement<?>> Map<String, T> toNamedElementMap(List<T> allEntities) {
		LinkedHashMap<String, T> namedContents = new LinkedHashMap<String, T>();
		for (T namedElement : allEntities)
			namedContents.put(namedElement.getName(), namedElement);
		return namedContents;
	}

	public static URI resolve(boolean directory, String... segments) {
		if (directory && segments.length > 0)
			segments[segments.length-1] += '/';
		return resolve(KirraContext.getBaseURI(), segments);
	}

	public static URI resolve(String... segments) {
		return resolve(true, segments);
	}
}
