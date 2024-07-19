/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;

/**
 * Simple access rule that can be constructed with a restricted permission (default is hidden) and will return that permission when the session has an interaction (meaning it is in the context of a
 * workflow).
 * 
 * @author Mickaël Chevalier
 */
public class RestrictInWorkflowAccessRule implements AccessRule {
	private AccessPermission restrictionType;

	public RestrictInWorkflowAccessRule() {
		this(AccessPermission.getHidden());
	}

	public RestrictInWorkflowAccessRule(final AccessPermission restrictionType) {
		this.restrictionType = restrictionType;
	}

	// Always restrict in a workflow, but this allows you to override for more customized behavior
	protected boolean restrictInWorkflow(final Adaptation adaptation, final Session session) {
		return true;
	}

	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (adaptation.isHistory()) {
			return AccessPermission.getReadWrite();
		}
		if (session.getInteraction(true) != null && restrictInWorkflow(adaptation, session)) {
			return restrictionType;
		}
		return AccessPermission.getReadWrite();
	}

	public AccessPermission getRestrictionType() {
		return this.restrictionType;
	}

	public void setRestrictionType(final AccessPermission restrictionType) {
		this.restrictionType = restrictionType;
	}
}
