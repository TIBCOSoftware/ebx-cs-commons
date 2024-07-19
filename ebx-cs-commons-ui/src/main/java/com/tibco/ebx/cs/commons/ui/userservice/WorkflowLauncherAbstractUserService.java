/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ui.selection.EntitySelection;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceExtended;
import com.orchestranetworks.userservice.UserServiceInitializeContext;
import com.orchestranetworks.userservice.UserServiceNext;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceProcessEventOutcomeContext;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceValidateContext;

/**
 * Abstract class for the workflow launcher user services. Basically implements {@link UserServiceExtended} with everything blank. {@link UserServiceExtended#initialize(UserServiceInitializeContext)}
 * is kept abstract so you have to override it and implement the code to launch the workflow then return the {@link UserServiceEventOutcome event outcome}, for instance
 * {@link UserServiceNext#nextClose()} or {@link UserServiceNext#nextWorkItem(com.orchestranetworks.workflow.WorkItemKey)}.<br>
 * <br>
 *
 * @param <S> selection type extends {@link EntitySelection}
 * @author Aurélien Ticot
 * @since 1.7.0
 */
public abstract class WorkflowLauncherAbstractUserService<S extends EntitySelection> implements UserServiceExtended<S> {
	private final String workflowPublicationName;
	private final UserMessage workflowLabel;

	/**
	 * Create a new instance of the class. The
	 *
	 * @param pWorkflowPublicationName the workflow published name
	 * @param pWorkflowLabel           the workflow label, optional
	 * @since 1.7.0
	 */
	public WorkflowLauncherAbstractUserService(final String pWorkflowPublicationName, final UserMessage pWorkflowLabel) {
		this.workflowPublicationName = pWorkflowPublicationName;
		this.workflowLabel = pWorkflowLabel;
	}

	/**
	 * Getter for the workflow label.
	 *
	 * @return the workflow label
	 * @since 1.7.0
	 */
	public UserMessage getWorkflowLabel() {
		return this.workflowLabel;
	}

	/**
	 * Getter for the workflow publication name.
	 *
	 * @return the workflow publication name
	 * @since 1.7.0
	 */
	public String getWorkflowPublicationName() {
		return this.workflowPublicationName;
	}

	@Override
	public UserServiceEventOutcome processEventOutcome(final UserServiceProcessEventOutcomeContext<S> pContext, final UserServiceEventOutcome pEventOutcome) {
		return pEventOutcome;
	}

	@Override
	public void setupDisplay(final UserServiceSetupDisplayContext<S> pContext, final UserServiceDisplayConfigurator pConfigurator) {
	}

	@Override
	public void setupObjectContext(final UserServiceSetupObjectContext<S> pContext, final UserServiceObjectContextBuilder pBuilder) {
	}

	@Override
	public void validate(final UserServiceValidateContext<S> pContext) {
	}
}
