package com.abstratt.kirra.rest.common;

import java.net.URI;
import java.net.URISyntaxException;

import javax.lang.model.element.Modifier;

import org.apache.commons.lang.StringUtils;

import com.abstratt.kirra.Entity;
import com.abstratt.kirra.Instance;
import com.abstratt.kirra.Service;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonHelper {
	public static URI asDirUri(URI dirURI) {
		if (dirURI.getPath().endsWith("/"))
			return dirURI;
		return URI.create(dirURI.toString() + "/");
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
}
