/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.recordscomparison;

import java.util.List;

import com.onwbp.adaptation.Adaptation;

/**
 * This bean is related to the record comparison class. Its aims to store all information of a particular data model field.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class RecordComparisonItemValue {
	private List<String> labels = null;
	private List<Adaptation> records = null;
	private int nbValue = 0;

	/**
	 * Instantiates a new record comparison item value.
	 *
	 * @since 1.0.0
	 */
	protected RecordComparisonItemValue() {
	}

	/**
	 * Instantiates a new record comparison item value.
	 *
	 * @param labels the list of labels representing the values
	 * @since 1.0.0
	 */
	protected RecordComparisonItemValue(final List<String> labels) {
		this.labels = labels;
		this.nbValue = labels.size();
	}

	/**
	 * Instantiates a new record comparison item value.
	 *
	 * @param labels  the list of labels representing the values
	 * @param records the list of records representing the values
	 * @since 1.0.0
	 */
	protected RecordComparisonItemValue(final List<String> labels, final List<Adaptation> records) {
		this.labels = labels;
		this.records = records;
		this.nbValue = labels.size();
	}

	/**
	 * Gets the labels.
	 *
	 * @return the list of labels representing the values. A list is used in case of multi-occurence or association.
	 * @since 1.0.0
	 */
	public List<String> getLabels() {
		return this.labels;
	}

	/**
	 * Gets the records.
	 *
	 * @return the list of records representing the values (FK or association). A list is used in case of multi-occurence or association.
	 * @since 1.0.0
	 */
	public List<Adaptation> getRecords() {
		return this.records;
	}

	/**
	 * Gets the nb value.
	 *
	 * @return the number of value.
	 * @since 1.0.0
	 */
	protected int getNbValue() {
		return this.nbValue;
	}

	/**
	 * Set the labels of the value. A list is used in case of multi-occurence or association.
	 *
	 * @param labels the list of labels representing the values
	 * @since 1.0.0
	 */
	protected void setLabels(final List<String> labels) {
		this.labels = labels;
		this.nbValue = labels.size();
	}

	/**
	 * Set the records as value in case of FK or association. A list is used in case of multi-occurence or association.
	 *
	 * @param records the list of records representing the values
	 * @since 1.0.0
	 */
	protected void setRecords(final List<Adaptation> records) {
		this.records = records;
	}

}
