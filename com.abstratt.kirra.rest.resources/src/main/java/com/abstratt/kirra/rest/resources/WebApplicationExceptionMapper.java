package com.abstratt.kirra.rest.resources;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.rest.common.ErrorDTO;
import com.google.gson.Gson;

@Provider
public class WebApplicationExceptionMapper extends AbstractExceptionMapper<WebApplicationException> {
	@Override
	protected Response exceptionToResponse(WebApplicationException exception) {
		return Response.status(exception.getResponse().getStatus()).type(MediaType.APPLICATION_JSON).entity(new Gson().toJson(new ErrorDTO(exception.getMessage(), Kind.INTERNAL))).build();
	}
}