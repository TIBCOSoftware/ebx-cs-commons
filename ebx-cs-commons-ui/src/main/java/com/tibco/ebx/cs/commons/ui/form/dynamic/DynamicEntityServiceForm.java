/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form.dynamic;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.userservice.UserServiceEventOutcome;

/**
 * Dynamic entity service entity
 * 
 * @author Mickaël Chevalier
 */
abstract class DynamicEntityServiceForm {

	protected DynamicServiceFormOperations operations;

	public void setEventOnRevert(final UserServiceEventOutcome eventOnRevert) {
		this.operations.setEventOnRevert(eventOnRevert);
	}

	public void setIgnoreFieldsWithInformationEquals(final String ignoreFieldsWithInformationEquals) {
		this.operations.setIgnoreFieldsWithInformationEquals(ignoreFieldsWithInformationEquals);
	}

	public void setJavascriptOnClose(final String javascriptOnClose) {
		this.operations.setJavascriptOnClose(javascriptOnClose);
	}

	public void setPathToCollectionInConfigurator(final Path pathToCollectionInConfigurator) {
		this.operations.setPathToCollectionInConfigurator(pathToCollectionInConfigurator);
	}

	public void setPathToTypeConfigurator(final Path pathToTypeConfigurator) {
		this.operations.setPathToTypeConfigurator(pathToTypeConfigurator);
	}

	public void setPathToTypes(final String pathToTypes) {
		this.operations.setPathToTypes(pathToTypes);
	}

}
