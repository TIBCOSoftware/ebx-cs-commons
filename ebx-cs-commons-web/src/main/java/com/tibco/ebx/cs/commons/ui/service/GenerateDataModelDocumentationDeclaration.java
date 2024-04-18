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
 * GenerateDataModelDocumentation User Service Declaration
 * 
 * @author Mickaël Chevalier
 */
public class GenerateDataModelDocumentationDeclaration implements UserServiceDeclaration.OnDataset {

	@Override
	public UserService<DatasetEntitySelection> createUserService() {
		return new GenerateDataModelDocumentation();
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
		pContext.setLabel("Generate Data Model Documentation");
	}

	@Override
	public ServiceKey getServiceKey() {
		return CommonsConstants.GENERATE_DATA_MODEL_DOCUMENTATION_SERVICE;
	}

}
