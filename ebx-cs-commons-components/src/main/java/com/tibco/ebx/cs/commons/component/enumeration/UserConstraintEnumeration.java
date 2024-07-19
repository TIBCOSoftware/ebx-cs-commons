/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2016. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.DirectoryHandler;

/**
 * This constraint enumeration can be used to create a selection field for choosing a user
 * 
 * @author Mickaël Chevalier
 */
public class UserConstraintEnumeration implements ConstraintEnumeration<String> {
	private static final String MESSAGE = "Specify a user.";
	private boolean relaxed = true;
	private boolean simpleLabel = false;
	private String roleName;

	@Override
	public void checkOccurrence(final String aValue, final ValueContextForValidation aValidationContext) throws InvalidSchemaException {
		if (!relaxed) {
			UserReference user = Profile.forUser(aValue);
			DirectoryHandler directory = DirectoryHandler.getInstance(aValidationContext.getHome().getRepository());
			if (!directory.isUserDefined(user)) {
				aValidationContext.addError("User " + aValue + " does not exist.");
			}
			if (roleName != null && !directory.isUserInRole(user, Profile.forSpecificRole(roleName))) {
				aValidationContext.addError("User " + aValue + " is not in role " + roleName + ".");
			}
		}
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// not implemented
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return MESSAGE;
	}

	@Override
	public String displayOccurrence(final String aValue, final ValueContext aContext, final Locale aLocale) throws InvalidSchemaException {
		if (simpleLabel) {
			return aValue;
		}
		return getUserLabel(aContext.getHome().getRepository(), aLocale, aValue);
	}

	public static String getUserLabel(final Repository repo, final Locale locale, final String userId) {
		DirectoryHandler directory = DirectoryHandler.getInstance(repo);
		UserReference user = Profile.forUser(userId);
		if (directory.isUserDefined(user)) {
			return directory.displayUser(user, locale);
		}
		return user.getLabel();
	}

	@Override
	public List<String> getValues(final ValueContext aContext) throws InvalidSchemaException {
		Set<String> result = new LinkedHashSet<>();
		String curr = (String) aContext.getValue();
		if (curr != null) {
			result.add(curr);
		}
		DirectoryHandler directory = DirectoryHandler.getInstance(aContext.getHome().getRepository());
		Role role = Profile.EVERYONE;
		if (roleName != null) {
			role = Profile.forSpecificRole(roleName);
		}
		List<UserReference> users = directory.getUsersInRole(role);
		for (UserReference user : users) {
			result.add(user.getUserId());
		}
		return new ArrayList<>(result);
	}

	public boolean isRelaxed() {
		return relaxed;
	}

	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}

	public boolean isSimpleLabel() {
		return simpleLabel;
	}

	public void setSimpleLabel(final boolean simpleLabel) {
		this.simpleLabel = simpleLabel;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(final String roleName) {
		this.roleName = roleName;
	}

}
