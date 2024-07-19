/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.lang.reflect.Method;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.ui.selection.RecordEntitySelection;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceExtended;
import com.orchestranetworks.userservice.UserServiceForCreate;
import com.orchestranetworks.userservice.UserServiceGetCreatedRecordContext;
import com.orchestranetworks.userservice.UserServiceInitializeContext;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;

/**
 * Dynamic record service form
 * 
 * @author Mickaël Chevalier
 */
public class DynamicRecordServiceForm extends DynamicEntityServiceForm implements UserServiceExtended<RecordEntitySelection>, UserServiceForCreate<RecordEntitySelection> {

	/**
	 * Constructor
	 * 
	 * @param pTable       table
	 * @param pRecord      record
	 * @param pIsDuplicate duplicate
	 */
	public DynamicRecordServiceForm(final AdaptationTable pTable, final Adaptation pRecord, final Boolean pIsDuplicate) {
		this.operations = new DynamicServiceFormOperations(pTable, pRecord, pIsDuplicate);
	}

	public void setDuplicate(final boolean duplicate) {
		this.operations.setDuplicate(duplicate);
	}

	public void setEventAfterDuplicate(final UserServiceEventOutcome eventAfterDuplicate) {
		this.operations.setEventAfterDuplicate(eventAfterDuplicate);
	}

	public void setToolbarName(final String pName) {
		this.operations.setToolbarName(pName);

	}

	public void setToolbarBuilderMethod(final Method pMethod) {
		this.operations.setToolbarBuilderMethod(pMethod);

	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<RecordEntitySelection> pContext, final UserServiceEventOutcome pEventOutcome) {
		return this.operations.processEventOutcome(pContext, pEventOutcome);
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<RecordEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		this.operations.setupDisplay(pContext, pConfigurator, pContext.getSession());
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<RecordEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		this.operations.setupObjectContext(pContext, pBuilder);
	}

	@Override
	public void validate(final UserServiceValidateContext<RecordEntitySelection> pContext) {
		this.operations.validate(pContext, pContext.getSession());
	}

	@Override
	public UserServiceEventOutcome initialize(final UserServiceInitializeContext<RecordEntitySelection> pContext) {
		return null;
	}

	@Override
	public Adaptation getCreatedRecord(UserServiceGetCreatedRecordContext<RecordEntitySelection> aContext) {
		return operations.getCreatedRecord(aContext);
	}

}
