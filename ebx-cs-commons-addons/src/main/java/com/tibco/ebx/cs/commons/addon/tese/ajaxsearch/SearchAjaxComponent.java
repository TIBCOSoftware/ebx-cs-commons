package com.tibco.ebx.cs.commons.addon.tese.ajaxsearch;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.UIAjaxComponent;
import com.orchestranetworks.ui.UIAjaxContext;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UICSSClasses;
import com.orchestranetworks.ui.UIHttpManagerComponent;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;

/**
 * The Class SearchAjaxComponent.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public abstract class SearchAjaxComponent extends UIAjaxComponent {
	private static final String OPEN_TH = "<th>";
	private static final String OPEN_TD = "<td>";
	private static final String CLOSE_TD = "</td>";
	private static final String CLOSE_TH = "</th>";
	private UIAjaxContext context;
	private Locale locale;

	private BigDecimal defaultSearchSensibility = BigDecimal.valueOf(70);
	private static final String RESULTS_SPAN_STYLE = "padding:15px;text-decoration:bold;";

	/**
	 * Defines the data set.
	 *
	 * @param pContext the context
	 * @return the adaptation
	 * @since 1.0.0
	 */
	public abstract Adaptation defineDataSet(final UIAjaxContext pContext);

	/**
	 * Defines the query.
	 *
	 * @param pContext the context
	 * @return the string
	 * @since 1.0.0
	 */
	public abstract String defineQuery(final UIAjaxContext pContext);

	/**
	 * Defines the searched tables.
	 *
	 * @param pContext the context
	 * @return the array list
	 * @since 1.0.0
	 */
	public abstract List<SchemaNode> defineSearchedTables(final UIAjaxContext pContext);

	/**
	 * Defines the search sensibility.
	 *
	 * @param pContext the context
	 * @return the big decimal
	 * @since 1.0.0
	 */
	public abstract BigDecimal defineSearchSensibility(final UIAjaxContext pContext);

	@Override
	public void doAjaxResponse(final UIAjaxContext pContext) {
		this.context = pContext;
		this.locale = this.context.getLocale();
		Session session = this.context.getSession();

		String query = this.getQuery(this.context);
		if (query.equals("")) {
			this.displayNoResults();
			return;
		}

		BigDecimal searchSensibility = this.getSearchSensibility(this.context);

		Adaptation dataSet = null;
		List<SchemaNode> tableSelected = null;
		List<SearchResultBean> results;
		try {
			dataSet = this.getDataSet(this.context);
			tableSelected = this.getSearchedTables(this.context);
			SearchEngine searchEngine = new SearchEngine(dataSet, session);
			results = searchEngine.search(tableSelected, query, searchSensibility);
		} catch (OperationException ex) {
			this.displayException(ex);
			return;
		}

		this.displaySearchResults(results);
	}

	/**
	 * Gets the default search sensibility.
	 *
	 * @return the default search sensibility
	 * @since 1.0.0
	 */
	public BigDecimal getDefaultSearchSensibility() {
		return this.defaultSearchSensibility;
	}

	/**
	 * Sets the default search sensibility.
	 *
	 * @param defaultSearchSensibility the new default search sensibility
	 * @since 1.0.0
	 */
	public void setDefaultSearchSensibility(final BigDecimal defaultSearchSensibility) {
		this.defaultSearchSensibility = defaultSearchSensibility;
	}

	/**
	 * Display exception.
	 *
	 * @param pException the exception
	 * @since 1.0.0
	 */
	private void displayException(final OperationException pException) {
		this.context.add("<span style=\"" + RESULTS_SPAN_STYLE + "color:red;\">");
		this.context.add(Messages.get(this.getClass(), this.locale, "Error"));
		this.context.add(pException.getMessageForLocale(this.locale));
		this.context.add("</span>");
	}

	/**
	 * Display no results.
	 *
	 * @since 1.0.0
	 */
	private void displayNoResults() {
		this.context.add("<span style=\"" + RESULTS_SPAN_STYLE + "\">");
		this.context.add(Messages.get(this.getClass(), this.locale, "NoResultFound"));
		this.context.add("</span>");
	}

	/**
	 * Display search results.
	 *
	 * @param pResults the results
	 * @since 1.0.0
	 */
	private void displaySearchResults(final List<SearchResultBean> pResults) {
		if (pResults == null || pResults.isEmpty()) {
			this.displayNoResults();
			return;
		}

		this.insertResultTable(pResults);
	}

	/**
	 * Gets the data set.
	 *
	 * @param pContext the context
	 * @return the data set
	 * @throws OperationException the operation exception
	 * @since 1.0.0
	 */
	private Adaptation getDataSet(final UIAjaxContext pContext) throws OperationException {
		Adaptation dataSet = this.defineDataSet(pContext);
		if (dataSet == null) {
			OperationException exception = OperationException.createError(Messages.getInfo(this.getClass(), "AnErrorOccured"));
			CommonsLogger.getLogger().error(Messages.get(this.getClass(), Locale.US, "NoDataSetHasBeenDefineForTheSearch"));
			throw exception;
		}
		return dataSet;
	}

	/**
	 * Gets the query.
	 *
	 * @param pContext the context
	 * @return the query
	 * @since 1.0.0
	 */
	private String getQuery(final UIAjaxContext pContext) {
		String query = this.defineQuery(pContext);
		if (query == null) {
			query = "";
		}
		return query;
	}

	/**
	 * Gets the searched tables.
	 *
	 * @param pContext the context
	 * @return the searched tables
	 * @throws OperationException the operation exception
	 * @since 1.0.0
	 */
	private List<SchemaNode> getSearchedTables(final UIAjaxContext pContext) throws OperationException {
		List<SchemaNode> searchedTables = this.defineSearchedTables(pContext);
		if (searchedTables == null || searchedTables.isEmpty()) {
			OperationException exception = OperationException.createError(Messages.getInfo(this.getClass(), "AnErrorOccured"));
			CommonsLogger.getLogger().error(Messages.get(this.getClass(), Locale.US, "NoSearchedTableHasBeenDefineForTheSearch"));
			throw exception;
		}
		return searchedTables;
	}

	/**
	 * Gets the search sensibility.
	 *
	 * @param pContext the context
	 * @return the search sensibility
	 * @since 1.0.0
	 */
	private BigDecimal getSearchSensibility(final UIAjaxContext pContext) {
		BigDecimal sensibility = this.defineSearchSensibility(pContext);
		if (sensibility != null) {
			sensibility = this.defaultSearchSensibility;
		}
		return sensibility;
	}

	/**
	 * Inserts the result table.
	 *
	 * @param pResults the results
	 * @since 1.0.0
	 */
	private void insertResultTable(final List<SearchResultBean> pResults) {
		this.context.add("<table class=\"" + UICSSClasses.TABLE.ESSENTIAL + "\">");
		this.insertResultTableHeader();
		this.insertResultTableBody(pResults);
		this.context.add("</table>");
	}

	/**
	 * Inserts the result table body.
	 *
	 * @param pResults the results
	 * @since 1.0.0
	 */
	private void insertResultTableBody(final List<SearchResultBean> pResults) {
		this.context.add("<tbody>");
		for (SearchResultBean result : pResults) {
			this.insertResultTableRow(result);
		}
		this.context.add("</tbody>");
	}

	/**
	 * Inserts the result table header.
	 *
	 * @since 1.0.0
	 */
	private void insertResultTableHeader() {
		this.context.add("<thead>");
		this.context.add("<tr>");

		this.context.add(OPEN_TH);
		this.context.add(Messages.get(this.getClass(), this.locale, "Record"));
		this.context.add(CLOSE_TH);

		this.context.add(OPEN_TH);
		this.context.add(Messages.get(this.getClass(), this.locale, "Score"));
		this.context.add(CLOSE_TH);

		this.context.add(OPEN_TH);
		this.context.add(Messages.get(this.getClass(), this.locale, "Link"));
		this.context.add(CLOSE_TH);

		this.context.add("</tr>");
		this.context.add("</thead>");
	}

	/**
	 * Inserts the result table row.
	 *
	 * @param pResult the result
	 * @since 1.0.0
	 */
	private void insertResultTableRow(final SearchResultBean pResult) {
		Adaptation record = pResult.getRecord();
		UserMessage reason = pResult.getReason();

		this.context.add("<tr>");

		this.context.add(OPEN_TD);
		if (record != null) {
			this.context.add(record.getLabel(this.locale));
		}
		this.context.add(CLOSE_TD);

		this.context.add(OPEN_TD);
		if (reason != null) {
			this.context.add(reason.formatMessage(this.locale));
		}
		this.context.add(CLOSE_TD);

		this.context.add(OPEN_TD);
		if (record != null) {
			UIHttpManagerComponent uiHttpManagerComponent = this.context.createWebComponentForSubSession();
			uiHttpManagerComponent.selectInstanceOrOccurrence(record);
			UIButtonSpecJSAction buttonPreview = this.context.buildButtonPreview(uiHttpManagerComponent.getURIWithParameters());
			this.context.addButtonJavaScript(buttonPreview);
		}
		this.context.add(CLOSE_TD);

		this.context.add("<tr>");
	}
}
