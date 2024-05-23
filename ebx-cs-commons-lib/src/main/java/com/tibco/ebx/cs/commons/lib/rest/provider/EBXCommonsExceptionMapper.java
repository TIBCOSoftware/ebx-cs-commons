package com.tibco.ebx.cs.commons.lib.rest.provider;

import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Mapper for EBXCommonsException <br>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class EBXCommonsExceptionMapper implements ExceptionMapper<EBXCommonsException> {

	@Override
	public Response toResponse(final EBXCommonsException pException) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(pException).build();
	}

}
