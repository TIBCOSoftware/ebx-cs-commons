/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.ui.selection.DataspaceEntitySelection;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * @author Mickaël Chevalier
 */
public class TechAdminOnlyServicePermissionRule<S extends DataspaceEntitySelection> implements ServicePermissionRule<S> {

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<S> context) {
		// the first time you set up the repository, need to bring in roles
		if (context.getSession().isUserInRole(CommonsConstants.TECH_ADMIN)) {
			return UserServicePermission.getEnabled();
		}
		return UserServicePermission.getDisabled();
	}
}