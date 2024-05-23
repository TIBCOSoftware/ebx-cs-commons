package com.tibco.ebx.cs.commons.addon.tese.ajaxsearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.addon.tese.DatasetSearchResult;
import com.orchestranetworks.addon.tese.RecordSearchResult;
import com.orchestranetworks.addon.tese.SearchContext;
import com.orchestranetworks.addon.tese.SearchOperationsFactory;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.Session;

/**
 * The Class SearchEngine.
 *
 * @author Aurélien Ticot
 * @author PAS
 * @since 1.0.0
 */
final class SearchEngine {
	private final Adaptation dataSet;
	private final Session session;
	private final Locale locale;

	/**
	 * Instantiates a new search engine.
	 *
	 * @param pDataSet the data set
	 * @param pSession the session
	 * @since 1.0.0
	 */
	protected SearchEngine(final Adaptation pDataSet, final Session pSession) {
		this.dataSet = pDataSet;
		this.session = pSession;
		this.locale = pSession.getLocale();
	}

	/**
	 * Manage results.
	 *
	 * @param pSearchResult       the search result
	 * @param pSearchedTableNodes the searched table nodes
	 * @return the array list
	 * @since 1.0.0
	 */
	private List<SearchResultBean> manageResults(final DatasetSearchResult pSearchResult, final List<SchemaNode> pSearchedTableNodes) {
		List<SearchResultBean> results = new ArrayList<>();

		for (SchemaNode searchedTableNode : pSearchedTableNodes) {
			AdaptationTable table = this.dataSet.getTable(searchedTableNode.getPathInSchema());
			if (table == null) {
				continue;
			}

			List<RecordSearchResult> tableResults = pSearchResult.getResults(searchedTableNode);
			if (tableResults == null) {
				return results;
			}

			for (RecordSearchResult recordSearchResult : tableResults) {
				PrimaryKey primaryKey = recordSearchResult.getPrimaryKey();
				Adaptation record = table.lookupAdaptationByPrimaryKey(primaryKey);
				BigDecimal score = recordSearchResult.getScore();

				SearchResultBean resultBean = new SearchResultBean(record, score);
				if (record != null && score != null) {
					results.add(resultBean);
				}
			}
		}

		Collections.sort(results, new SearchResultBeanComparator());

		return results;
	}

	/**
	 * Search.
	 *
	 * @param pSearchedTableNodes the searched table nodes
	 * @param pQuery              the query
	 * @param pSearchSensibility  the search sensibility
	 * @return the array list
	 * @since 1.0.0
	 */
	protected List<SearchResultBean> search(final List<SchemaNode> pSearchedTableNodes, final String pQuery, final BigDecimal pSearchSensibility) {
		SearchContext searchContext = new SearchContext(this.dataSet, this.locale, pSearchedTableNodes, pQuery, pSearchSensibility, this.session);
		DatasetSearchResult searchResult = SearchOperationsFactory.getSearchOperations().searchDataset(searchContext);
		return this.manageResults(searchResult, pSearchedTableNodes);
	}
}
