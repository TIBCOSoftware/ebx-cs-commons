package com.tibco.ebx.cs.commons.lib.rest.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

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
