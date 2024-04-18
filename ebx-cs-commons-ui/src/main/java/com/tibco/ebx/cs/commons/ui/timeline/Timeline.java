package com.tibco.ebx.cs.commons.ui.timeline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.orchestranetworks.ui.UIButtonSpecJSAction;
import com.orchestranetworks.ui.UIComponentWriter;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * The Class Timeline allows defining and drawing a timeline graphical
 * representation of items.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
@SuppressWarnings("javadoc")
public final class Timeline {
	public static final String JS_VARNAME_TIMELINE = "timeline";
	public static final String JS_VARNAME_OPTIONS = "options";
	public static final String JS_VARNAME_ITEMS = "items";
	public static final String JS_VARNAME_GROUPS = "groups";
	public static final String JS_FUNCNAME_MENU_ASOF = "asof";
	public static final String JS_FUNCNAME_MENU_FIT = "fit";
	public static final String JS_FUNCNAME_MENU_RESET = "reset";
	public static final String JS_FUNCNAME_MENU_ZOOM = "zoom";
	public static final String JS_FUNCNAME_MENU_MOVE = "move";
	public static final String JS_FUNCNAME_MENU_GOTODATE = "gotoDate";
	public static final String JS_FUNCNAME_INITIALIZE = "initialize";
	public static final String JS_FUNCNAME_UPDATETIMELINEINFO = "updateTimelineInfo";
	public static final String JS_FUNCNAME_GETTIMELINEINFO = "getTimelineInfo";
	public static final String JS_VARNAME_INITIAL_START_DATE = "initialStartDate";
	public static final String JS_VARNAME_INITIAL_END_DATE = "initialEndDate";
	public static final String JS_VARNAME_INITIAL_CUSTOM_TIME = "initialCustomTime";
	public static final String JS_VARNAME_MIN_TIMELINE_DATE = "minTimelineDate";
	public static final String JS_VARNAME_MAX_TIMELINE_DATE = "maxTimelineDate";
	public static final String JS_VARNAME_CURRENT_RECORD_PK = "currentPk";
	public static final String JS_FUNCNAME_ONMOVE = "onMove";
	public static final String JS_FUNCNAME_ONREMOVE = "onRemove";
	public static final String JS_FUNCNAME_ONMOVING = "onMoving";
	public static final String JS_FUNCNAME_ONUPDATE = "onUpdate";
	public static final String JS_FUNCNAME_ONADD = "onAdd";
	public static final String JS_VARNAME_TIMELINE_INFO_OUTPUT = "timelineInfoOutput";
	protected static final String CUSTOM_TIME_CALENDAR_PREFIX = "customTimeUser";

	/**
	 * Format the date to be included in JavaScript code. It uses the javascript
	 * library moment, allowing to better manage date manipulation on client side.
	 * Moment especially provides startOf and endOf methods to return the start /
	 * end of the day (or month, year ...) from an exact date. Loading the ressource
	 * moment.js file is required.
	 *
	 * @param date    the date to format.
	 * @param startOf a boolean setting if the returned moment date shall use the
	 *                .startOf(type) method.
	 * @param endOf   a boolean setting if the returned moment date shall use the
	 *                .endOf(type) method.
	 * @param type    a string setting the type of startOf / endOf to use. Values
	 *                are: minute, hour, day, month, year and can be get from
	 *                TimelineOptions.Snap class.
	 * @return the moment date: moment(215678943) or
	 *         moment(215678943).startOf('day') or moment(215678943).endOf('day').
	 * @since 1.0.0
	 */
	protected static String formatDate(final Date date, final boolean startOf, final boolean endOf, final String type) {
		if (date == null) {
			return null;
		}

		String momentDate = "moment(" + date.getTime() + ")";
		if (startOf && type != null) {
			momentDate += ".startOf('" + type + "')";
		}
		if (endOf && type != null) {
			momentDate += ".endOf('" + type + "')";
		}
		return momentDate;
	}

	private List<TimelineItem> items;
	private List<TimelineGroup> groups;
	private TimelineOptions options;
	private static final String CONTAINER_VAR_NAME = "container";
	private HashMap<String, String> customVariableDeclaration;

	/**
	 * Instantiates a new timeline.
	 *
	 * @since 1.0.0
	 */
	public Timeline() {
		this.items = new ArrayList<>();
		this.groups = new ArrayList<>();
		this.options = new TimelineOptions();
	}

	/**
	 * Instantiates a new timeline.
	 *
	 * @param items the list of items to include in the timeline.
	 * @since 1.0.0
	 */
	public Timeline(final List<TimelineItem> items) {
		if (items != null) {
			this.items = items;
		} else {
			this.items = new ArrayList<>();
		}

		this.groups = new ArrayList<>();
		this.options = new TimelineOptions();
		this.mergeItems();
	}

	/**
	 * Instantiates a new timeline.
	 *
	 * @param items   the list of items to include in the timeline.
	 * @param groups  the list of groups to include in the timeline.
	 * @param options the options to configure the timeline behavior.
	 * @since 1.0.0
	 */
	public Timeline(final List<TimelineItem> items, final List<TimelineGroup> groups, final TimelineOptions options) {
		if (items != null) {
			this.items = items;
		} else {
			this.items = new ArrayList<>();
		}
		if (groups != null) {
			this.groups = groups;
		} else {
			this.groups = new ArrayList<>();
		}
		if (options != null) {
			this.options = options;
		} else {
			this.options = new TimelineOptions();
		}
		this.mergeItems();
	}

	/**
	 * Instantiates a new timeline.
	 *
	 * @param items   the list of items to include in the timeline.
	 * @param options the options to configure the timeline behavior.
	 * @since 1.0.0
	 */
	public Timeline(final List<TimelineItem> items, final TimelineOptions options) {
		if (items != null) {
			this.items = items;
		} else {
			this.items = new ArrayList<>();
		}

		this.groups = new ArrayList<>();

		if (options != null) {
			this.options = options;
		} else {
			this.options = new TimelineOptions();
		}
		this.mergeItems();
	}

	/**
	 * Define the potentially required javascript variables.
	 *
	 * @param pDeclaration a map of Strings with the variable name as key and the
	 *                     variable value as value of the map.
	 * @since 1.0.0
	 */
	public void addVariableDeclaration(final HashMap<String, String> pDeclaration) {
		this.customVariableDeclaration = pDeclaration;
	}

	/**
	 * Draw the timeline. Insert it in the stream.
	 *
	 * @param pWriter        the writer
	 * @param pTimelineDivId the timeline div id
	 * @since 1.0.0
	 */
	public void draw(final UIComponentWriter pWriter, final String pTimelineDivId) {
		this.addDependencies(pWriter);
		this.addTimelineDiv(pWriter, pTimelineDivId);
		this.addVariableDeclaration(pWriter);
		this.addItems(pWriter);
		this.addGroups(pWriter);
		this.addOptions(pWriter);
		this.addTimelineImplementation(pWriter, pTimelineDivId);
		this.insertJS(pWriter);
	}

	/**
	 * Gets the list of groups.
	 *
	 * @return the list of TimelineGroup.
	 * @since 1.0.0
	 */
	public List<TimelineGroup> getGroups() {
		return this.groups;
	}

	/**
	 * Gets the list of items.
	 *
	 * @return the list of TimelineItem.
	 * @since 1.0.0
	 */
	public List<TimelineItem> getItems() {
		return this.items;
	}

	/**
	 * Gets the options.
	 *
	 * @return the instance of TimelineOptions.
	 * @since 1.0.0
	 */
	public TimelineOptions getOptions() {
		return this.options;
	}

	/**
	 * Sets the groups.
	 *
	 * @param pGroups the new list of TimelineGroup
	 * @since 1.0.0
	 */
	public void setGroups(final List<TimelineGroup> pGroups) {
		this.groups = pGroups;
	}

	/**
	 * Sets the items.
	 *
	 * @param pItems the new list of TimelineItem.
	 * @since 1.0.0
	 */
	public void setItems(final List<TimelineItem> pItems) {
		this.items = pItems;
		this.mergeItems();
	}

	/**
	 * Sets the options.
	 *
	 * @param pOptions the new instance of TimelineOptions.
	 * @since 1.0.0
	 */
	public void setOptions(final TimelineOptions pOptions) {
		this.options = pOptions;
	}

	/**
	 * Adds the dependencies of the timeline from the resources.
	 * <ul>
	 * <li>vis.js</li>
	 * <li>vis.css</li>
	 * <li>moment.js</li>
	 * <li>moment-range.js</li>
	 * </ul>
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addDependencies(final UIComponentWriter pWriter) {
		Presales_UIUtils.addCssLink(pWriter, "vis.css", CommonsConstants.MODULE_NAME);
	}

	/**
	 * Adds the groups to the stream.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addGroups(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + Timeline.JS_VARNAME_GROUPS + " = new vis.DataSet();");
		if (this.groups != null && !this.groups.isEmpty()) {
			pWriter.addJS_cr();
			pWriter.addJS_cr(Timeline.JS_VARNAME_GROUPS + ".add([");
			pWriter.addJS_cr();
			int nbGroups = this.groups.size();
			for (int i = 0; i < nbGroups; i++) {
				TimelineGroup group = this.groups.get(i);
				pWriter.addJS("    " + group.getJsObject());
				if (i + 1 < nbGroups) {
					pWriter.addJS_cr(",");
				}
				pWriter.addJS_cr();
			}
			pWriter.addJS_cr();
			pWriter.addJS_cr("]);");
		}
		pWriter.addJS_cr();
	}

	/**
	 * Adds the items to the stream.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addItems(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + Timeline.JS_VARNAME_ITEMS + " = new vis.DataSet();");
		if (this.items != null && !this.items.isEmpty()) {
			pWriter.addJS_cr();
			pWriter.addJS_cr(Timeline.JS_VARNAME_ITEMS + ".add([");
			pWriter.addJS_cr();
			int nbItems = this.items.size();
			for (int i = 0; i < nbItems; i++) {
				TimelineItem item = this.items.get(i);
				pWriter.addJS("    " + item.getJsObject());
				if (i + 1 < nbItems) {
					pWriter.addJS_cr(",");
				}
				pWriter.addJS_cr();
			}
			pWriter.addJS_cr();
			pWriter.addJS_cr("]);");
		}
		pWriter.addJS_cr();
	}

	/**
	 * Adds the menu custom date.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addMenuCustomDate(final UIComponentWriter pWriter) {
		pWriter.add("<div style=\"margin:10px 0;float:left;\">");

		UIButtonSpecJSAction asof = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.AsOf"),
				Timeline.JS_FUNCNAME_MENU_ASOF + "();");
		pWriter.addButtonJavaScript(asof);

		Calendar customTime = Calendar.getInstance();

		Date proposedCustomTime = this.options.getProposedCustomTime();
		if (proposedCustomTime != null) {
			customTime.setTime(proposedCustomTime);
		}
		Presales_UIUtils.addEBXDateComponent(Timeline.CUSTOM_TIME_CALENDAR_PREFIX, pWriter, customTime);

		pWriter.add("</div>");

	}

	/**
	 * Adds the menu navigation.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addMenuNavigation(final UIComponentWriter pWriter) {
		pWriter.add("<div style=\"margin:10px 0;float:right;\">");

		UIButtonSpecJSAction fit = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.Fit"),
				Timeline.JS_FUNCNAME_MENU_FIT + "();");
		pWriter.addButtonJavaScript(fit);

		UIButtonSpecJSAction reset = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.Reset"),
				Timeline.JS_FUNCNAME_MENU_RESET + "();");
		pWriter.addButtonJavaScript(reset);

		UIButtonSpecJSAction zoomIn = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.ZoomIn"),
				Timeline.JS_FUNCNAME_MENU_ZOOM + "(-" + this.options.getZoomStep() + ");");
		pWriter.addButtonJavaScript(zoomIn);

		UIButtonSpecJSAction zoomOut = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.ZoomOut"),
				Timeline.JS_FUNCNAME_MENU_ZOOM + "(" + this.options.getZoomStep() + ");");
		pWriter.addButtonJavaScript(zoomOut);

		UIButtonSpecJSAction moveLeft = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.MoveLeft"),
				Timeline.JS_FUNCNAME_MENU_MOVE + "(" + this.options.getMoveStep() + ");");
		pWriter.addButtonJavaScript(moveLeft);

		UIButtonSpecJSAction moveRight = new UIButtonSpecJSAction(Messages.getInfo(this.getClass(), "menu.MoveRight"),
				Timeline.JS_FUNCNAME_MENU_MOVE + "(-" + this.options.getMoveStep() + ");");
		pWriter.addButtonJavaScript(moveRight);

		pWriter.add("</div>");

	}

	/**
	 * Adds the options to the stream.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addOptions(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS("var " + Timeline.JS_VARNAME_OPTIONS + " = ");
		if (this.options != null) {
			pWriter.addJS(this.options.getJsObject());
		} else {
			pWriter.addJS("{}");
		}
		pWriter.addJS_cr(";");
	}

	/**
	 * Adds the timeline div.
	 *
	 * @param pWriter        the writer
	 * @param pTimelineDivId the timeline div id
	 * @since 1.0.0
	 */
	private void addTimelineDiv(final UIComponentWriter pWriter, final String pTimelineDivId) {
		pWriter.add("<div style=\"margin:15px;\">");

		boolean menuCustomDate = this.options.getMenuCustomDate();
		if (menuCustomDate) {
			this.addMenuCustomDate(pWriter);
		}
		boolean menuNav = this.options.getMenuNavigation();
		if (menuNav) {
			this.addMenuNavigation(pWriter);
		}

		if (menuCustomDate || menuNav) {
			pWriter.add("<div style=\"clear:both;\"></div>");
		}

		pWriter.add("<div id=\"" + pTimelineDivId + "\"></div>");

		pWriter.add("</div>");
	}

	/**
	 * Adds the timeline implementation to the stream.
	 *
	 * @param pWriter        the writer
	 * @param pTimelineDivId the timeline div id
	 * @since 1.0.0
	 */
	private void addTimelineImplementation(final UIComponentWriter pWriter, final String pTimelineDivId) {

		pWriter.addJS_cr();
		pWriter.addJS_cr(
				"var " + Timeline.CONTAINER_VAR_NAME + " = document.getElementById('" + pTimelineDivId + "');");
		pWriter.addJS_cr();
		pWriter.addJS_cr("var " + Timeline.JS_VARNAME_TIMELINE + " = new vis.Timeline(" + Timeline.CONTAINER_VAR_NAME
				+ ", " + Timeline.JS_VARNAME_ITEMS + ", " + Timeline.JS_VARNAME_OPTIONS + ");");
		pWriter.addJS_cr();
		if (this.groups != null) {
			pWriter.addJS_cr(Timeline.JS_VARNAME_TIMELINE + ".setGroups(" + Timeline.JS_VARNAME_GROUPS + ");");
		}
		String currentTime = Timeline.formatDate(new Date(), false, false, this.options.getSnap());
		if (this.options.isSnapToStartAndEnd()) {
			currentTime = Timeline.formatDate(new Date(), true, false, this.options.getSnap());
		}
		pWriter.addJS_cr(Timeline.JS_VARNAME_TIMELINE + ".setCurrentTime(" + currentTime + ");");
		pWriter.addJS_cr();
		pWriter.addJS_cr(Timeline.JS_FUNCNAME_INITIALIZE + "();");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the variable declaration.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addVariableDeclaration(final UIComponentWriter pWriter) {
		TimelineJS.insertVariableDeclaration(pWriter, this.options, this.customVariableDeclaration);
	}

	/**
	 * Insert the timeline JavaScript functions.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void insertJS(final UIComponentWriter pWriter) {

		TimelineJS.insertFunctionOnAdd(pWriter, "");
		TimelineJS.insertFunctionOnMove(pWriter, "");
		TimelineJS.insertFunctionOnMoving(pWriter, "");
		TimelineJS.insertFunctionOnRemove(pWriter, "");
		TimelineJS.insertFunctionOnUpdate(pWriter, "");

		TimelineJS.insertFunctionGotoDate(pWriter, this.options.isSnapToStartAndEnd(), this.options.getSnap());
		TimelineJS.insertFunctionAsof(pWriter);

		TimelineJS.insertFunctionMove(pWriter);
		TimelineJS.insertFunctionZoom(pWriter);
		TimelineJS.insertFunctionFit(pWriter);
		TimelineJS.insertFunctionReset(pWriter);

		TimelineJS.insertFunctionInitialize(pWriter, null);

		if (this.options.getTimelineInfo() != null) {
			TimelineJS.insertVarTimelineInfoOutput(pWriter, null);
			TimelineJS.insertFunctionGetTimelineInfo(pWriter);
			TimelineJS.insertFunctionUpdateTimelineInfo(pWriter, this.options.getTimelineInfo());
		}
	}

	/**
	 * Merge the items of the timeline when they are equal, ie same group, same
	 * value/content and an overlapping date range. Merging the items is an option
	 * defined in the instance of TimelineOptions.
	 *
	 * @see TimelineItem#equalItems(TimelineItem)
	 * @see TimelineOptions#getMergeItem()
	 * @see TimelineOptions#setMergeItem(boolean)
	 * @since 1.0.0
	 */
	private void mergeItems() {
		if (this.options == null || !this.options.getMergeItem()) {
			return;
		}

		List<TimelineItem> mergedItems = new ArrayList<>();
		List<Integer> alreadyMerged = new ArrayList<>();

		int nbItems = this.items.size();
		for (int i = 0; i < nbItems; i++) {
			TimelineItem currentItem = this.items.get(i);
			if (alreadyMerged.contains(i)) {
				continue;
			}

			for (int j = i + 1; j < nbItems; j++) {
				TimelineItem comparedItem = this.items.get(j);

				if (currentItem.equalItems(comparedItem)) {
					Date currentStartDate = currentItem.getStart();
					Date comparedStartDate = comparedItem.getStart();
					if (currentStartDate != null && currentStartDate.after(comparedStartDate)) {
						currentItem.setStart(comparedStartDate);
					}

					Date currentEndDate = currentItem.getEnd();
					Date comparedEndDate = comparedItem.getEnd();
					if (currentEndDate != null && currentEndDate.before(comparedEndDate)) {
						currentItem.setEnd(comparedEndDate);
					}
					currentItem.setEndless(comparedItem.isEndless());
					alreadyMerged.add(j);
				}
			}
			mergedItems.add(currentItem);
		}
		this.items = mergedItems;
	}
}
