package com.tibco.ebx.cs.commons.lib.utils;

import com.orchestranetworks.rest.ApplicationConfigurator;
import com.tibco.ebx.cs.commons.lib.rest.provider.EBXCommonsExceptionMapper;
import com.tibco.ebx.cs.commons.lib.rest.provider.EBXResourceNotFoundExceptionMapper;
import com.tibco.ebx.cs.commons.lib.rest.provider.EBXResourceNotIdentifiedExceptionMapper;
import com.tibco.ebx.cs.commons.lib.rest.provider.OperationExceptionMapper;

/**
 * Rest Utilities <br>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public final class RestUtils {

	private RestUtils() {
		super();
	}

	/**
	 * Register exception mappers
	 * 
	 * @param pConfigurator ApplicationConfigurator
	 */
	public static void registerCommonsExceptionMappers(final ApplicationConfigurator pConfigurator) {
		pConfigurator.register(EBXCommonsExceptionMapper.class);
		pConfigurator.register(EBXResourceNotFoundExceptionMapper.class);
		pConfigurator.register(EBXResourceNotIdentifiedExceptionMapper.class);
		pConfigurator.register(OperationExceptionMapper.class);
	}

}
