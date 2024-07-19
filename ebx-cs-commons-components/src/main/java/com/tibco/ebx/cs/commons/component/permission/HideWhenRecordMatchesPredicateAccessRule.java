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
 * Use this rule to hide a field within a create context.
 * 
 * @author Mickaël Chevalier
 */
public class HideWhenRecordMatchesPredicateAccessRule implements AccessRule {
	private final String predicate;
	private final String sessionKey;
	private final String sessionValue;
	private final boolean match;

	public HideWhenRecordMatchesPredicateAccessRule(final String predicate) {
		this(predicate, null, null, true);
	}

	public HideWhenRecordMatchesPredicateAccessRule(final String predicate, final String sessionKey, final String sessionValue, final boolean match) {
		super();
		this.predicate = predicate;
		this.sessionKey = sessionKey;
		this.sessionValue = sessionValue;
		this.match = match;
	}

	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		boolean hideNonNull = node.getDefaultValue() != null;
		if (adaptation.isTableOccurrence() && (adaptation.get(node) == null || hideNonNull) && adaptation.matches(predicate)) {
			return AccessPermission.getHidden();
		} else if (adaptation.isSchemaInstance() && sessionKey != null) {
			Object val = session.getAttribute(sessionKey);
			if (val != null && match == val.equals(sessionValue)) {
				return AccessPermission.getHidden();
			}
		}
		return AccessPermission.getReadWrite();
	}
}
