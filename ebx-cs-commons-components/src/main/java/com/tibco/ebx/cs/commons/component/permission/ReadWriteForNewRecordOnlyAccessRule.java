package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Almost the opposite of HideOnCreationAccessRule, this rule ensures that some fields are only read-write during creation and are otherwise read-only or hidden. The 'otherwise' permission is
 * configurable using an argument to the constructor. The no-argument constructor uses read-only.
 *
 * @see HideOnCreationAccessRule
 * @author Mickaël Chevalier
 */
public class ReadWriteForNewRecordOnlyAccessRule implements AccessRule {
	/** The role giving permission despite the rule */
	private Role permissiveRole;

	protected AccessPermission notNewRecordPermission;

	public ReadWriteForNewRecordOnlyAccessRule() {
		this(AccessPermission.getReadOnly(), CommonsConstants.TECH_ADMIN);
	}

	public ReadWriteForNewRecordOnlyAccessRule(final AccessPermission notNewRecordPermission) {
		this(notNewRecordPermission, CommonsConstants.TECH_ADMIN);
	}

	public ReadWriteForNewRecordOnlyAccessRule(final Role permissiveRole) {
		this(AccessPermission.getReadOnly(), permissiveRole);
	}

	public ReadWriteForNewRecordOnlyAccessRule(final AccessPermission notNewRecordPermission, final Role permissiveRole) {
		this.notNewRecordPermission = notNewRecordPermission;
		this.permissiveRole = permissiveRole;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.orchestranetworks.service.AccessRule#getPermission(com.onwbp.adaptation .Adaptation, com.orchestranetworks.service.Session, com.orchestranetworks.schema.SchemaNode)
	 */
	@Override
	public AccessPermission getPermission(final Adaptation pAdaptation, final Session pSession, final SchemaNode pNode) {
		// If it's a new record or viewing on a table (is schema instance for both cases),
		// or if it's history, or if you're in a permissive role, then return RW
		if (pAdaptation.isSchemaInstance() || pAdaptation.isHistory() || isUserAlwaysReadWrite(pSession)) {
			return AccessPermission.getReadWrite();
		}
		return this.notNewRecordPermission;
	}

	/**
	 * Checks if a permissive role was specified, and if so if the user is in that role, but can be overridden to specify any additional logic based on the session that should determine if a user is
	 * always read/write (e.g. tracking info).
	 *
	 * @param session the user session
	 * @return whether the user's always read/write
	 */
	protected boolean isUserAlwaysReadWrite(final Session session) {
		return this.permissiveRole != null && session.isUserInRole(this.permissiveRole);
	}

	public Role getPermissiveRole() {
		return this.permissiveRole;
	}

	public void setPermissiveRole(final Role permissiveRole) {
		this.permissiveRole = permissiveRole;
	}
}
