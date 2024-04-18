package com.tibco.ebx.cs.commons.ui.userservice.historytimeline;

import com.orchestranetworks.schema.Path;
import com.tibco.ebx.cs.commons.ui.userservice.historytimeline.HistoryTimelineTransaction.OperationType;

/**
 * The Class HistoryTimelineTransactionFieldValue.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class HistoryTimelineTransactionFieldValue {
	private Path path;
	private String value;
	private String operation;

	/**
	 * Instantiates a new history timeline transaction field value.
	 *
	 * @param path  the path
	 * @param value the value
	 * @since 1.0.0
	 */
	public HistoryTimelineTransactionFieldValue(final Path path, final String value) {
		this.path = path;
		this.value = value;
	}

	/**
	 * Instantiates a new history timeline transaction field value.
	 *
	 * @param path      the path
	 * @param value     the value
	 * @param operation the operation
	 * @since 1.0.0
	 */
	public HistoryTimelineTransactionFieldValue(final Path path, final String value, final String operation) {
		this.path = path;
		this.value = value;
		this.operation = operation;
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 * @since 1.0.0
	 */
	public String getGroup() {
		if (this.path == null) {
			return "";
		} else {
			return this.path.format();
		}
	}

	/**
	 * Gets the operation.
	 *
	 * @return the operation
	 * @since 1.0.0
	 */
	public String getOperation() {
		return this.operation;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 * @since 1.0.0
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 * @since 1.0.0
	 */
	public String getStyle() {
		String op = this.getOperation();
		if (op == null) {
			return HistoryTimelineTransaction.DEFAULT_ITEM_STYLE;
		} else if (op.equals(OperationType.creation)) {
			return HistoryTimelineTransaction.CREATION_ITEM_STYLE;
		} else if (op.equals(OperationType.modification)) {
			return HistoryTimelineTransaction.MODIFICATION_ITEM_STYLE;
		} else if (op.equals(OperationType.deletion)) {
			return HistoryTimelineTransaction.DELETION_ITEM_STYLE;
		} else {
			return HistoryTimelineTransaction.DEFAULT_ITEM_STYLE;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 * @since 1.0.0
	 */
	public String getValue() {
		if (this.value == null) {
			return "";
		} else {
			return this.value;
		}
	}

	/**
	 * Sets the operation.
	 *
	 * @param operation the new operation
	 * @since 1.0.0
	 */
	public void setOperation(final String operation) {
		this.operation = operation;
	}

	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 * @since 1.0.0
	 */
	public void setPath(final Path path) {
		this.path = path;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 * @since 1.0.0
	 */
	public void setValue(final String value) {
		this.value = value;
	}
}
