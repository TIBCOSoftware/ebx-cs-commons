/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.selection.DataspaceEntitySelection;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * If you want a service to show up only in the context of a workflow...
 * 
 * @author Mickaël Chevalier
 */
public class WorkflowOnlyServicePermissionRule<S extends DataspaceEntitySelection> implements ServicePermissionRule<S> {
	private final boolean allowForTechAdmin;

	public WorkflowOnlyServicePermissionRule() {
		this(true);
	}

	public WorkflowOnlyServicePermissionRule(final boolean allowForTechAdmin) {
		this.allowForTechAdmin = allowForTechAdmin;
	}

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<S> context) {
		Session session = context.getSession();
		// the first time you set up the repository, need to bring in roles
		if ((allowForTechAdmin && context.getSession().isUserInRole(CommonsConstants.TECH_ADMIN)) || session.getInteraction(true) != null) {
			return UserServicePermission.getEnabled();
		}
		return UserServicePermission.getDisabled();
	}
}