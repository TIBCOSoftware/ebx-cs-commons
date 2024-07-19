/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.template;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;

/**
 * Java Bean representing a table occurrence in EBX, also called a record.
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public abstract class TableBean extends EBXBean{

	protected Adaptation ebxRecord;

	protected ValueContext ebxContext;

	/**
	 * Get the persisted record in EBX corresponding to this Java Bean.
	 * If it return null, it does not necessarily mean that the Java Bean does not represent a persisted record in EBX.
	 *
	 * @return An instance {@link Adaptation}, or null.
	 */
	public Adaptation getEbxRecord() {
		return this.ebxRecord;
	}

	/**
	 * Set a reference to the persisted record in EBX corresponding to this Java Bean.
	 */
	public void setEbxRecord(final Adaptation ebxRecord) {
		this.ebxRecord = ebxRecord;
	}

	/**
	 * Get the value context in EBX corresponding with this Java Bean.
	 *
	 * @return An instance of {@link ValueContext}, or null.
	 */
	public ValueContext getEbxContext() {
		return this.ebxContext;
	}

	/**
	 * Set a reference to the value context in EBX corresponding to this Java Bean.
	 */
	public void setEbxContext(final ValueContext ebxContext) {
		this.ebxContext = ebxContext;
	}

	/**
	 * Verify if the bean is bound to a table occurrence or a value context.
	 */
	protected boolean isBoundToEBX() {
		return this.ebxRecord != null  || this.ebxContext != null;
	}

	protected abstract TableDAO<?> getDAO();
}
