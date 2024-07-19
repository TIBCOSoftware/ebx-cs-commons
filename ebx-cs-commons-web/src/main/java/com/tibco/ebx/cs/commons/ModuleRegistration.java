/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons;

import com.orchestranetworks.module.ModuleContextOnRepositoryStartup;
import com.orchestranetworks.module.ModuleRegistrationServlet;
import com.orchestranetworks.module.ModuleServiceRegistrationContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;
import com.tibco.ebx.cs.commons.ui.service.GenerateDataModelDocumentationDeclaration;
import com.tibco.ebx.cs.commons.ui.service.GenerateJavaAccessers;
import com.tibco.ebx.cs.commons.ui.service.GenerateJavaAccessersDeclaration;

/**
 * Module registration
 * 
 * @author Mickaël Chevalier
 */
public class ModuleRegistration extends ModuleRegistrationServlet {

	private static final long serialVersionUID = 1818684201843882548L;

	@Override
	public void handleRepositoryStartup(final ModuleContextOnRepositoryStartup pContext) throws OperationException {
		CommonsLogger.setLogger(pContext.getLoggingCategory());
		Session systemUser = pContext.createSystemUserSession(null);
		try {
			GenerateJavaAccessers.initRepository(pContext.getRepository(), systemUser, pContext.getModuleName());
		} catch (OperationException ex) {
			CommonsLogger.getLogger().error("Commons module initializationfinished with error(s).", ex);
			throw new RuntimeException(ex);
		}
		CommonsLogger.getLogger().info("Commons module initialization and upgrade complete");
	}

	@Override
	public void handleServiceRegistration(final ModuleServiceRegistrationContext pContext) {
		pContext.registerUserService(new GenerateJavaAccessersDeclaration());
		pContext.registerUserService(new GenerateDataModelDocumentationDeclaration());
	}
}