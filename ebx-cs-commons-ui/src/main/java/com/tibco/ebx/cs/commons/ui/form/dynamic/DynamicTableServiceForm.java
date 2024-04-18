package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.lang.reflect.Method;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;

/**
 * Dynamic Table Service Form
 * 
 * @author Mickaël Chevalier
 */
public class DynamicTableServiceForm extends DynamicEntityServiceForm implements UserService<TableViewEntitySelection> {

	/**
	 * Constructor
	 * 
	 * @param pTable       table
	 * @param pRecord      record
	 * @param pIsDuplicate is duplicate ?
	 */
	public DynamicTableServiceForm(final AdaptationTable pTable, final Adaptation pRecord, final Boolean pIsDuplicate) {
		this.operations = new DynamicServiceFormOperations(pTable, pRecord, pIsDuplicate);
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<TableViewEntitySelection> pContext, final UserServiceEventOutcome pEventOutcome) {
		return this.operations.processEventOutcome(pContext, pEventOutcome);
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<TableViewEntitySelection> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		this.operations.setupDisplay(pContext, pConfigurator, pContext.getSession());
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<TableViewEntitySelection> pContext, final UserServiceObjectContextBuilder pBuilder) {
		this.operations.setupObjectContext(pContext, pBuilder);
	}

	@Override
	public void validate(final UserServiceValidateContext<TableViewEntitySelection> pContext) {
		this.operations.validate(pContext, pContext.getSession());
	}

	public void setToolbarName(final String pName) {
		this.operations.setToolbarName(pName);

	}

	public void setToolbarBuilderMethod(final Method pMethod) {
		this.operations.setToolbarBuilderMethod(pMethod);

	}
}
