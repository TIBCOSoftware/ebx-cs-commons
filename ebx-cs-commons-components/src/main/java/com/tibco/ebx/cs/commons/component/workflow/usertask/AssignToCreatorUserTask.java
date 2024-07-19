/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.workflow.usertask;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.workflow.CreationWorkItemSpec;
import com.orchestranetworks.workflow.UserTask;
import com.orchestranetworks.workflow.UserTaskCreationContext;

/**
 * Extends UserTask to allocate the user task to the creator of the workflow.<br>
 * <br>
 * Only the {@link #handleCreate(UserTaskCreationContext) handleCreate()} method is used.<br>
 * <br>
 * This class can be used as is by directly setting the qualified name in the Workflow Task configuration:<br>
 * {@code com.orchestranetworks.presales.toolbox.workflow.usertask.AssignToCreatorUserTask}<br>
 * <br>
 * Or, this class can be extended again to add custom behavior. If overriding the {@link #handleCreate(UserTaskCreationContext) handleCreate()} method use {@code super.handleCreate(pContext)} at first
 * to ensure user allocation.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class AssignToCreatorUserTask extends UserTask {
	/*
	 * @see com.orchestranetworks.workflow.UserTask#handleCreate(com.orchestranetworks.workflow. UserTaskCreationContext)
	 */
	@Override
	public void handleCreate(final UserTaskCreationContext pContext) throws OperationException {
		UserReference user = pContext.getProcessInstance().getCreator();
		CreationWorkItemSpec spec = CreationWorkItemSpec.forAllocation(user);
		pContext.createWorkItem(spec);
	}
}
