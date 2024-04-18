/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.SessionPermissions;
import com.orchestranetworks.service.UserReference;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * An access rule that applies the node permissions of another user (the "permissions user"). It can be thought of as restricting the actual user to the template specified in the data set permissions
 * of this permissions user.
 * 
 * @author Mickaël Chevalier
 */
public abstract class PermissionsUserNodeAccessRule implements AccessRule {
	private String permissionsUserId;
	private PermissionsUserManager permissionsUserManager;

	protected PermissionsUserNodeAccessRule(final String permissionsUserId) {
		this(permissionsUserId, DefaultPermissionsUserManager.getInstance());
	}

	protected PermissionsUserNodeAccessRule(final String permissionsUserId, final PermissionsUserManager permissionsUserManager) {
		this.permissionsUserId = permissionsUserId;
		this.permissionsUserManager = permissionsUserManager;
	}

	protected abstract boolean isPermissionsUserApplied(Adaptation adaptation, Session session, SchemaNode node);

	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (adaptation.isHistory() || isUserAlwaysReadWrite(session) || !isPermissionsUserApplied(adaptation, session, node)) {
			return AccessPermission.getReadWrite();
		}

		UserReference permissionsUser = Profile.forUser(permissionsUserId);
		Repository repo = adaptation.getHome().getRepository();
		SessionPermissions permissions;
		if (permissionsUserManager == null) {
			permissions = repo.createSessionPermissionsForUser(permissionsUser);
		} else {
			permissions = permissionsUserManager.getSessionPermissions(repo, permissionsUser);
		}
		return permissions.getNodeAccessPermission(node, adaptation);
	}

	protected boolean isUserAlwaysReadWrite(final Session session) {
		return session.isUserInRole(CommonsConstants.TECH_ADMIN);
	}
}
