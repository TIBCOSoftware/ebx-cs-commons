/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * This rule ensures that some fields are only read-write unitl assigned and are otherwise read-only. Note: the tech-admin role will always have read-write access.
 * 
 * @author Mickaël Chevalier
 */
public class ReadWriteUntilValueAssignedAccessRule implements AccessRule {

	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		// Will be data set when called for column permissions
		// or a record not yet saved
		if (adaptation.isSchemaInstance() || adaptation.isHistory()) {
			return AccessPermission.getReadWrite();
		}
		if (isUserAlwaysReadWrite(session)) {
			return AccessPermission.getReadWrite();
		} else if (adaptation.get(node.getPathInAdaptation()) == null) {
			return AccessPermission.getReadWrite();
		} else {
			return AccessPermission.getReadOnly();
		}
	}

	protected boolean isUserAlwaysReadWrite(final Session session) {
		return session.isUserInRole(CommonsConstants.TECH_ADMIN);
	}
}
