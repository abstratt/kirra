package com.abstratt.kirra.rest.resources;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseLoggingInterceptor implements ContainerResponseFilter {
	private static Logger logger = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		logger.debug("{} {} -> {}", requestContext.getMethod(), requestContext.getUriInfo().getPath(), responseContext.getStatus());
	}
	
}
