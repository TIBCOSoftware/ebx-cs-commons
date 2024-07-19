/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.addon.tese.ajaxsearch;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * The Class SearchResultBeanComparator.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
final class SearchResultBeanComparator implements Comparator<SearchResultBean> {
	@Override
	public int compare(final SearchResultBean pResult1, final SearchResultBean pResult2) {
		BigDecimal scoreO2 = pResult2.getScore();
		BigDecimal scoreO1 = pResult1.getScore();
		return scoreO2.compareTo(scoreO1);
	}
}
