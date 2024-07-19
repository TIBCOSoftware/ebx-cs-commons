/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.HashMap;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * Create a record in a table with a map between Paths and values as input.
 * 
 * @author Mickaël Chevalier
 */
public class CreateRecordProcedure extends GenericProcedure {

	/** The created record. */
	private Adaptation createdRecord;

	/** The table. */
	private AdaptationTable table;

	/** Map between paths and values. */
	private Map<Path, Object> pathValueMap = new HashMap<>();

	/** Reset value context between creations. */
	private boolean resetValueContextBetweenCreations = true;

	/** The vcfu. */
	private ValueContextForUpdate vcfu;

	/**
	 * Instantiates a new creates the record procedure.
	 */
	public CreateRecordProcedure() {

	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pTable the table
	 */
	public CreateRecordProcedure(final AdaptationTable pTable) {
		this.table = pTable;
	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pTable        the table
	 * @param pPathValueMap the map between paths and values
	 */
	public CreateRecordProcedure(final AdaptationTable pTable, final Map<Path, Object> pPathValueMap) {
		this.table = pTable;
		this.pathValueMap = pPathValueMap;
	}

	/**
	 * Clear the map of values.
	 */
	public void clearValues() {
		this.pathValueMap.clear();
	}

	/*
	 * @see com.orchestranetworks.ps.procedure.GenericProcedure#doExecute(com.orchestranetworks.service.ProcedureContext)
	 */
	@Override
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		if (this.table == null) {
			throw OperationException.createError("Table cannot be null");
		}
		if ((this.vcfu == null) || this.resetValueContextBetweenCreations) {
			this.vcfu = pContext.getContextForNewOccurrence(this.table);
		}

		for (Map.Entry<Path, Object> entry : this.pathValueMap.entrySet()) {
			this.vcfu.setValue(entry.getValue(), entry.getKey());
		}

		this.createdRecord = pContext.doCreateOccurrence(this.vcfu, this.table);
	}

	/**
	 * Gets the created record.
	 *
	 * @return the created record
	 */
	public Adaptation getCreatedRecord() {
		return this.createdRecord;
	}

	/**
	 * Gets the map between paths and values
	 *
	 * @return the path value map
	 */
	public Map<Path, Object> getPathValueMap() {
		return this.pathValueMap;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public AdaptationTable getTable() {
		return this.table;
	}

	/**
	 * Gets the vcfu.
	 *
	 * @return the vcfu
	 */
	public ValueContextForUpdate getVcfu() {
		return this.vcfu;
	}

	/**
	 * If true, a new value context will be created at each execution. If false, the same value context can be kept for all execution. It can improve performance as value context creation are
	 * expensive. However, the developer must ensure that all values in the map are relevant for further creation with the same procedure.
	 *
	 * @return true, if value context are reset between creations
	 */
	public boolean isResetValueContextBetweenCreations() {
		return this.resetValueContextBetweenCreations;
	}

	/**
	 * Replace the whole map between paths and values.
	 *
	 * @param pathValueMap the new map
	 */
	public void setPathValueMap(final Map<Path, Object> pathValueMap) {
		this.pathValueMap = pathValueMap;
	}

	/**
	 * If true, a new value context will be created at each execution. If false, the same value context can be kept for all execution. It can improve performance as value context creation are
	 * expensive. However, the developer must ensure that all values in the map are relevant for further creation with the same procedure.
	 *
	 * @param resetValueContextBetweenCreations the new reset value context between creations
	 */
	public void setResetValueContextBetweenCreations(final boolean resetValueContextBetweenCreations) {
		this.resetValueContextBetweenCreations = resetValueContextBetweenCreations;
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 */
	public void setTable(final AdaptationTable table) {
		this.table = table;
	}

	/**
	 * Add a value to the map or change it
	 *
	 * @param pPath  the path
	 * @param pValue the value
	 */
	public void setValue(final Path pPath, final Object pValue) {
		this.pathValueMap.put(pPath, pValue);
	}

	/**
	 * Sets the value context to use to create the record.
	 *
	 * @param vcfu the new valut context
	 */
	public void setVcfu(final ValueContextForUpdate vcfu) {
		this.vcfu = vcfu;
	}
}
