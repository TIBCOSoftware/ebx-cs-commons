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
import com.tibco.ebx.cs.commons.lib.utils.WorkflowUtils;

//TODO write an article on this
/**
 * @author Mickaël Chevalier
 *
 *         Apply the right of a user to any node or occurrence accessed in a workflow.
 *
 *         The login of the user must be set as tracking information in the user task definition.
 *
 *         Many users can be set separated by a ';'
 */
public class WorkflowAccessRule implements AccessRule {

	/** The role giving permission despite the rule */
	private String permissiveRole;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.orchestranetworks.service.AccessRule#getPermission(com.onwbp.adaptation .Adaptation, com.orchestranetworks.service.Session, com.orchestranetworks.schema.SchemaNode)
	 */
	@Override
	public final AccessPermission getPermission(final Adaptation pAdaptation, final Session pSession, final SchemaNode pNode) {
		if (this.permissiveRole != null && pSession.isUserInRole(Profile.forSpecificRole(this.permissiveRole))) {
			return AccessPermission.getReadWrite();
		}

		if (!WorkflowUtils.isInWorkflow(pSession)) {
			return AccessPermission.getReadWrite();
		}

		final String trackingInfo = pSession.getTrackingInfo();
		if (trackingInfo == null) {
			return AccessPermission.getReadWrite();
		}

		Repository repository = pAdaptation.getHome().getRepository();
		SessionPermissions permissions = null;
		AccessPermission userAccessPermission = null;
		AccessPermission accessPermission = AccessPermission.getReadWrite();

		final String[] users = trackingInfo.split(";");
		for (String user : users) {
			UserReference userReference = Profile.forUser(user);
			permissions = repository.createSessionPermissionsForUser(userReference);
			if (permissions == null) {
				continue;
			}
			userAccessPermission = permissions.getNodeAccessPermission(pNode, pAdaptation);
			accessPermission = accessPermission.min(userAccessPermission);
		}

		return accessPermission;
	}

	public String getPermissiveRole() {
		return this.permissiveRole;
	}

	public void setPermissiveRole(final String permissiveRole) {
		this.permissiveRole = permissiveRole;
	}
}
