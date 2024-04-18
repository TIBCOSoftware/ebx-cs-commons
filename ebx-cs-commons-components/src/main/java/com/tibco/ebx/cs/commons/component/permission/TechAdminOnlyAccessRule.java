package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Access rule that allows only tech-admins to see a certain field.
 * 
 * @author Mickaël Chevalier
 */
public class TechAdminOnlyAccessRule implements AccessRule {
	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (session.isUserInRole(CommonsConstants.TECH_ADMIN)) {
			return AccessPermission.getReadWrite();
		} else {
			return AccessPermission.getHidden();
		}
	}
}
