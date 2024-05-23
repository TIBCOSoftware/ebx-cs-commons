package com.tibco.ebx.cs.commons.lib.rest.provider;

import com.orchestranetworks.service.OperationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Mapper for OperationException <br>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class OperationExceptionMapper implements ExceptionMapper<OperationException> {

	@Override
	public Response toResponse(final OperationException pException) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(pException).build();
	}

}
