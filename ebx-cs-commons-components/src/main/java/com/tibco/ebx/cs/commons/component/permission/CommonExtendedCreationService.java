package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.userservice.declaration.UserServiceExtensionDeclaration;
import com.orchestranetworks.userservice.declaration.UserServicePropertiesDefinitionContext;
import com.orchestranetworks.userservice.declaration.WebComponentDeclarationContext;

/**
 * @author Mickaël Chevalier
 */
public class CommonExtendedCreationService implements UserServiceExtensionDeclaration {

	public static final String USER_PARAMETER = "user";
	private final String moduleName;

	public CommonExtendedCreationService(final String moduleName) {
		this.moduleName = moduleName;
	}

	@Override
	public void defineProperties(final UserServicePropertiesDefinitionContext pContext) {
		pContext.setLabel("Extension of Creation of a new record");
	}

	@Override
	public void extendWebComponent(final WebComponentDeclarationContext pContext) {
		pContext.addOutputParameter(USER_PARAMETER, UserMessage.createInfo("User"), UserMessage.createInfo("User who performed the task."));
	}

	@Override
	public ServiceKey getExtendedServiceKey() {
		return ServiceKey.CREATE;
	}

	@Override
	public ServiceKey getServiceKey() {
		return ServiceKey.forModuleServiceName(this.moduleName, "CommonsExtendedCreation");
	}

}
