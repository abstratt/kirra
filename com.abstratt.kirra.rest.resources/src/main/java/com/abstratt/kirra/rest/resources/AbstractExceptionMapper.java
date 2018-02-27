package com.abstratt.kirra.rest.resources;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExceptionMapper<E extends Throwable> implements ExceptionMapper<E>{
	private static Logger logger = LoggerFactory.getLogger(AbstractExceptionMapper.class);
	@Override
	public final Response toResponse(E e) {
		logger.error("Exception caught", e);
		return exceptionToResponse(e);
	}

	protected abstract Response exceptionToResponse(E e);

}
