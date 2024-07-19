/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.addon.tese.ajaxsearch;

import java.math.BigDecimal;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;

/**
 * The Class SearchResultBean.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
final class SearchResultBean {
	private Adaptation record;
	private BigDecimal score;

	/**
	 * Instantiates a new search result bean.
	 *
	 * @since 1.0.0
	 */
	protected SearchResultBean() {
	}

	/**
	 * Instantiates a new search result bean.
	 *
	 * @param record the record
	 * @param score  the score
	 * @since 1.0.0
	 */
	protected SearchResultBean(final Adaptation record, final BigDecimal score) {
		this.record = record;
		this.score = score;
	}

	/**
	 * Gets the reason.
	 *
	 * @return the reason
	 * @since 1.0.0
	 */
	protected UserMessage getReason() {
		UserMessage reason = UserMessage.createInfo(this.getScore() + "%");
		return reason;
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
	 * Gets the score.
	 *
	 * @return the score
	 * @since 1.0.0
	 */
	protected BigDecimal getScore() {
		return this.score;
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
	 * Sets the score.
	 *
	 * @param score the new score
	 * @since 1.0.0
	 */
	protected void setScore(final BigDecimal score) {
		this.score = score;
	}

}
