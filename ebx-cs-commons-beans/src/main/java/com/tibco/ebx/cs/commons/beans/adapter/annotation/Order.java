/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.annotation;

import com.onwbp.adaptation.RequestSortCriteria;

/**
 * Enumeration of sort criteria possible orders
 * 
 * @author Gilles Mayer
 */
public enum Order {
	/** Ascending order */
	ASC(true),
	/** Descending order */
	DESC(false);

	/**
	 * Matches the semantic of the second parameter value of the <br>
	 * {@link RequestSortCriteria#add(com.orchestranetworks.schema.Path, boolean)} method
	 */
	public final boolean isAscendant;

	Order(final boolean isAscendant) {
		this.isAscendant = isAscendant;
	}
}