package com.abstratt.kirra.rest.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.rest.common.ErrorDTO;
import com.google.gson.Gson;

@Provider
public class ThrowableMapper extends AbstractExceptionMapper<Throwable> {
	@Override
	protected Response exceptionToResponse(Throwable exception) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).type(MediaType.APPLICATION_JSON).entity(new Gson().toJson(new ErrorDTO(exception.getMessage(), Kind.INTERNAL))).build();
	}
}