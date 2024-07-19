/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.HashMap;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;

/**
 * The Class RecordValuesBean.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class RecordValuesBean {
	private AdaptationTable table;
	private Adaptation record;
	private HashMap<Path, Object> values;

	/**
	 * Instantiates a new record values bean. <br>
	 * For an update procedure: a record and values are required.<br>
	 * For a creation procedure: a table and a values map are required, a record is required for a duplication (set at procedure level). <br>
	 * For a delete procedure: only a record is required.
	 *
	 * @since 1.0.0
	 */
	public RecordValuesBean() {
	}

	/**
	 * Instantiates a new record values bean. <br>
	 * For an update procedure: a record and values are required.<br>
	 * For a creation procedure: a table and a values map are required, a record is required for a duplication (set at procedure level). <br>
	 * For a delete procedure: only a record is required.
	 *
	 * @param record the record
	 * @param values the values
	 * @since 1.0.0
	 */
	public RecordValuesBean(final Adaptation record, final HashMap<Path, Object> values) {
		this.record = record;
		this.values = values;
	}

	/**
	 * Instantiates a new record values bean. <br>
	 * For an update procedure: a record and values are required.<br>
	 * For a creation procedure: a table and a values map are required, a record is required for a duplication (set at procedure level). <br>
	 * For a delete procedure: only a record is required.
	 *
	 * @param table  the table
	 * @param record the record
	 * @param values the values
	 * @since 1.0.0
	 */
	public RecordValuesBean(final AdaptationTable table, final Adaptation record, final HashMap<Path, Object> values) {
		this.table = table;
		this.record = record;
		this.values = values;
	}

	/**
	 * Instantiates a new record values bean. <br>
	 * For an update procedure: a record and values are required.<br>
	 * For a creation procedure: a table and a values map are required, a record is required for a duplication (set at procedure level). <br>
	 * For a delete procedure: only a record is required.
	 *
	 * @param table  the table
	 * @param values the values
	 * @since 1.0.0
	 */
	public RecordValuesBean(final AdaptationTable table, final HashMap<Path, Object> values) {
		this.table = table;
		this.values = values;
	}

	/**
	 * Gets the record.
	 *
	 * @return the record
	 * @since 1.0.0
	 */
	public Adaptation getRecord() {
		return this.record;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 * @since 1.0.0
	 */
	public AdaptationTable getTable() {
		return this.table;
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 * @since 1.0.0
	 */
	public HashMap<Path, Object> getValues() {
		return this.values;
	}

	/**
	 * Sets the record.
	 *
	 * @param record the new record
	 * @since 1.0.0
	 */
	public void setRecord(final Adaptation record) {
		this.record = record;
	}

	/**
	 * Sets the table.
	 *
	 * @param table the new table
	 * @since 1.0.0
	 */
	public void setTable(final AdaptationTable table) {
		this.table = table;
	}

	/**
	 * Sets the values.
	 *
	 * @param values the values
	 * @since 1.0.0
	 */
	public void setValues(final HashMap<Path, Object> values) {
		this.values = values;
	}

}
