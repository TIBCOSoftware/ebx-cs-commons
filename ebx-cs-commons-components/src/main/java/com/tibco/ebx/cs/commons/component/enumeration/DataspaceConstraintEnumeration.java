/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Constraint enumeration to list dataspaces. You can also include a value "This" to represent the current dataspace and also restrict to "This".
 *
 * @see #setIncludeThis(boolean)
 * @see #setRestrictToThis(boolean)
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class DataspaceConstraintEnumeration implements ConstraintEnumeration<String> {
	/**
	 * String value of "This".
	 *
	 * @since 1.8.0
	 */
	private boolean restrictToThis = false;
	private boolean includeThis = false;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		if (pValue.equals(CommonsConstants.THIS)) {
			return Messages.get(this.getClass(), pLocale, "dataspace.This");
		} else {
			AdaptationHome dataspace = pContext.getHome().getRepository().lookupHome(HomeKey.forBranchName(pValue));
			return dataspace.getLabelOrName(pLocale);
		}
	}

	private List<AdaptationHome> getDataspaces(final AdaptationHome pDataspace) {
		ArrayList<AdaptationHome> dataspaces = new ArrayList<>();

		if (pDataspace == null) {
			return dataspaces;
		}

		List<AdaptationHome> children = null;

		if (pDataspace.isBranch()) {
			children = pDataspace.getVersionChildren();
		} else {
			children = pDataspace.getBranchChildren();
		}

		for (AdaptationHome child : children) {
			if (child.isTechnicalBranch() || child.isTechnicalVersion()) {
				continue;
			}

			if (child.isBranch() && child.isOpen()) {
				dataspaces.add(child);
			}
			dataspaces.addAll(this.getDataspaces(child));
		}
		return dataspaces;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		if (this.isIncludeThis()) {
			values.add(CommonsConstants.THIS);
		}
		if (this.isRestrictToThis()) {
			return values;
		}

		Repository repository = pContext.getAdaptationInstance().getHome().getRepository();
		AdaptationHome referenceDataspace = repository.getReferenceBranch();
		ArrayList<AdaptationHome> dataspaces = new ArrayList<>();
		dataspaces.add(referenceDataspace);
		dataspaces.addAll(this.getDataspaces(referenceDataspace));
		for (AdaptationHome dataspace : dataspaces) {
			String dataspaceName = dataspace.getKey().getName();
			values.add(dataspaceName);
		}
		return values;
	}

	/**
	 * Getter for the includeThis parameter.
	 *
	 * @return true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public boolean isIncludeThis() {
		return this.includeThis;
	}

	/**
	 * Getter for the restrictToThis parameter.
	 *
	 * @return true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public boolean isRestrictToThis() {
		return this.restrictToThis;
	}

	/**
	 * Setter for the includeThis parameter. Default is false.
	 *
	 * @param pIncludeThis true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public void setIncludeThis(final boolean pIncludeThis) {
		this.includeThis = pIncludeThis;
	}

	/**
	 * Setter for the restrictToThis parameter. Default is false.
	 *
	 * @param pRestrictToThis true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public void setRestrictToThis(final boolean pRestrictToThis) {
		this.restrictToThis = pRestrictToThis;
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		// nothing to setup
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "DataspaceConstraintEnumeration.userDocumentation");
	}
}
