package com.abstratt.kirra.rest.resources;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.abstratt.kirra.rest.common.ErrorDTO;
import com.google.gson.Gson;

@Provider
public class KirraRestExceptionMapper extends AbstractExceptionMapper<KirraRestException> {
	@Override
	protected Response exceptionToResponse(KirraRestException e) {
		return Response.status(e.getStatus()).type(MediaType.APPLICATION_JSON).entity(new Gson().toJson(new ErrorDTO(e.getMessage(), e.getKind()))).build();
	}

}
