package com.tibco.ebx.cs.commons.ui.service;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.ui.selection.DataspaceEntitySelection;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;

/**
 *
 * Interface to implement wizards as user services as described in EBX documentation.
 * 
 * @author Mickaël Chevalier
 */
public interface DataspaceWizardStep {
	/**
	 * Setup display
	 * 
	 * @param pContext      UserServiceSetupDisplayContext
	 * @param pConfigurator UserServiceDisplayConfigurator
	 * @throws OperationException OperationException
	 */
	public void setupDisplay(UserServiceSetupDisplayContext<DataspaceEntitySelection> pContext, UserServiceDisplayConfigurator pConfigurator) throws OperationException;
}
