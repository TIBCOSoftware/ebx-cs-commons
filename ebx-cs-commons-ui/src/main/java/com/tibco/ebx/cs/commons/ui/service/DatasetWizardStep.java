/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.service;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.ui.selection.DatasetEntitySelection;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;

/**
 *
 * Interface to implement wizards as user services as described in EBX documentation.
 * 
 * @author Mickaël Chevalier
 */
public interface DatasetWizardStep {
	/**
	 * Setup display
	 * 
	 * @param pContext      UserServiceSetupDisplayContext
	 * @param pConfigurator UserServiceDisplayConfigurator
	 * @throws OperationException OperationException
	 */
	public void setupDisplay(UserServiceSetupDisplayContext<DatasetEntitySelection> pContext, UserServiceDisplayConfigurator pConfigurator) throws OperationException;
}
