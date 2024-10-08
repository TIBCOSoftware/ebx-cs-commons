/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright � Orchestra Networks 2000-2006. All rights reserved.
 */
package com.tibco.ebx.cs.commons.lib.utils;

import java.util.ArrayList;
import java.util.List;

import com.onwbp.adaptation.AdaptationHome;

/**
 *
 * Collector of Homes.
 * 
 * @author Mickaël Chevalier
 */
public class HomeCollector {

	/** The Constant ALL_LEVELS. */
	public static final int ALL_LEVELS = -1;

	/** The including branch. */
	private boolean includingBranch = true;

	/** The including version. */
	private boolean includingVersion = true;

	/** The including open. */
	private boolean includingOpen = true;

	/** The including closed. */
	private boolean includingClosed = false;

	/** The including initial version. */
	private boolean includingInitialVersion = true;

	/** The including technical branch. */
	private boolean includingTechnicalBranch = false;

	/** The including technical version. */
	private boolean includingTechnicalVersion = false;

	/**
	 * Collect homes.
	 *
	 * @param pRootHome    the root home
	 * @param pIncludeRoot include Root or not
	 * @return the list
	 */
	public List<AdaptationHome> collectHomes(final AdaptationHome pRootHome, final boolean pIncludeRoot) {
		return this.collectHomes(pRootHome, pIncludeRoot, null);
	}

	/**
	 * Collect homes.
	 *
	 * @param pRootHome    the root home
	 * @param pIncludeRoot include Root or not
	 * @param pPattern     pattern to use
	 * 
	 * @return the list
	 */
	public List<AdaptationHome> collectHomes(final AdaptationHome pRootHome, final boolean pIncludeRoot, final String pPattern) {
		List<AdaptationHome> homes = this.collectHomes(pRootHome, ALL_LEVELS, pPattern);
		if (pIncludeRoot) {
			homes.add(pRootHome);
		}
		return homes;
	}

	/**
	 * Collect homes.
	 *
	 * @author MCH
	 * @param pRootHome the root home
	 * @param pLevel    the level
	 * @return the list
	 */
	public List<AdaptationHome> collectHomes(final AdaptationHome pRootHome, final int pLevel) {
		return this.collectHomes(pRootHome, pLevel, null);
	}

	private List<AdaptationHome> collectHomes(final AdaptationHome pRootHome, final int pLevel, final String pPattern) {
		List<AdaptationHome> homes = new ArrayList<>();

		if (pRootHome == null) {
			return homes;
		}

		List<AdaptationHome> children = null;
		if (pRootHome.isBranch()) {
			children = pRootHome.getVersionChildren();
		} else {
			children = pRootHome.getBranchChildren();
		}

		for (AdaptationHome home : children) {

			if (home.isVersion() && this.includingVersion && (!home.isInitialVersion() || this.includingInitialVersion) && (!home.isTechnicalVersion() || this.includingTechnicalVersion)
					|| home.isBranch() && this.includingBranch && (!home.isTechnicalBranch() || this.includingTechnicalBranch) && (home.isOpen() && this.includingOpen || this.includingClosed)) {
				if (pPattern == null || home.getKey().getName().matches(pPattern)) {
					homes.add(home);
				}
			}

			if (pLevel == ALL_LEVELS || pLevel < 0) {
				homes.addAll(this.collectHomes(home, pLevel - 1));
			}
		}

		return homes;
	}

	/**
	 * Checks if is including branch.
	 *
	 * @return true, if is including branch
	 */
	public boolean isIncludingBranch() {
		return this.includingBranch;
	}

	/**
	 * Checks if is including closed.
	 *
	 * @return true, if is including closed
	 */
	public boolean isIncludingClosed() {
		return this.includingClosed;
	}

	/**
	 * Checks if is including initial version.
	 *
	 * @return true, if is including initial version
	 */
	public boolean isIncludingInitialVersion() {
		return this.includingInitialVersion;
	}

	/**
	 * Checks if is including open.
	 *
	 * @return true, if is including open
	 */
	public boolean isIncludingOpen() {
		return this.includingOpen;
	}

	/**
	 * Checks if is including technical branch.
	 *
	 * @return true, if is including technical branch
	 */
	public boolean isIncludingTechnicalBranch() {
		return this.includingTechnicalBranch;
	}

	/**
	 * Checks if is including technical version.
	 *
	 * @return true, if is including technical version
	 */
	public boolean isIncludingTechnicalVersion() {
		return this.includingTechnicalVersion;
	}

	/**
	 * Checks if is including version.
	 *
	 * @return true, if is including version
	 */
	public boolean isIncludingVersion() {
		return this.includingVersion;
	}

	/**
	 * Sets the including branch.
	 *
	 * @param includingBranch the new including branch
	 */
	public void setIncludingBranch(final boolean includingBranch) {
		this.includingBranch = includingBranch;
	}

	/**
	 * Sets the including closed.
	 *
	 * @param includingClosed the new including closed
	 */
	public void setIncludingClosed(final boolean includingClosed) {
		this.includingClosed = includingClosed;
	}

	/**
	 * Sets the including initial version.
	 *
	 * @param includingInitialVersion the new including initial version
	 */
	public void setIncludingInitialVersion(final boolean includingInitialVersion) {
		this.includingInitialVersion = includingInitialVersion;
	}

	/**
	 * Sets the including open.
	 *
	 * @param includingOpen the new including open
	 */
	public void setIncludingOpen(final boolean includingOpen) {
		this.includingOpen = includingOpen;
	}

	/**
	 * Sets the including technical branch.
	 *
	 * @param includingTechnicalBranch the new including technical branch
	 */
	public void setIncludingTechnicalBranch(final boolean includingTechnicalBranch) {
		this.includingTechnicalBranch = includingTechnicalBranch;
	}

	/**
	 * Sets the including technical version.
	 *
	 * @param includingTechnicalVersion the new including technical version
	 */
	public void setIncludingTechnicalVersion(final boolean includingTechnicalVersion) {
		this.includingTechnicalVersion = includingTechnicalVersion;
	}

	/**
	 * Sets the including version.
	 *
	 * @param includingVersion the new including version
	 */
	public void setIncludingVersion(final boolean includingVersion) {
		this.includingVersion = includingVersion;
	}
}
