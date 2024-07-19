/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ActionPermission;
import com.orchestranetworks.service.ServicePermission;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/***
 * @author Mickaël Chevalier
 */
public class TechAdminOnlyServicePermission implements ServicePermission {

	@Override
	public ActionPermission getPermission(final SchemaNode schemaNode, final Adaptation adaptation, final Session session) {

		if (session.isUserInRole(CommonsConstants.TECH_ADMIN)) {
			return ActionPermission.getEnabled();
		} else {
			return ActionPermission.getHidden();
		}

	}
}