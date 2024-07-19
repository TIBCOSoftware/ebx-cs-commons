/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.selection.DataspaceEntitySelection;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;

/***
 * @author Mickaël Chevalier
 */
public class MasterOrChildDataSpaceOnlyServicePermissionRule<S extends DataspaceEntitySelection> implements ServicePermissionRule<S> {
	protected boolean allowInMaster = true;
	protected boolean allowInChild = true;
	protected boolean allowInsideWorkflow = true;
	protected boolean allowOutsideWorkflow = true;

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<S> aContext) {
		AdaptationHome dataSpace = aContext.getEntitySelection().getDataspace();
		Session session = aContext.getSession();
		boolean canUserLaunch;
		String msgParam;
		if (isMasterDataSpace(dataSpace)) {
			canUserLaunch = canUserLaunchInMasterDataSpace(dataSpace, session);
			msgParam = "master";
		} else {
			canUserLaunch = canUserLaunchInChildDataSpace(dataSpace, session);
			msgParam = "child";
		}
		if (canUserLaunch) {
			SessionInteraction interaction = session.getInteraction(true);
			if ((interaction != null && canUserLaunchInsideWorkflow(dataSpace, session)) || (interaction == null && canUserLaunchOutsideWorkflow(dataSpace, session))) {
				return UserServicePermission.getEnabled();
			}
			return UserServicePermission.getDisabled(UserMessage.createError("This service can't be invoked from " + (interaction == null ? "inside" : "outside") + " of a workflow."));
		}
		return UserServicePermission.getDisabled(UserMessage.createError("Not allowed to launch service from a " + msgParam + " data space."));
	}

	protected boolean isMasterDataSpace(final AdaptationHome dataSpace) {
		if (dataSpace.isBranchReference()) {
			return true;
		}
		AdaptationHome parentBranch = dataSpace.getParentBranch();
		return parentBranch != null && parentBranch.isBranchReference();
	}

	protected boolean canUserLaunchInMasterDataSpace(final AdaptationHome dataSpace, final Session session) {
		return allowInMaster;
	}

	protected boolean canUserLaunchInChildDataSpace(final AdaptationHome dataSpace, final Session session) {
		return allowInChild;
	}

	protected boolean canUserLaunchInsideWorkflow(final AdaptationHome dataSpace, final Session session) {
		return allowInsideWorkflow;
	}

	protected boolean canUserLaunchOutsideWorkflow(final AdaptationHome dataSpace, final Session session) {
		return allowOutsideWorkflow;
	}

	public boolean isAllowInMaster() {
		return this.allowInMaster;
	}

	public void setAllowInMaster(final boolean allowInMaster) {
		this.allowInMaster = allowInMaster;
	}

	public boolean isAllowInChild() {
		return this.allowInChild;
	}

	public void setAllowInChild(final boolean allowInChild) {
		this.allowInChild = allowInChild;
	}

	public boolean isAllowInsideWorkflow() {
		return this.allowInsideWorkflow;
	}

	public void setAllowInsideWorkflow(final boolean allowInsideWorkflow) {
		this.allowInsideWorkflow = allowInsideWorkflow;
	}

	public boolean isAllowOutsideWorkflow() {
		return this.allowOutsideWorkflow;
	}

	public void setAllowOutsideWorkflow(final boolean allowOutsideWorkflow) {
		this.allowOutsideWorkflow = allowOutsideWorkflow;
	}

}