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
import com.orchestranetworks.service.directory.DirectoryHandler;
import com.orchestranetworks.service.directory.ProfileListContextBridge;

/**
 * This constraint enumeration can be used to create a selection field for choosing a role
 * 
 * @author Mickaël Chevalier
 *
 */
public class RoleConstraintEnumeration implements ConstraintEnumeration<String> {
	private static final String MESSAGE = "Specify a role.";
	private boolean relaxed = true;

	@Override
	public void checkOccurrence(final String aValue, final ValueContextForValidation aValidationContext) throws InvalidSchemaException {
		if (!this.relaxed) {
			Role role = Profile.forSpecificRole(aValue);
			DirectoryHandler directory = DirectoryHandler.getInstance(aValidationContext.getHome().getRepository());
			if (!directory.isSpecificRoleDefined(role)) {
				aValidationContext.addError("Role " + aValue + " does not exist.");
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
		return getRoleLabel(aContext.getHome().getRepository(), aLocale, aValue);
	}

	public static String getRoleLabel(final Repository repo, final Locale locale, final String roleName) {
		DirectoryHandler directory = DirectoryHandler.getInstance(repo);
		Role role = Profile.forSpecificRole(roleName);
		if (directory.isSpecificRoleDefined(role)) {
			return directory.displaySpecificRole(role, locale);
		}
		return role.getLabel();
	}

	@Override
	public List<String> getValues(final ValueContext aContext) throws InvalidSchemaException {
		Set<String> result = new LinkedHashSet<>();
		String curr = (String) aContext.getValue();
		if (curr != null) {
			result.add(curr);
		}
		DirectoryHandler directory = DirectoryHandler.getInstance(aContext.getHome().getRepository());

		List<Profile> profiles = directory.getProfiles(ProfileListContextBridge.getForWorkflow());
		for (Profile profile : profiles) {
			if (profile.isSpecificRole()) {
				result.add(((Role) profile).getRoleName());
			}
		}
		return new ArrayList<>(result);
	}

	public boolean isRelaxed() {
		return this.relaxed;
	}

	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}
}
