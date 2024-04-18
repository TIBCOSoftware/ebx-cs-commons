package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * Modify a record or a data set based on a map between paths and values.
 * 
 * @author Mickaël Chevalier
 */
public class ModifyValuesProcedure extends GenericProcedure {

	/** The record or data set to modify. */
	private Adaptation adaptation;

	/** The map between paths and values. */
	private Map<Path, Object> pathValueMap;

	/**
	 * Instantiates a new procedure.
	 */
	public ModifyValuesProcedure() {

	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pAdaptation the record or data set to modify
	 */
	public ModifyValuesProcedure(final Adaptation pAdaptation) {
		this.adaptation = pAdaptation;
	}

	/**
	 * Instantiates a new procedure.
	 *
	 * @param pAdaptation   the record or data set to modify.
	 * @param pPathValueMap the map between path and values.
	 */
	public ModifyValuesProcedure(final Adaptation pAdaptation, final Map<Path, Object> pPathValueMap) {
		this.adaptation = pAdaptation;
		this.pathValueMap = pPathValueMap;
	}

	/**
	 * Clear amp of values.
	 */
	public void clearValues() {
		this.pathValueMap.clear();
	}

	/*
	 * @see com.orchestranetworks.ps.procedure.GenericProcedure#doExecute(com.orchestranetworks.service.ProcedureContext)
	 */
	@Override
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		if (this.adaptation == null) {
			throw OperationException.createError("Adaptation cannot be null");
		}
		final ValueContextForUpdate vcfu = pContext.getContext(this.adaptation.getAdaptationName());
		for (Map.Entry<Path, Object> entry : this.pathValueMap.entrySet()) {
			vcfu.setValue(entry.getValue(), entry.getKey());
		}
		this.adaptation = pContext.doModifyContent(this.adaptation, vcfu);
	}

	/**
	 * Gets the record or data set to modify.
	 *
	 * @return the record or data set to modify.
	 */
	public Adaptation getAdaptation() {
		return this.adaptation;
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
	 * Sets the the record or data set to modify.
	 *
	 * @param adaptation the record or data set to modify
	 */
	public void setAdaptation(final Adaptation adaptation) {
		this.adaptation = adaptation;
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
		this.pathValueMap.put(pPath, pValue);
	}
}
