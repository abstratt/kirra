package com.abstratt.kirra.rest.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;

@Provider
public class KirraCorsFilter implements ContainerResponseFilter {
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		MultivaluedMap<String, Object> headers = responseContext.getHeaders();
		headers.add("Access-Control-Allow-Origin", getAllowOrigin(requestContext));
		headers.add("Access-Control-Allow-Credentials", getAllowCredentials());
		headers.add("Access-Control-Allow-Headers", StringUtils.join(getAllowHeaders(), ","));
		headers.add("Access-Control-Allow-Methods", StringUtils.join(getAllowMethods(), ","));
	}

	protected List<String> getAllowMethods() {
		return Arrays.asList("GET", "POST","PUT", "DELETE", "OPTIONS", "HEAD");
	}

	protected List<String> getAllowHeaders() {
		return Arrays.asList("origin","content-type", "accept", "authorization", "x-requested-with", "cache-control");
	}

	protected boolean getAllowCredentials() {
		return true;
	}

	protected String getAllowOrigin(ContainerRequestContext requestContext) {
		return requestContext.getHeaderString("Origin");
	}
}