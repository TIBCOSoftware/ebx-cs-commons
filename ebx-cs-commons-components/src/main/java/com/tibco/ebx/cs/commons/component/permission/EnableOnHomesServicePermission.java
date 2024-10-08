/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ActionPermission;
import com.orchestranetworks.service.ServicePermission;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.HomeUtils;

/**
 *
 * Enable a service on specified homes and their descendants or just its descendants including versions or not. Hide on the others.
 *
 * <pre>
 * {@code
 *         <osd:service resourcePath="/myFirstPage.jsp" class="com.orchestranetworks.ps.commons.permissions.EnableOnHomesServicePermission" >
 *         	<homeName>...</homeName>
 *         	<regexp>...</regexp>
 *         	<includingDescendants>false</includingDescendants>
 *         	<descendantsOnly>false</descendantsOnly>
 *         	<includingVersions>false</includingVersions>
 *         </osd:service>
 * }
 * </pre>
 *
 * @author Mickaël Chevalier
 */
public class EnableOnHomesServicePermission implements ServicePermission {

	/** The home name. */
	private String homeName;

	/** The regexp. */
	private boolean regexp;

	/** The including descendants. */
	private boolean includingDescendants = false;

	/** The descendants only. */
	private boolean descendantsOnly = false;

	/** The including versions. */
	private boolean includingVersions = false;

	/**
	 * Gets the home name.
	 *
	 * @return the home name
	 */
	public String getHomeName() {
		return this.homeName;
	}

	/*
	 * @see com.orchestranetworks.service.ServicePermission#getPermission(com. orchestranetworks.schema.SchemaNode, com.onwbp.adaptation.Adaptation, com.orchestranetworks.service.Session)
	 */
	@Override
	public ActionPermission getPermission(final SchemaNode pNode, final Adaptation pAdaptation, final Session pSession) {

		AdaptationHome currentHome = pAdaptation.getHome();
		if (!this.includingVersions && currentHome.isVersion()) {
			return ActionPermission.getHidden();
		}

		if (this.nameMatches(currentHome.getKey().getName())) {
			if (this.descendantsOnly) {
				return ActionPermission.getHidden();
			}
			return ActionPermission.getEnabled();
		}

		if (this.includingDescendants) {
			List<AdaptationHome> ancestors = HomeUtils.getAncestors(pAdaptation.getHome());
			for (AdaptationHome ancestor : ancestors) {
				if (this.nameMatches(ancestor.getKey().getName())) {
					return ActionPermission.getEnabled();
				}
			}
		}

		return ActionPermission.getHidden();
	}

	/**
	 * Checks if is descendants.
	 *
	 * @return true, if is descendants
	 */
	public boolean isDescendants() {
		return this.includingDescendants;
	}

	/**
	 * Checks if is descendants only.
	 *
	 * @return true, if is descendants only
	 */
	public boolean isDescendantsOnly() {
		return this.descendantsOnly;
	}

	/**
	 * Checks if is including versions.
	 *
	 * @return true, if is including versions
	 */
	public boolean isIncludingVersions() {
		return this.includingVersions;
	}

	/**
	 * Checks if is regexp.
	 *
	 * @return true, if is regexp
	 */
	public boolean isRegexp() {
		return this.regexp;
	}

	/**
	 * Name matches.
	 *
	 * @param pName the name
	 * @return true, if successful
	 */
	private boolean nameMatches(final String pName) {
		return pName.equals(this.homeName) || this.regexp && pName.matches(this.homeName);
	}

	/**
	 * Sets the descendants.
	 *
	 * @param descendants the new descendants
	 */
	public void setDescendants(final boolean descendants) {
		this.includingDescendants = descendants;
	}

	/**
	 * Sets the descendants only.
	 *
	 * @param descendantsOnly the new descendants only
	 */
	public void setDescendantsOnly(final boolean descendantsOnly) {
		this.descendantsOnly = descendantsOnly;
	}

	/**
	 * Sets the home name.
	 *
	 * @param homeName the new home name
	 */
	public void setHomeName(final String homeName) {
		this.homeName = homeName;
	}

	/**
	 * Sets the including versions.
	 *
	 * @param includingVersions the new including versions
	 */
	public void setIncludingVersions(final boolean includingVersions) {
		this.includingVersions = includingVersions;
	}

	/**
	 * Sets the regexp.
	 *
	 * @param regexp the new regexp
	 */
	public void setRegexp(final boolean regexp) {
		this.regexp = regexp;
	}
}