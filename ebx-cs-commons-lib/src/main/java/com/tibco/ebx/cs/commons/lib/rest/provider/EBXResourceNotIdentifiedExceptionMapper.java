/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.rest.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;

/**
 * Mapper for EBXResourceNotIdentifiedException <br>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class EBXResourceNotIdentifiedExceptionMapper implements ExceptionMapper<EBXResourceNotIdentifiedException> {

	@Override
	public Response toResponse(final EBXResourceNotIdentifiedException pException) {
		return Response.status(Response.Status.BAD_REQUEST).entity(pException).build();
	}

}
