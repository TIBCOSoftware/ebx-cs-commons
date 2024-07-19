/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ActionPermission;
import com.orchestranetworks.service.ServicePermission;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.annotation.Reviewed;

/**
 * Show the service only while in workflow.
 *
 * @author Lionel Luquet
 */
@Reviewed("MCH")
public class ShowServiceOnlyInWorkflow implements ServicePermission {
	@Override
	public ActionPermission getPermission(final SchemaNode pNode, final Adaptation pAdaptation, final Session pSession) {
		SessionInteraction sessionInteraction = pSession.getInteraction(true);

		if (sessionInteraction != null) {
			return ActionPermission.getEnabled();
		} else {
			return ActionPermission.getHidden();
		}
	}
}
