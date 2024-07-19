/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;

/**
 * Delete a record or a data set.
 * 
 * @author Mickaël Chevalier
 */
public class DeleteRecordProcedure extends GenericProcedure {

	/** The adaptation or record to delete. */
	private Adaptation adaptation;

	/** Deleting children. */
	private boolean deletingChildren;

	/**
	 * Instantiates a new procedure.
	 */
	public DeleteRecordProcedure() {

	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pAdaptation the record or data set to delete
	 */
	public DeleteRecordProcedure(final Adaptation pAdaptation) {
		this.adaptation = pAdaptation;
	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pAdaptation        the record or data set to delete
	 * @param isDeletingChildren If true, the data set children will be deleted as well
	 */
	public DeleteRecordProcedure(final Adaptation pAdaptation, final boolean isDeletingChildren) {
		this.adaptation = pAdaptation;
		this.deletingChildren = isDeletingChildren;
	}

	/*
	 * @see com.orchestranetworks.ps.procedure.GenericProcedure#doExecute(com.orchestranetworks.service.ProcedureContext)
	 */
	@Override
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		if (this.adaptation == null) {
			throw OperationException.createError("Adaptation cannot be null");
		}
		pContext.doDelete(this.adaptation.getAdaptationName(), this.deletingChildren);
	}

	/**
	 * Gets the record or data set to delete.
	 *
	 * @return the adaptation
	 */
	public Adaptation getAdaptation() {
		return this.adaptation;
	}

	/**
	 * Checks if data set children will be deleted.
	 *
	 * @return true, if data set children will be deleted.
	 */
	public boolean isDeletingChildren() {
		return this.deletingChildren;
	}

	/**
	 * Sets the record or data set to delete.
	 *
	 * @param adaptation the record or data set to delete
	 */
	public void setAdaptation(final Adaptation adaptation) {
		this.adaptation = adaptation;
	}

	/**
	 * Define if data set children will be deleted as well.
	 *
	 * @param deletingChildren Set to true, if data set children must be deleted.
	 */
	public void setDeletingChildren(final boolean deletingChildren) {
		this.deletingChildren = deletingChildren;
	}
}
