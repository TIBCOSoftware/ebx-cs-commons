/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.historytimeline;

import java.util.Date;

import com.tibco.ebx.cs.commons.ui.timeline.TimelineItem;

/**
 * The Class HistoryTimelineTransactionValueItem.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class HistoryTimelineTransactionValueItem extends TimelineItem {
	private final String operation;

	/**
	 * Instantiates a new history timeline transaction value item.
	 *
	 * @param group     the group
	 * @param start     the start
	 * @param end       the end
	 * @param content   the content
	 * @param operation the operation
	 * @since 1.0.0
	 */
	public HistoryTimelineTransactionValueItem(final String group, final Date start, final Date end, final String content, final String operation) {
		super(group, start, end, content);
		this.operation = operation;
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
	 * Equal items.
	 *
	 * @param otherItem the other item
	 * @return true, if successful
	 * @since 1.0.0
	 */
	@Override
	protected boolean equalItems(final TimelineItem otherItem) {
		boolean equal = super.equalItems(otherItem);

		if (equal) {
			String op = this.getOperation();
			if (op == null) {
				return false;
			}

			String otherOp = null;
			try {
				otherOp = ((HistoryTimelineTransactionValueItem) otherItem).getOperation();
			} catch (Exception e) {
				return false;
			}

			return (op.equals(otherOp));
		}
		return equal;
	}
}
