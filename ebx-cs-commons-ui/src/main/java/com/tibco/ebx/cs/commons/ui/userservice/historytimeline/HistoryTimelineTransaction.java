/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.historytimeline;

import java.util.Date;
import java.util.List;

import com.onwbp.adaptation.Adaptation;

/**
 * The Class HistoryTimelineTransaction.
 * 
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class HistoryTimelineTransaction {
	/**
	 * The Class OperationType.
	 */
	protected class OperationType {
		private OperationType() {
			super();
		}

		protected static final String modification = "U";
		protected static final String creation = "C";
		protected static final String deletion = "D";
	}

	protected static final String TRANSACTION_GROUP_ID = "transactions";
	protected static final String DEFAULT_ITEM_STYLE = "background-color: #E0E0FF;";
	protected static final String CREATION_ITEM_STYLE = "background-color: #E0FFE0;border-color: LimeGreen;";
	protected static final String MODIFICATION_ITEM_STYLE = "background-color: #E0E0FF;";
	protected static final String DELETION_ITEM_STYLE = "background-color: #F0D0D0;border-color: #FF8042;";

	private int index;
	private Adaptation record;
	private String recordPK;
	private String transactionId;
	private Date startDate;
	private Date endDate;
	private String operation;
	private String user;
	private List<HistoryTimelineTransactionFieldValue> fieldValues;

	/**
	 * Instantiates a new history timeline transaction.
	 *
	 * @param record    the record
	 * @param startDate the start date
	 * @param endDate   the end date
	 * @since 1.0.0
	 */
	protected HistoryTimelineTransaction(final Adaptation record, final Date startDate, final Date endDate) {
		this.record = record;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 * @since 1.0.0
	 */
	protected Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Gets the field values.
	 *
	 * @return the field values
	 * @since 1.0.0
	 */
	protected List<HistoryTimelineTransactionFieldValue> getFieldValues() {
		return this.fieldValues;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 * @since 1.0.0
	 */
	protected int getIndex() {
		return this.index;
	}

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 * @since 1.0.0
	 */
	protected String getOperation() {
		return this.operation;
	}

	/**
	 * Gets the record.
	 *
	 * @return the record
	 * @since 1.0.0
	 */
	protected Adaptation getRecord() {
		return this.record;
	}

	/**
	 * Gets the record pk.
	 *
	 * @return the record pk
	 * @since 1.0.0
	 */
	protected String getRecordPK() {
		return this.recordPK;
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 * @since 1.0.0
	 */
	protected Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 * @since 1.0.0
	 */
	protected String getStyle() {
		String op = this.getOperation();
		if (op == null) {
			return "";
		} else if (op.equals(OperationType.creation)) {
			return HistoryTimelineTransaction.CREATION_ITEM_STYLE;
		} else if (op.equals(OperationType.modification)) {
			return HistoryTimelineTransaction.MODIFICATION_ITEM_STYLE;
		} else if (op.equals(OperationType.deletion)) {
			return HistoryTimelineTransaction.DELETION_ITEM_STYLE;
		} else {
			return "";
		}
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return the transaction id
	 * @since 1.0.0
	 */
	protected String getTransactionId() {
		return this.transactionId;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 * @since 1.0.0
	 */
	protected String getUser() {
		return this.user;
	}

	/**
	 * Sets the end date.
	 *
	 * @param endDate the new end date
	 * @since 1.0.0
	 */
	protected void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Sets the field values.
	 *
	 * @param fieldValues the new field values
	 * @since 1.0.0
	 */
	protected void setFieldValues(final List<HistoryTimelineTransactionFieldValue> fieldValues) {
		this.fieldValues = fieldValues;
	}

	/**
	 * Sets the index.
	 *
	 * @param index the new index
	 * @since 1.0.0
	 */
	protected void setIndex(final int index) {
		this.index = index;
	}

	/**
	 * Sets the operation.
	 *
	 * @param operation the new operation
	 * @since 1.0.0
	 */
	protected void setOperation(final String operation) {
		this.operation = operation;
	}

	/**
	 * Sets the record.
	 *
	 * @param record the new record
	 * @since 1.0.0
	 */
	protected void setRecord(final Adaptation record) {
		this.record = record;
	}

	/**
	 * Sets the record pk.
	 *
	 * @param recordPK the new record pk
	 * @since 1.0.0
	 */
	protected void setRecordPK(final String recordPK) {
		this.recordPK = recordPK;
	}

	/**
	 * Sets the start date.
	 *
	 * @param startDate the new start date
	 * @since 1.0.0
	 */
	protected void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the transaction id.
	 *
	 * @param transactionId the new transaction id
	 * @since 1.0.0
	 */
	protected void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 * @since 1.0.0
	 */
	protected void setUser(final String user) {
		this.user = user;
	}

}
