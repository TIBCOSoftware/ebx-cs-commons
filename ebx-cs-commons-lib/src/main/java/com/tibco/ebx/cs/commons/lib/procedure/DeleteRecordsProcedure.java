/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.ArrayList;
import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.ProcedureContext;

/**
 * Delete a record or a data set.
 * 
 * @author Mickaël Chevalier
 */
public class DeleteRecordsProcedure extends GenericProcedure {

	/** The list of data sets or records to delete. */
	private List<Adaptation> records;

	private boolean allPrivileges = true;

	/** Deleting children. */
	private boolean deletingChildren;

	/**
	 * Instantiates a new delete records procedure. Passing the record to delete in argument.
	 *
	 * @param record the record
	 * @since 1.0.0
	 */
	public DeleteRecordsProcedure(final Adaptation record) {
		super();
		this.records = new ArrayList<>();
		this.records.add(record);
	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pRecords the list of records or data sets to delete
	 */
	public DeleteRecordsProcedure(final List<Adaptation> pRecords) {
		this.records = pRecords;
	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pRecords           the list of records or data sets to delete
	 * @param isDeletingChildren If true, the data set children will be deleted as well
	 */
	public DeleteRecordsProcedure(final List<Adaptation> pRecords, final boolean isDeletingChildren) {
		this.records = pRecords;
		this.deletingChildren = isDeletingChildren;
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
	 * Sets the all privileges. If true execute the procedure without any permission restriction. Default is true;
	 *
	 * @param pAllPrivileges the new all privileges
	 */
	@Override
	public void setAllPrivileges(final boolean pAllPrivileges) {
		this.allPrivileges = pAllPrivileges;
	}

	/**
	 * Define if data set children will be deleted as well.
	 *
	 * @param deletingChildren Set to true, if data set children must be deleted.
	 */
	public void setDeletingChildren(final boolean deletingChildren) {
		this.deletingChildren = deletingChildren;
	}

	/**
	 * Sets the list of records or data sets to delete
	 *
	 * @param pRecords the list of records or data sets to delete
	 */
	public void setRecords(final List<Adaptation> pRecords) {
		this.records = pRecords;
	}

	/*
	 * @see com.orchestranetworks.ps.procedure.GenericProcedure#doExecute(com. orchestranetworks.service.ProcedureContext)
	 */
	@Override
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		pContext.setAllPrivileges(this.allPrivileges);
		if (this.records != null && !this.records.isEmpty()) {
			for (Adaptation record : this.records) {
				if (!record.isDeleted()) {
					pContext.doDelete(record.getAdaptationName(), this.deletingChildren);
				}
			}
		}
	}
}
