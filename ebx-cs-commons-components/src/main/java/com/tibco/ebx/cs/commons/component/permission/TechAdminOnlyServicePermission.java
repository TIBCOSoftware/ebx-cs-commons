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