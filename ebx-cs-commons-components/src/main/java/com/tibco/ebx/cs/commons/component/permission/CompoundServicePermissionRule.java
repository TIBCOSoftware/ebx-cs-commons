package com.tibco.ebx.cs.commons.component.permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.ui.selection.DataspaceEntitySelection;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;

/**
 * @author Mickaël Chevalier
 */
public class CompoundServicePermissionRule<S extends DataspaceEntitySelection> implements ServicePermissionRule<S> {
	private List<ServicePermissionRule<S>> componentRules = new ArrayList<>();

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<S> arg0) {
		UserServicePermission result = UserServicePermission.getEnabled();
		for (ServicePermissionRule<S> servicePermissionRule : componentRules) {
			UserServicePermission subResult = servicePermissionRule.getPermission(arg0);
			result = combine(result, subResult);
		}
		return result;
	}

	private static UserServicePermission combine(final UserServicePermission result, final UserServicePermission newSub) {
		if (newSub.isDisabled()) {
			if (result.isDisabled()) {
				return UserServicePermission.getDisabled(appendMessage(result.getDisabledReason(), newSub.getDisabledReason()));
			} else {
				return newSub;
			}
		}
		return result;
	}

	private static UserMessage appendMessage(final UserMessage left, final UserMessage right) {
		if (left == null) {
			return right;
		}
		if (right == null) {
			return left;
		}
		StringBuilder msg = new StringBuilder();
		msg.append(left.formatMessage(Locale.getDefault())).append(" and ").append(right.formatMessage(Locale.getDefault()));
		return UserMessage.createError(msg.toString());
	}

	public CompoundServicePermissionRule<S> appendRule(final ServicePermissionRule<S> rule) {
		componentRules.add(rule);
		return this;
	}
}
