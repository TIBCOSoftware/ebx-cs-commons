/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.rest.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.orchestranetworks.service.OperationException;

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
