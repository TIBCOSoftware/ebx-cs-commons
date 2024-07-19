/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
Z * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.service.Session;

/**
 * Use this rule to hide a field within a create user task.
 *
 * Note that this doesn't restrict during record creation in general, just during a create user task.
 * 
 * @author Mickaël Chevalier
 */
public class HideOnCreationAccessRule implements AccessRule {
	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (adaptation.isHistory()) {
			return AccessPermission.getReadWrite();
		}
		SessionInteraction sessionInter = session.getInteraction(true);
		if (sessionInter != null && ServiceKey.CREATE.equals(sessionInter.getServiceKey())) {
			return AccessPermission.getHidden();
		}
		return AccessPermission.getReadWrite();
	}
}
