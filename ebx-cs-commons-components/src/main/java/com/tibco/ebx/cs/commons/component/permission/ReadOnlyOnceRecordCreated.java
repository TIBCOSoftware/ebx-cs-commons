/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Session;

/**
 * @author Mickaël Chevalier
 * 
 *         Set nodes or occurrence access right to read-only after creation.
 * 
 */
public class ReadOnlyOnceRecordCreated implements AccessRule {

	/** The role giving permission despite the rule */
	private String permissiveRole;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orchestranetworks.service.AccessRule#getPermission(com.onwbp.adaptation .Adaptation, com.orchestranetworks.service.Session, com.orchestranetworks.schema.SchemaNode)
	 */
	@Override
	public AccessPermission getPermission(final Adaptation pAdaptation, final Session pSession, final SchemaNode pNode) {

		if ((this.permissiveRole != null) && pSession.isUserInRole(Profile.forSpecificRole(this.permissiveRole))) {
			return AccessPermission.getReadWrite();
		}

		if (pAdaptation.isSchemaInstance()) {
			return AccessPermission.getReadWrite();
		}
		return AccessPermission.getReadOnly();
	}

	public String getPermissiveRole() {
		return this.permissiveRole;
	}

	public void setPermissiveRole(final String permissiveRole) {
		this.permissiveRole = permissiveRole;
	}

}
