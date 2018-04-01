package com.abstratt.kirra.rest.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.abstratt.kirra.KirraException;
import com.abstratt.kirra.KirraException.Kind;
import com.abstratt.kirra.rest.common.ErrorDTO;
import com.google.gson.Gson;

@Provider
public class KirraExceptionMapper extends AbstractExceptionMapper<KirraException> {
	private Status getStatus(Kind kind) {
		switch (kind) {
		case OBJECT_NOT_FOUND:
		case ELEMENT_NOT_FOUND:
			return Status.NOT_FOUND;
		case VALIDATION:
		case SCHEMA:
			return Status.BAD_REQUEST;
		case EXTERNAL:
			return Status.SERVICE_UNAVAILABLE;
		default:
			return Status.INTERNAL_SERVER_ERROR;
		}
	}

	@Override
	protected Response exceptionToResponse(KirraException e) {
		return Response.status(getStatus(e.getKind())).type(MediaType.APPLICATION_JSON).entity(new Gson().toJson(new ErrorDTO(e.getMessage(), e.getKind()))).build();
	}

}
