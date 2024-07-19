/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.service;

import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.ui.selection.DatasetEntitySelection;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.declaration.ActivationContextOnDataset;
import com.orchestranetworks.userservice.declaration.UserServiceDeclaration;
import com.orchestranetworks.userservice.declaration.UserServicePropertiesDefinitionContext;
import com.orchestranetworks.userservice.declaration.WebComponentDeclarationContext;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Declaration for GenerateJavaAccessers service
 * 
 * @author Mickaël Chevalier
 * @see GenerateJavaAccessers
 */
public class GenerateJavaAccessersDeclaration implements UserServiceDeclaration.OnDataset {

	@Override
	public UserService<DatasetEntitySelection> createUserService() {
		return new GenerateJavaAccessers();
	}

	@Override
	public void declareWebComponent(final WebComponentDeclarationContext pContext) {
		// nothing to implement
	}

	@Override
	public void defineActivation(final ActivationContextOnDataset pContext) {
		pContext.includeAllDatasets();
	}

	@Override
	public void defineProperties(final UserServicePropertiesDefinitionContext pContext) {
		pContext.setLabel("Generate Java Accessers");
	}

	@Override
	public ServiceKey getServiceKey() {
		return CommonsConstants.GENERATE_JAVA_ACCESSERS_SERVICE;
	}

}
