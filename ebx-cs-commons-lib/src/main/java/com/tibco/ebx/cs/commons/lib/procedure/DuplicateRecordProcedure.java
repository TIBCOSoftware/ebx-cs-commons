/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.HashMap;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * Duplicate a record
 * 
 * @author Mickaël Chevalier
 */
public class DuplicateRecordProcedure extends GenericProcedure {
	/** The record to duplicate */
	private Adaptation adaptation;

	/** The resulting, duplicate record */
	private Adaptation createdRecord;

	/** The map between paths and values. */
	private Map<Path, Object> pathValueMap;

	/**
	 * @param pContext     ProcedureContext in which to execute copy
	 * @param adaptation   record to copy
	 * @param pathValueMap values to change on new copy
	 * @return created record
	 * @throws OperationException
	 * @deprecated use instance methods or {@link Procedures.Duplicate#execute(ProcedureContext, Adaptation, Map)} instead.
	 */
	@Deprecated
	public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap) throws OperationException {
		return Procedures.Duplicate.execute(pContext, adaptation, pathValueMap);
	}

	/**
	 * @param pContext                 ProcedureContext in which to execute copy
	 * @param adaptation               record to copy
	 * @param pathValueMap             values to change on new copy
	 * @param enableAllPrivileges
	 * @param disableTriggerActivation
	 * @return created record
	 * @throws OperationException
	 * @deprecated use instance methods or {@link Procedures.Duplicate#execute(ProcedureContext, Adaptation, Map, boolean, boolean)} instead.
	 */
	@Deprecated
	public static Adaptation execute(final ProcedureContext pContext, final Adaptation adaptation, final Map<Path, Object> pathValueMap, final boolean enableAllPrivileges,
			final boolean disableTriggerActivation) throws OperationException {
		return Procedures.Duplicate.execute(pContext, adaptation, pathValueMap, enableAllPrivileges, disableTriggerActivation);
	}

	public DuplicateRecordProcedure() {
	}

	/**
	 * Constructor
	 * 
	 * @param adaptation   adaptation
	 * @param pathValueMap pathValueMap
	 */
	public DuplicateRecordProcedure(final Adaptation adaptation, final Map<Path, Object> pathValueMap) {
		this.adaptation = adaptation;
		setPathValueMap(pathValueMap);
	}

	public Adaptation getAdaptation() {
		return adaptation;
	}

	public void setAdaptation(final Adaptation adaptation) {
		this.adaptation = adaptation;
	}

	public Adaptation getCreatedRecord() {
		return this.createdRecord;
	}

	public void setCreatedRecord(final Adaptation createdRecord) {
		this.createdRecord = createdRecord;
	}

	/**
	 * Gets map between paths and values.
	 *
	 * @return the map between paths and values.
	 */
	public Map<Path, Object> getPathValueMap() {
		return this.pathValueMap;
	}

	/**
	 * Sets the map between paths and values.
	 *
	 * @param pathValueMap the map between paths and values.
	 */
	public void setPathValueMap(final Map<Path, Object> pathValueMap) {
		this.pathValueMap = pathValueMap;
	}

	/**
	 * Add a value to the map between paths and values or modify it.
	 *
	 * @param pPath  the path
	 * @param pValue the value
	 */
	public void setValue(final Path pPath, final Object pValue) {
		if (this.pathValueMap == null) {
			this.pathValueMap = new HashMap<>();
		}
		this.pathValueMap.put(pPath, pValue);
	}

	/**
	 * Clear amp of values.
	 */
	public void clearValues() {
		this.pathValueMap.clear();
	}

	/**
	 * Return true if the pathValueMap is null or empty
	 */
	public boolean isEmpty() {
		return this.pathValueMap == null || this.pathValueMap.isEmpty();
	}

	protected AdaptationHome getHome() {
		return getAdaptation().getHome();
	}

	@Override
	public void doExecute(final ProcedureContext pContext) throws OperationException {
		if (this.adaptation == null) {
			throw OperationException.createError("Adaptation cannot be null");
		}
		final ValueContextForUpdate vcfu = pContext.getContextForNewOccurrence(adaptation, adaptation.getContainerTable());
		Map<Path, Object> pvm = getPathValueMap();
		if (pvm != null) {
			for (Map.Entry<Path, Object> entry : pvm.entrySet()) {
				vcfu.setValue(entry.getValue(), entry.getKey());
			}
		}
		this.createdRecord = pContext.doCreateOccurrence(vcfu, this.adaptation.getContainerTable());
	}

}
