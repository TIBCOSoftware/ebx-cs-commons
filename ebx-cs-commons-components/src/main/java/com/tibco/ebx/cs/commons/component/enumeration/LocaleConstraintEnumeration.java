/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * 
 * @author Mickaël Chevalier
 *
 */
public class LocaleConstraintEnumeration implements ConstraintEnumeration<Locale> {

	@Override
	public void checkOccurrence(final Locale aValue, final ValueContextForValidation aValidationContext) throws InvalidSchemaException {
		// no implementation
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// no implementation
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}

	@Override
	public String displayOccurrence(final Locale aValue, final ValueContext aContext, final Locale aLocale) throws InvalidSchemaException {
		if (aValue == null) {
			return "[null]";
		}

		return aValue.getDisplayName(aLocale);
	}

	@Override
	public List<Locale> getValues(final ValueContext aContext) throws InvalidSchemaException {
		return aContext.getHome().getRepository().getLocales();
	}

}
