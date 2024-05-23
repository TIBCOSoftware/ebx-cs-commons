package com.tibco.ebx.cs.commons.ui.userservice.historytimeline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.CfContentType;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIComponentWriter;
import com.orchestranetworks.ui.UIHttpManagerComponent;
import com.orchestranetworks.ui.UIHttpManagerComponentBridge;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.ui.timeline.Timeline;
import com.tibco.ebx.cs.commons.ui.timeline.TimelineGroup;
import com.tibco.ebx.cs.commons.ui.timeline.TimelineItem;
import com.tibco.ebx.cs.commons.ui.timeline.TimelineOptions;

/**
 * <strong>Using non public API</strong><br>
 * <br>
 * The Class HistoryTimeline.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class HistoryTimeline {
	private static final String TECHNICAL_FIELDS_PATH = "/ebx-technical";
	private static final String OPERATION_FIELDS_PATH = "/ebx-operationCode";
	private static final String TIMESTAMP_FIELD_PATH = "/ebx-technical/timestamp";
	private static final String OPERATION_FIELD_PATH = "/ebx-technical/op";
	private static final String USER_FIELD_PATH = "/ebx-technical/user";

	/**
	 * <strong>Using non public API</strong><br>
	 * <br>
	 * Sets the history content type.
	 *
	 * @param pManagerComponent the new history content type
	 * @since 1.0.0
	 */
	private static void setHistoryContentType(final UIHttpManagerComponent pManagerComponent) {
		// USING_NON_PUBLIC_API

		UIHttpManagerComponentBridge.setContentType(CfContentType.HISTORY, pManagerComponent);
	}

	private final UIComponentWriter writer;
	private final Locale locale;
	private final Adaptation record;
	private AdaptationTable historyTable;
	private List<Adaptation> historyItems;
	private TimelineOptions options;
	private Timeline timeline;
	private List<TimelineItem> timelineItems;
	private List<TimelineGroup> timelineGroups;
	private Date initialDate = new Date();

	/**
	 * Instantiates a new history timeline.
	 *
	 * @param record the record
	 * @param writer the writer
	 * @since 1.0.0
	 */
	public HistoryTimeline(final Adaptation record, final UIComponentWriter writer) {
		this.record = record;
		this.writer = writer;
		this.locale = writer.getLocale();
		this.initializeTimeline();
	}

	/**
	 * Instantiates a new history timeline.
	 *
	 * @param record  the record
	 * @param options the options
	 * @param writer  the writer
	 * @see com.tibco.ebx.cs.commons.ui.timeline.TimelineOptions
	 * @since 1.0.0
	 */
	public HistoryTimeline(final Adaptation record, final UIComponentWriter writer, final TimelineOptions options) {
		this.record = record;
		this.writer = writer;
		this.locale = writer.getLocale();
		this.options = options;
		this.initializeTimeline();
	}

	/**
	 * Adds the history timeline.
	 *
	 * @since 1.0.0
	 */
	public void addHistoryTimeline() {
		String timelineDivId = UUID.randomUUID().toString();
		this.timeline.draw(this.writer, timelineDivId);
	}

	/**
	 * Adds the pop up link to history item.
	 *
	 * @param pWriter the writer
	 * @param pRecord the record
	 * @return the string
	 * @since 1.0.0
	 */
	private static String addPopUpLinkToHistoryItem(final UIComponentWriter pWriter, final Adaptation pRecord) {
		String popUpLink = "";

		if (pRecord != null) {
			UIHttpManagerComponent managerComponent = pWriter.createWebComponentForSubSession();
			managerComponent.selectInstanceOrOccurrence(pRecord);
			HistoryTimeline.setHistoryContentType(managerComponent);
			UIButtonSpecJSAction buttonSpec = pWriter.buildButtonPreview(managerComponent.getURIWithParameters());
			String jsCommand = buttonSpec.getJavaScriptCommand().replace("\"", "'");

			popUpLink += "<a href=\"#\" onclick=\"" + jsCommand + "\">";
			popUpLink += "<span class=\"ebx_Open\"><span class=\"ebx_Icon\"></span></span>";
			popUpLink += "</a>";

		}
		return popUpLink;
	}

	/**
	 * Builds the history item predicate.
	 *
	 * @param pCurrentRecord the current record
	 * @return the string
	 * @since 1.0.0
	 */
	private static String buildHistoryItemPredicate(final Adaptation pCurrentRecord) {
		PrimaryKey primaryKey = pCurrentRecord.getOccurrencePrimaryKey();
		SchemaNode schemaNode = pCurrentRecord.getSchemaNode().getTableNode();
		SchemaNode[] pkNodes = schemaNode.getTablePrimaryKeyNodes();
		Object[] pkValues = schemaNode.getTablePrimaryKeyValues(primaryKey);
		int nbPkNodes = pkNodes.length;

		StringBuilder predicate = new StringBuilder("");
		for (int i = 0; i < nbPkNodes; i++) {
			String node = Path.SELF.add(pkNodes[i].getPathInAdaptation()).format();
			String value = (String) pkValues[i];

			predicate.append(node + "='" + value + "'");
			if (i + 1 < nbPkNodes) {
				predicate.append(" and ");
			}
		}
		return predicate.toString();
	}

	/**
	 * Creates the timeline groups.
	 *
	 * @param pSchemaNode the schema node
	 * @since 1.0.0
	 */
	private void createTimelineGroups(final SchemaNode pSchemaNode) {
		this.timelineGroups = new ArrayList<>();

		Integer order = 100;
		boolean noAdd = true;

		String transactionGroupId = HistoryTimelineTransaction.TRANSACTION_GROUP_ID;
		String transactionGroupName = Messages.get(this.getClass(), this.locale, "TransactionsGroupName");

		TimelineGroup transactionGroup = new TimelineGroup(transactionGroupId, transactionGroupName, noAdd, 0);
		this.timelineGroups.add(transactionGroup);

		for (SchemaNode pNode : pSchemaNode.getNodeChildren()) {
			String group = pNode.getPathInAdaptation().format();

			if (group.contains(HistoryTimeline.OPERATION_FIELDS_PATH) || group.contains(HistoryTimeline.TECHNICAL_FIELDS_PATH)) {
				continue;
			}

			if (pNode.isTerminalValue()) {
				String groupName = pNode.getLabel(this.locale);
				TimelineGroup nodeGroup = new TimelineGroup(group, groupName, noAdd, order);
				nodeGroup.setSchemaNode(true);
				this.timelineGroups.add(nodeGroup);
			} else {
				this.createTimelineGroups(pNode);
			}
		}
	}

	/**
	 * Creates the timeline items.
	 *
	 * @since 1.0.0
	 */
	private void createTimelineItems() {
		List<HistoryTimelineTransaction> transactions = this.createTransaction();
		if (transactions == null) {
			return;
		}

		this.timelineItems = new ArrayList<>();

		for (HistoryTimelineTransaction transaction : transactions) {
			Date startDate = transaction.getStartDate();
			if (this.initialDate.after(startDate)) {
				this.initialDate = startDate;
			}
			Date endDate = transaction.getEndDate();
			String operation = transaction.getOperation();
			boolean isEndless = false;
			if (endDate == null) {
				isEndless = true;
				endDate = this.options.getMax();
			}

			this.createTransactionTimelineItems(transaction);

			List<HistoryTimelineTransactionFieldValue> values = transaction.getFieldValues();
			for (HistoryTimelineTransactionFieldValue value : values) {
				String group = value.getGroup();
				String content = value.getValue();
				String style = value.getStyle();

				HistoryTimelineTransactionValueItem transactionValueItem = new HistoryTimelineTransactionValueItem(group, startDate, endDate, content, operation);
				transactionValueItem.setEndless(isEndless);
				transactionValueItem.setPk(transaction.getTransactionId());
				transactionValueItem.setStyle(style);
				this.timelineItems.add(transactionValueItem);
			}

		}
	}

	/**
	 * Creates the timeline transaction bg item.
	 *
	 * @param pTransaction the transaction
	 * @return the timeline item
	 * @since 1.0.0
	 */
	private TimelineItem createTimelineTransactionBgItem(final HistoryTimelineTransaction pTransaction) {
		int index = pTransaction.getIndex();
		Date startDate = pTransaction.getStartDate();
		Date endDate = pTransaction.getEndDate();
		boolean even = HistoryTimeline.isEven(index);
		boolean isEndless = false;
		if (endDate == null) {
			isEndless = true;
			endDate = this.options.getMax();
		}

		HistoryTimelineTransactionItem item = new HistoryTimelineTransactionItem(null, startDate, endDate, index, index, null);
		item.setEndless(isEndless);
		item.setType(TimelineItem.Type.BACKGROUND);

		if (even) {
			item.setStyle(item.getTransactionEvenStyle());
		} else {
			item.setStyle(item.getTransactionOddStyle());
		}
		return item;
	}

	/**
	 * Creates the timeline transaction item.
	 *
	 * @param pTransaction the transaction
	 * @return the timeline item
	 * @since 1.0.0
	 */
	private TimelineItem createTimelineTransactionItem(final HistoryTimelineTransaction pTransaction) {
		int index = pTransaction.getIndex();
		Date startDate = pTransaction.getStartDate();
		Date endDate = pTransaction.getEndDate();
		String user = pTransaction.getUser();
		String operation = pTransaction.getOperation();
		boolean even = HistoryTimeline.isEven(index);
		boolean isEndless = false;
		if (endDate == null) {
			isEndless = true;
			endDate = this.options.getMax();
		}

		String content = HistoryTimeline.addPopUpLinkToHistoryItem(this.writer, pTransaction.getRecord()).replace("'", "\\'");
		content += " ";

		if (operation != null && operation.equals(HistoryTimelineTransaction.OperationType.creation)) {
			content += Messages.get(this.getClass(), this.locale, "Creation");
		} else if (operation != null && operation.equals(HistoryTimelineTransaction.OperationType.modification)) {
			content += Messages.get(this.getClass(), this.locale, "Modification");
		} else if (operation != null && operation.equals(HistoryTimelineTransaction.OperationType.deletion)) {
			content += Messages.get(this.getClass(), this.locale, "Deletion");
		} else {
			content += Messages.get(this.getClass(), this.locale, "Transaction");
		}

		content += " " + Messages.get(this.getClass(), this.locale, "by{User}At{Time}", user, startDate);

		String groupId = HistoryTimelineTransaction.TRANSACTION_GROUP_ID;

		HistoryTimelineTransactionItem item = new HistoryTimelineTransactionItem(groupId, startDate, endDate, index, index, content);
		item.setEndless(isEndless);
		item.setVersion(true);

		if (even) {
			item.setStyle(item.getTransactionEvenStyle());
		} else {
			item.setStyle(item.getTransactionOddStyle());
		}
		return item;
	}

	/**
	 * Creates the transaction.
	 *
	 * @return the list
	 * @since 1.0.0
	 */
	private List<HistoryTimelineTransaction> createTransaction() {
		if (this.historyItems == null) {
			return null;
		}

		List<HistoryTimelineTransaction> transactions = new ArrayList<>();
		if (this.historyItems.isEmpty()) {
			return transactions;
		}

		String recordPK = this.record.getOccurrencePrimaryKey().format();
		if (this.historyTable == null) {
			this.historyTable = HistoryTimeline.getHistoryTable(this.record);
		}
		SchemaNode schemaNode = this.historyTable.getTableOccurrenceRootNode();

		int nbHistoryItems = this.historyItems.size();

		for (int i = 0; i < nbHistoryItems; i++) {
			Adaptation historyItem = this.historyItems.get(i);

			Date startDate = historyItem.getDate(Path.SELF.add(HistoryTimeline.TIMESTAMP_FIELD_PATH));
			Date endDate = null;
			if (i + 1 < nbHistoryItems) {
				endDate = this.historyItems.get(i + 1).getDate(Path.SELF.add(HistoryTimeline.TIMESTAMP_FIELD_PATH));
			}
			String operation = historyItem.getString(Path.SELF.add(HistoryTimeline.OPERATION_FIELD_PATH));
			String user = historyItem.getString(Path.SELF.add(HistoryTimeline.USER_FIELD_PATH));
			String transactionId = historyItem.getOccurrencePrimaryKey().format();

			List<HistoryTimelineTransactionFieldValue> values = this.getHistoryItemFieldValues(historyItem, schemaNode);

			HistoryTimelineTransaction transaction = new HistoryTimelineTransaction(historyItem, startDate, endDate);
			transaction.setIndex(i + 1);
			transaction.setTransactionId(transactionId);
			transaction.setUser(user);
			transaction.setRecordPK(recordPK);
			transaction.setFieldValues(values);
			transaction.setOperation(operation);

			transactions.add(transaction);
		}
		return transactions;
	}

	/**
	 * Creates the transaction timeline items.
	 *
	 * @param pTransaction the transaction
	 * @since 1.0.0
	 */
	private void createTransactionTimelineItems(final HistoryTimelineTransaction pTransaction) {
		// transaction item background
		TimelineItem transactionBgItem = this.createTimelineTransactionBgItem(pTransaction);
		this.timelineItems.add(transactionBgItem);

		// transaction item
		TimelineItem transactionItem = this.createTimelineTransactionItem(pTransaction);
		this.timelineItems.add(transactionItem);
	}

	/**
	 * Define timeline options.
	 *
	 * @return the timeline options
	 * @since 1.0.0
	 */
	private static TimelineOptions defineTimelineOptions() {
		TimelineOptions timelineOptions = new TimelineOptions(null, null, false);
		timelineOptions.setMergeItem(true);
		timelineOptions.setSnapToStartAndEnd(false);
		timelineOptions.setMenuNavigation(true);
		timelineOptions.setSnap(TimelineOptions.Snap.EXACT);
		timelineOptions.setZoomMin(TimelineOptions.Zoom.MINUTE_10);
		timelineOptions.setZoomMax(TimelineOptions.Zoom.YEAR_1);

		return timelineOptions;
	}

	/**
	 * Gets the history item field values.
	 *
	 * @param pHistoryItem the history item
	 * @param pSchemaNode  the schema node
	 * @return the history item field values
	 * @since 1.0.0
	 */
	private List<HistoryTimelineTransactionFieldValue> getHistoryItemFieldValues(final Adaptation pHistoryItem, final SchemaNode pSchemaNode) {
		List<HistoryTimelineTransactionFieldValue> fieldValues = new ArrayList<>();

		String mainOperation = pHistoryItem.getString(Path.SELF.add(HistoryTimeline.OPERATION_FIELD_PATH));

		for (TimelineGroup timelineGroup : this.timelineGroups) {
			if (!timelineGroup.isSchemaNode()) {
				continue;
			}
			Path path = Path.ROOT.add(timelineGroup.getId());
			SchemaNode node = pSchemaNode.getNode(Path.SELF.add(path));
			String value = node.formatToXsString(pHistoryItem.get(path));
			Path operationPath = Path.ROOT.add(HistoryTimeline.OPERATION_FIELDS_PATH).add(path);
			String operation = pHistoryItem.getString(operationPath);
			if (operation != null) {
				operation = mainOperation;
			}
			HistoryTimelineTransactionFieldValue fieldValue = new HistoryTimelineTransactionFieldValue(path, value, operation);
			fieldValues.add(fieldValue);
		}

		return fieldValues;
	}

	/**
	 * Gets the history items.
	 *
	 * @param pCurrentRecord the current record
	 * @return the history items
	 * @since 1.0.0
	 */
	private List<Adaptation> getHistoryItems(final Adaptation pCurrentRecord) {
		if (pCurrentRecord == null) {
			return null;
		}

		this.historyTable = HistoryTimeline.getHistoryTable(pCurrentRecord);
		if (this.historyTable == null) {
			return null;
		}

		String predicate = HistoryTimeline.buildHistoryItemPredicate(pCurrentRecord);

		return this.historyTable.selectOccurrences(predicate);
	}

	/**
	 * Gets the history table.
	 *
	 * @param pCurrentRecord the current record
	 * @return the history table
	 * @since 1.0.0
	 */
	private static AdaptationTable getHistoryTable(final Adaptation pCurrentRecord) {
		AdaptationTable table = pCurrentRecord.getContainerTable();
		return table.getHistory();
	}

	/**
	 * Gets the timeline groups.
	 *
	 * @return the timeline groups
	 * @since 1.0.0
	 */
	private List<TimelineGroup> getTimelineGroups() {
		if (this.timelineGroups == null) {
			if (this.historyTable == null) {
				this.historyTable = HistoryTimeline.getHistoryTable(this.record);
			}
			SchemaNode schemaNode = this.historyTable.getTableOccurrenceRootNode();
			this.createTimelineGroups(schemaNode);
		}
		return this.timelineGroups;
	}

	/**
	 * Gets the timeline items.
	 *
	 * @return the timeline items
	 * @since 1.0.0
	 */
	private List<TimelineItem> getTimelineItems() {
		if (this.timelineItems == null) {
			this.createTimelineItems();
		}
		return this.timelineItems;
	}

	/**
	 * Initialize timeline.
	 *
	 * @since 1.0.0
	 */
	private void initializeTimeline() {
		this.historyItems = this.getHistoryItems(this.record);

		if (this.options == null) {
			this.options = HistoryTimeline.defineTimelineOptions();
		}

		List<TimelineGroup> groups = this.getTimelineGroups();

		List<TimelineItem> items = this.getTimelineItems();

		this.setStartAndEndDate();
		this.timeline = new Timeline(items, groups, this.options);

		HashMap<String, String> declaration = new HashMap<>();
		this.timeline.addVariableDeclaration(declaration);
	}

	/**
	 * Checks if is even.
	 *
	 * @param pIndex the index
	 * @return true, if is even
	 * @since 1.0.0
	 */
	private static boolean isEven(final int pIndex) {
		return (pIndex % 2 == 0);
	}

	/**
	 * Sets the start and end date.
	 *
	 * @since 1.0.0
	 */
	private void setStartAndEndDate() {
		long startDate = this.initialDate.getTime();
		long endDate = new Date().getTime();
		long interval = endDate - startDate;
		long offset = interval * 5 / 100;

		Calendar initialCal = Calendar.getInstance();
		initialCal.setTimeInMillis(startDate - offset);
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(endDate + offset);

		this.options.setStart(initialCal.getTime());
		this.options.setEnd(endCal.getTime());
		this.options.setProposedCustomTime(this.initialDate);
	}
}
