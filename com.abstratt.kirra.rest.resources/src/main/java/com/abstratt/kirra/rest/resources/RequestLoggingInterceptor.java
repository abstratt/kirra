package com.abstratt.kirra.rest.resources;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLoggingInterceptor implements ContainerRequestFilter {
	private static Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

	@Override
	public void filter(ContainerRequestContext arg) throws IOException {
		logger.debug("{} {}", arg.getMethod(), arg.getUriInfo().getPath());
	}
	
}
