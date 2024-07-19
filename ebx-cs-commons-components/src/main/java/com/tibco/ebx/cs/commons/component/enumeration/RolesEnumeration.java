/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.directory.DirectoryHandler;
import com.orchestranetworks.service.directory.ProfileListContextBridge;

/**
 *
 * Enumerates roles
 * 
 * @author Mickaël Chevalier
 *
 */
public class RolesEnumeration implements ConstraintEnumeration<String> {

	private boolean includeAdmin;
	private boolean includeEveryone;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		Profile role = Profile.parse(pValue);
		DirectoryHandler directory = DirectoryHandler.getInstance(pContext.getHome().getRepository());
		if (!directory.isProfileDefined(role)) {
			pContext.addError("Role " + pValue + " does not exist.");
		}
	}

	public boolean isIncludeAdmin() {
		return this.includeAdmin;
	}

	public void setIncludeAdmin(final boolean includeAdmin) {
		this.includeAdmin = includeAdmin;
	}

	public boolean isIncludeEveryone() {
		return this.includeEveryone;
	}

	public void setIncludeEveryone(final boolean includeEveryone) {
		this.includeEveryone = includeEveryone;
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// not implemented
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return "";
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {

		if (pValue.startsWith("B") || pValue.startsWith("R")) {
			Profile role = Profile.parse(pValue);
			return role.getLabel();
		} else {
			return pValue;
		}
	}

	@Override
	public List<String> getValues(final ValueContext aContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		DirectoryHandler directory = DirectoryHandler.getInstance(aContext.getHome().getRepository());
		for (Profile profile : directory.getProfiles(ProfileListContextBridge.getForWorkflow())) {
			if (profile.isBuiltInEveryone() && this.includeEveryone) {
				values.add(profile.format());
			}
			if (profile.isBuiltInAdministrator() && this.includeAdmin) {
				values.add(profile.format());
			}
			if (profile.isSpecificRole()) {
				values.add(profile.format());
			}
		}
		return values;
	}

}
