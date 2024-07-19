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
 * An AccessRule that will return the least restrictive permissions from the given access rules. For example, if one rule returns Hidden and a 2nd rule returns ReadOnly, this will return ReadOnly. The
 * AccessRulesManager applies the most restrictive rule, so in this way you can essentially layer a logical "or" condition.
 * 
 * @author Mickaël Chevalier
 */
public class LeastRestrictiveAccessRule implements AccessRule {
	private AccessRule[] rules;

	public LeastRestrictiveAccessRule() {
		this(new AccessRule[0]);
	}

	public LeastRestrictiveAccessRule(final AccessRule[] rules) {
		this.rules = rules;
	}

	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		AccessPermission returnValue = AccessPermission.getHidden();
		for (int i = 0; i < rules.length && !AccessPermission.getReadWrite().equals(returnValue); i++) {
			AccessRule rule = rules[i];
			AccessPermission permission = rule.getPermission(adaptation, session, node);
			if (!AccessPermission.getHidden().equals(permission)) {
				if (AccessPermission.getReadWrite().equals(permission) || (AccessPermission.getReadOnly().equals(permission) && AccessPermission.getHidden().equals(returnValue))) {
					returnValue = permission;
				}
			}
		}
		return returnValue;
	}

	public AccessRule[] getRules() {
		return this.rules;
	}

	public void setRules(final AccessRule[] rules) {
		this.rules = rules;
	}
}
