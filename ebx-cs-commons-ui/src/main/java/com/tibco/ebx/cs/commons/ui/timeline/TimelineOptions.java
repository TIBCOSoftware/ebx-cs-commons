package com.tibco.ebx.cs.commons.ui.timeline;

import java.util.Calendar;
import java.util.Date;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;

/**
 * The Class TimelineOptions.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class TimelineOptions {
	/**
	 * The Class Align.
	 *
	 * @since 1.0.0
	 */
	public static class Align {
		private Align() {
			super();
		}

		public static final String AUTO = "auto";
	}

	/**
	 * The Class Orientation.
	 *
	 * @since 1.0.0
	 */
	public static class Orientation {
		private Orientation() {
			super();
		}

		public static final String TOP = "top";
		public static final String BOTTOM = "bottom";
	}

	/**
	 * The Class Snap.
	 *
	 * @since 1.0.0
	 */
	public static class Snap {
		private Snap() {
			super();
		}

		public static final String EXACT = "exact";
		public static final String YEAR = "year";
		protected static final String YEAR_VALUE = "1000 * 60 * 60 * 24 * 365 * 1";
		public static final String MONTH = "month";
		protected static final String MONTH_VALUE = "1000 * 60 * 60 * 24 * 30 * 1";
		public static final String DAY = "day";
		protected static final String DAY_VALUE = "1000 * 60 * 60 * 24 * 1";
		public static final String HOUR = "hour";
		protected static final String HOUR_VALUE = "1000 * 60 * 60 * 1";
		public static final String MINUTE = "minute";
		protected static final String MINUTE_VALUE = "1000 * 60 * 1";
	}

	/**
	 * The Class Zoom.
	 *
	 * @since 1.0.0
	 */
	public static class Zoom {
		private Zoom() {
			super();
		}

		public static final String YEAR_10 = "1000 * 60 * 60 * 24 * 365 * 10";
		public static final String YEAR_5 = "1000 * 60 * 60 * 24 * 365 * 5";
		public static final String YEAR_4 = "1000 * 60 * 60 * 24 * 365 * 4";
		public static final String YEAR_3 = "1000 * 60 * 60 * 24 * 365 * 3";
		public static final String YEAR_2 = "1000 * 60 * 60 * 24 * 365 * 2";
		public static final String YEAR_1 = "1000 * 60 * 60 * 24 * 365 * 1";
		public static final String MONTH_10 = "1000 * 60 * 60 * 24 * 30 * 10";
		public static final String MONTH_9 = "1000 * 60 * 60 * 24 * 30 * 9";
		public static final String MONTH_6 = "1000 * 60 * 60 * 24 * 30 * 6";
		public static final String MONTH_5 = "1000 * 60 * 60 * 24 * 30 * 5";
		public static final String MONTH_4 = "1000 * 60 * 60 * 24 * 30 * 4";
		public static final String MONTH_3 = "1000 * 60 * 60 * 24 * 30 * 3";
		public static final String MONTH_2 = "1000 * 60 * 60 * 24 * 30 * 2";
		public static final String MONTH_1 = "1000 * 60 * 60 * 24 * 30 * 1";
		public static final String DAY_20 = "1000 * 60 * 60 * 24 * 20";
		public static final String DAY_14 = "1000 * 60 * 60 * 24 * 14";
		public static final String DAY_10 = "1000 * 60 * 60 * 24 * 10";
		public static final String DAY_7 = "1000 * 60 * 60 * 24 * 7";
		public static final String DAY_5 = "1000 * 60 * 60 * 24 * 5";
		public static final String DAY_1 = "1000 * 60 * 60 * 24 * 1";
		public static final String HOUR_12 = "1000 * 60 * 60 * 12";
		public static final String HOUR_6 = "1000 * 60 * 60 * 6";
		public static final String HOUR_3 = "1000 * 60 * 60 * 3";
		public static final String HOUR_2 = "1000 * 60 * 60 * 2";
		public static final String HOUR_1 = "1000 * 60 * 60 * 1";
		public static final String MINUTE_50 = "1000 * 60 * 50";
		public static final String MINUTE_45 = "1000 * 60 * 45";
		public static final String MINUTE_40 = "1000 * 60 * 40";
		public static final String MINUTE_30 = "1000 * 60 * 30";
		public static final String MINUTE_20 = "1000 * 60 * 20";
		public static final String MINUTE_15 = "1000 * 60 * 15";
		public static final String MINUTE_10 = "1000 * 60 * 10";
		public static final String MINUTE_5 = "1000 * 60 * 5";
		public static final String MINUTE_4 = "1000 * 60 * 4";
		public static final String MINUTE_3 = "1000 * 60 * 3";
		public static final String MINUTE_2 = "1000 * 60 * 2";
		public static final String MINUTE_1 = "1000 * 60 * 1";
	}

	protected static class JsObjectName {
		private JsObjectName() {
			super();
		}

		protected static class Align {
			private Align() {
				super();
			}

			protected static final String JS_NAME = "align";
		}

		protected static class ClickToUse {
			private ClickToUse() {
				super();
			}

			protected static final String JS_NAME = "clickToUse";
		}

		protected static class Editable {
			private Editable() {
				super();
			}

			protected static final String JS_NAME = "editable";
		}

		protected static class Editable_Add {
			private Editable_Add() {
				super();
			}

			protected static final String JS_NAME = "add";
		}

		protected static class Editable_Remove {
			private Editable_Remove() {
				super();
			}

			protected static final String JS_NAME = "remove";
		}

		protected static class Editable_UpdateGroup {
			private Editable_UpdateGroup() {
				super();
			}

			protected static final String JS_NAME = "updateGroup";
		}

		protected static class Editable_UpdateTime {
			private Editable_UpdateTime() {
				super();
			}

			protected static final String JS_NAME = "updateTime";
		}

		protected static class End {
			private End() {
				super();
			}

			protected static final String JS_NAME = "end";
		}

		protected static class GroupOrder {
			private GroupOrder() {
				super();
			}

			protected static final String JS_NAME = "groupOrder";
		}

		protected static class Max {
			private Max() {
				super();
			}

			protected static final String JS_NAME = "max";
		}

		protected static class Min {
			private Min() {
				super();
			}

			protected static final String JS_NAME = "min";
		}

		protected static class OnAdd {
			private OnAdd() {
				super();
			}

			protected static final String JS_NAME = "onAdd";
		}

		protected static class OnMove {
			private OnMove() {
				super();
			}

			protected static final String JS_NAME = "onMove";
		}

		protected static class OnMoving {
			private OnMoving() {
				super();
			}

			protected static final String JS_NAME = "onMoving";
		}

		protected static class OnRemove {
			private OnRemove() {
				super();
			}

			protected static final String JS_NAME = "onRemove";
		}

		protected static class OnUpdate {
			private OnUpdate() {
				super();
			}

			protected static final String JS_NAME = "onUpdate";
		}

		protected static class Orientation {
			private Orientation() {
				super();
			}

			protected static final String JS_NAME = "orientation";
		}

		protected static class Snap {
			private Snap() {
				super();
			}

			protected static final String JS_NAME = "snap";
		}

		protected static class Stack {
			private Stack() {
				super();
			}

			protected static final String JS_NAME = "stack";
		}

		protected static class Start {
			private Start() {
				super();
			}

			protected static final String JS_NAME = "start";
		}

		protected static class ZoomMax {
			private ZoomMax() {
				super();
			}

			protected static final String JS_NAME = "zoomMax";
		}

		protected static class ZoomMin {
			private ZoomMin() {
				super();
			}

			protected static final String JS_NAME = "zoomMin";
		}
	}

	private static final String ALIGN = Align.AUTO;
	private Date start;
	private Date end;
	private Date min;
	private Date max;
	private Date initialCustomTime;
	private Date proposedCustomTime;
	private String zoomMin = Zoom.DAY_20;
	private String zoomMax = Zoom.YEAR_5;
	private Double zoomStep = 0.2d;
	private Double moveStep = 0.2d;
	private String snap = Snap.DAY;
	private boolean snapToStartAndEnd = true;
	private boolean stack = false;
	private String orientation = Orientation.TOP;
	private boolean editable = false;
	private boolean clickToUse = false;
	private boolean menuNavigation = false;
	private boolean menuCustomDate = true;
	private Path timelineInfo;
	private boolean mergeItem = true;

	private Adaptation record;

	/**
	 * Instantiates a new timeline options.
	 *
	 * @since 1.0.0
	 */
	public TimelineOptions() {
	}

	/**
	 * Instantiates a new timeline options.
	 *
	 * @param start the start
	 * @param end   the end
	 * @since 1.0.0
	 */
	public TimelineOptions(final Date start, final Date end) {
		this.start = start;
		this.end = end;
	}

	/**
	 * Instantiates a new timeline options.
	 *
	 * @param start    the start
	 * @param end      the end
	 * @param editable the editable
	 * @since 1.0.0
	 */
	public TimelineOptions(final Date start, final Date end, final boolean editable) {
		this.start = start;
		this.end = end;
		this.editable = editable;
	}

	/**
	 * Gets the align.
	 *
	 * @return the align
	 * @since 1.0.0
	 */
	public String getAlign() {
		return TimelineOptions.ALIGN;
	}

	/**
	 * Gets the editable.
	 *
	 * @return the editable
	 * @since 1.0.0
	 */
	public boolean getEditable() {
		return this.editable;
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 * @since 1.0.0
	 */
	public Date getEnd() {
		if (this.end == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(this.getStart());
			cal.add(Calendar.MONTH, 6);
			return cal.getTime();
		} else {
			return this.end;
		}
	}

	/**
	 * Gets the initial custom time.
	 *
	 * @return the initial custom time
	 * @since 1.0.0
	 */
	public Date getInitialCustomTime() {
		return this.initialCustomTime;
	}

	/**
	 * Gets the max.
	 *
	 * @return the max
	 * @since 1.0.0
	 */
	public Date getMax() {
		if (this.max == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(2025, 11, 31);
			return cal.getTime();
		} else {
			return this.max;
		}
	}

	/**
	 * Gets the menu custom date.
	 *
	 * @return the menu custom date
	 * @since 1.0.0
	 */
	public boolean getMenuCustomDate() {
		return this.menuCustomDate;
	}

	/**
	 * Gets the menu navigation.
	 *
	 * @return the menu navigation
	 * @since 1.0.0
	 */
	public boolean getMenuNavigation() {
		return this.menuNavigation;
	}

	/**
	 * Gets the merge item.
	 *
	 * @return the merge item
	 * @since 1.0.0
	 */
	public boolean getMergeItem() {
		return this.mergeItem;
	}

	/**
	 * Gets the min.
	 *
	 * @return the min
	 * @since 1.0.0
	 */
	public Date getMin() {
		if (this.min == null) {
			Calendar cal = Calendar.getInstance();
			cal.set(2000, 0, 1);
			return cal.getTime();
		} else {
			return this.min;
		}
	}

	/**
	 * Gets the move step.
	 *
	 * @return the move step
	 * @since 1.0.0
	 */
	public Double getMoveStep() {
		return this.moveStep;
	}

	/**
	 * Gets the orientation.
	 *
	 * @return the orientation
	 * @since 1.0.0
	 */
	public String getOrientation() {
		return this.orientation;
	}

	/**
	 * Gets the proposed custom time.
	 *
	 * @return the proposed custom time
	 * @since 1.0.0
	 */
	public Date getProposedCustomTime() {
		return this.proposedCustomTime;
	}

	/**
	 * Gets the record.
	 *
	 * @return the record
	 * @since 1.0.0
	 */
	public Adaptation getRecord() {
		return this.record;
	}

	/**
	 * Gets the snap.
	 *
	 * @return the snap
	 * @since 1.0.0
	 */
	public String getSnap() {
		return this.snap;
	}

	/**
	 * Gets the stack.
	 *
	 * @return the stack
	 * @since 1.0.0
	 */
	public boolean getStack() {
		return this.stack;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 * @since 1.0.0
	 */
	public Date getStart() {
		if (this.start == null) {
			return new Date();
		} else {
			return this.start;
		}
	}

	/**
	 * Gets the timeline info.
	 *
	 * @return the timeline info
	 * @since 1.0.0
	 */
	public Path getTimelineInfo() {
		return this.timelineInfo;
	}

	/**
	 * Gets the zoom max.
	 *
	 * @return the zoom max
	 * @since 1.0.0
	 */
	public String getZoomMax() {
		return this.zoomMax;
	}

	/**
	 * Gets the zoom min.
	 *
	 * @return the zoom min
	 * @since 1.0.0
	 */
	public String getZoomMin() {
		return this.zoomMin;
	}

	/**
	 * Gets the zoom step.
	 *
	 * @return the zoom step
	 * @since 1.0.0
	 */
	public Double getZoomStep() {
		return this.zoomStep;
	}

	/**
	 * Checks if is click to use.
	 *
	 * @return true, if is click to use
	 * @since 1.0.0
	 */
	public boolean isClickToUse() {
		return this.clickToUse;
	}

	/**
	 * Checks if is snap to start and end.
	 *
	 * @return true, if is snap to start and end
	 * @since 1.0.0
	 */
	public boolean isSnapToStartAndEnd() {
		return this.snapToStartAndEnd;
	}

	/**
	 * Sets the click to use.
	 *
	 * @param clickToUse the new click to use
	 * @since 1.0.0
	 */
	public void setClickToUse(final boolean clickToUse) {
		this.clickToUse = clickToUse;
	}

	/**
	 * Sets the editable.
	 *
	 * @param editable the new editable
	 * @since 1.0.0
	 */
	public void setEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * Sets the end.
	 *
	 * @param end the new end
	 * @since 1.0.0
	 */
	public void setEnd(final Date end) {
		this.end = end;
	}

	/**
	 * Sets the initial custom time.
	 *
	 * @param initialCustomTime the new initial custom time
	 * @since 1.0.0
	 */
	public void setInitialCustomTime(final Date initialCustomTime) {
		this.initialCustomTime = initialCustomTime;
	}

	/**
	 * Sets the max.
	 *
	 * @param max the new max
	 * @since 1.0.0
	 */
	public void setMax(final Date max) {
		this.max = max;
	}

	/**
	 * Sets the menu custom date.
	 *
	 * @param menuCustomDate the new menu custom date
	 * @since 1.0.0
	 */
	public void setMenuCustomDate(final boolean menuCustomDate) {
		this.menuCustomDate = menuCustomDate;
	}

	/**
	 * Sets the menu navigation.
	 *
	 * @param menuNavigation the new menu navigation
	 * @since 1.0.0
	 */
	public void setMenuNavigation(final boolean menuNavigation) {
		this.menuNavigation = menuNavigation;
	}

	/**
	 * Sets the merge item.
	 *
	 * @param mergeItem the new merge item
	 * @since 1.0.0
	 */
	public void setMergeItem(final boolean mergeItem) {
		this.mergeItem = mergeItem;
	}

	/**
	 * Sets the min.
	 *
	 * @param min the new min
	 * @since 1.0.0
	 */
	public void setMin(final Date min) {
		this.min = min;
	}

	/**
	 * Sets the move step.
	 *
	 * @param moveStep the new move step
	 * @since 1.0.0
	 */
	public void setMoveStep(final Double moveStep) {
		this.moveStep = moveStep;
	}

	/**
	 * Sets the orientation.
	 *
	 * @param orientation the new orientation
	 * @since 1.0.0
	 */
	public void setOrientation(final String orientation) {
		this.orientation = orientation;
	}

	/**
	 * Sets the proposed custom time.
	 *
	 * @param proposedCustomTime the new proposed custom time
	 * @since 1.0.0
	 */
	public void setProposedCustomTime(final Date proposedCustomTime) {
		this.proposedCustomTime = proposedCustomTime;
	}

	/**
	 * Sets the record.
	 *
	 * @param record the new record
	 * @since 1.0.0
	 */
	public void setRecord(final Adaptation record) {
		this.record = record;
	}

	/**
	 * Sets the snap.
	 *
	 * @param snap the new snap
	 * @since 1.0.0
	 */
	public void setSnap(final String snap) {
		this.snap = snap;
	}

	/**
	 * Sets the snap to start and end.
	 *
	 * @param snapToStartAndEnd the new snap to start and end
	 * @since 1.0.0
	 */
	public void setSnapToStartAndEnd(final boolean snapToStartAndEnd) {
		this.snapToStartAndEnd = snapToStartAndEnd;
	}

	/**
	 * Sets the stack.
	 *
	 * @param stack the new stack
	 * @since 1.0.0
	 */
	public void setStack(final boolean stack) {
		this.stack = stack;
	}

	/**
	 * Sets the start.
	 *
	 * @param start the new start
	 * @since 1.0.0
	 */
	public void setStart(final Date start) {
		this.start = start;
	}

	/**
	 * Sets the timeline info.
	 *
	 * @param timelineInfo the new timeline info
	 * @since 1.0.0
	 */
	public void setTimelineInfo(final Path timelineInfo) {
		this.timelineInfo = timelineInfo;
	}

	/**
	 * Sets the zoom max.
	 *
	 * @param zoomMax the new zoom max
	 * @since 1.0.0
	 */
	public void setZoomMax(final String zoomMax) {
		this.zoomMax = zoomMax;
	}

	/**
	 * Sets the zoom min.
	 *
	 * @param zoomMin the new zoom min
	 * @since 1.0.0
	 */
	public void setZoomMin(final String zoomMin) {
		this.zoomMin = zoomMin;
	}

	/**
	 * Sets the zoom step.
	 *
	 * @param zoomStep the new zoom step
	 * @since 1.0.0
	 */
	public void setZoomStep(final Double zoomStep) {
		this.zoomStep = zoomStep;
	}

	/**
	 * Adds the snap function.
	 *
	 * @return the string
	 * @since 1.0.0
	 */
	private String addSnapFunction() {
		String type = this.getSnap();
		String snapping = null;

		if (type != null) {
			if (type.equals(Snap.YEAR)) {
				snapping = Snap.YEAR_VALUE;
			} else if (type.equals(Snap.MONTH)) {
				snapping = Snap.MONTH_VALUE;
			} else if (type.equals(Snap.DAY)) {
				snapping = Snap.DAY_VALUE;
			} else if (type.equals(Snap.HOUR)) {
				snapping = Snap.HOUR_VALUE;
			} else if (type.equals(Snap.MINUTE)) {
				snapping = Snap.MINUTE_VALUE;
			}
		}

		String function = "function (date, scale, step) {";
		if (snapping != null) {
			function += "var newDate = moment(date);";
			function += "var offset = moment(date).utcOffset();";
			function += "var snaping = " + snapping + ";";
			function += "var myDate = moment(Math.round(newDate / snaping) * snaping);";
			function += "return myDate.subtract(offset,'m');";
		} else {
			function += "return date;";
		}
		function += "}";
		return function;
	}

	/**
	 * Builds the js object content.
	 *
	 * @return the string
	 * @since 1.0.0
	 */
	protected String buildJsObjectContent() {
		String jsObjectContent = "";

		jsObjectContent += JsObjectName.Start.JS_NAME + ": " + Timeline.JS_VARNAME_INITIAL_START_DATE + ", ";
		jsObjectContent += JsObjectName.End.JS_NAME + ": " + Timeline.JS_VARNAME_INITIAL_END_DATE + ", ";
		jsObjectContent += JsObjectName.Min.JS_NAME + ": " + Timeline.JS_VARNAME_MIN_TIMELINE_DATE + ", ";
		jsObjectContent += JsObjectName.Max.JS_NAME + ": " + Timeline.JS_VARNAME_MAX_TIMELINE_DATE + ", ";

		if (this.getZoomMin() != null) {
			jsObjectContent += JsObjectName.ZoomMin.JS_NAME + ": " + this.getZoomMin() + ", ";
		}
		if (this.getZoomMax() != null) {
			jsObjectContent += JsObjectName.ZoomMax.JS_NAME + ": " + this.getZoomMax() + ", ";
		}
		if (this.getAlign() != null) {
			jsObjectContent += JsObjectName.Align.JS_NAME + ": '" + this.getAlign() + "', ";
		}
		if (this.getOrientation() != null) {
			jsObjectContent += JsObjectName.Orientation.JS_NAME + ": '" + this.getOrientation() + "', ";
		}

		jsObjectContent += JsObjectName.Stack.JS_NAME + ": " + this.getStack() + ", ";

		if (this.getSnap() != null) {
			jsObjectContent += JsObjectName.Snap.JS_NAME + ": " + this.addSnapFunction() + ", ";
		}

		jsObjectContent += JsObjectName.Editable.JS_NAME + ": {";
		if (this.getEditable()) {
			jsObjectContent += JsObjectName.Editable_Add.JS_NAME + ": true, ";
			jsObjectContent += JsObjectName.Editable_UpdateTime.JS_NAME + ": true, ";
			jsObjectContent += JsObjectName.Editable_UpdateGroup.JS_NAME + ": false, ";
			jsObjectContent += JsObjectName.Editable_Remove.JS_NAME + ": true";
		} else {
			jsObjectContent += JsObjectName.Editable_Add.JS_NAME + ": false, ";
			jsObjectContent += JsObjectName.Editable_UpdateTime.JS_NAME + ": false, ";
			jsObjectContent += JsObjectName.Editable_UpdateGroup.JS_NAME + ": false, ";
			jsObjectContent += JsObjectName.Editable_Remove.JS_NAME + ": false";
		}
		jsObjectContent += "}, ";

		jsObjectContent += JsObjectName.ClickToUse.JS_NAME + ": " + this.isClickToUse() + ", ";

		jsObjectContent += JsObjectName.GroupOrder.JS_NAME + ": '" + TimelineGroup.JsObjectName.Order.JS_NAME + "', ";

		jsObjectContent += JsObjectName.OnMove.JS_NAME + ": function(item, callback){";
		jsObjectContent += Timeline.JS_FUNCNAME_ONMOVE + "(item, callback);";
		jsObjectContent += "}, ";

		jsObjectContent += JsObjectName.OnMoving.JS_NAME + ": function(item, callback){";
		jsObjectContent += Timeline.JS_FUNCNAME_ONMOVING + "(item, callback);";
		jsObjectContent += "}, ";

		jsObjectContent += JsObjectName.OnAdd.JS_NAME + ": function(item, callback){ ";
		jsObjectContent += Timeline.JS_FUNCNAME_ONADD + "(item, callback);";
		jsObjectContent += "}, ";

		jsObjectContent += JsObjectName.OnRemove.JS_NAME + ": function(item, callback){ ";
		jsObjectContent += Timeline.JS_FUNCNAME_ONREMOVE + "(item, callback);";
		jsObjectContent += "}, ";

		jsObjectContent += JsObjectName.OnUpdate.JS_NAME + ": function(item, callback){ ";
		jsObjectContent += Timeline.JS_FUNCNAME_ONUPDATE + "(item, callback);";
		jsObjectContent += "}";

		return jsObjectContent;
	}

	/**
	 * Gets the js object.
	 *
	 * @return the js object
	 * @since 1.0.0
	 */
	protected String getJsObject() {
		String jsObject = "{";
		jsObject += this.buildJsObjectContent();
		jsObject += "}";
		return jsObject;
	}

	/**
	 * Gets the variable declaration.
	 *
	 * @return the variable declaration
	 * @since 1.0.0
	 */
	protected String getVariableDeclaration() {
		String declaration = "";

		// Current PK
		declaration += "var " + Timeline.JS_VARNAME_CURRENT_RECORD_PK;
		if (this.getRecord() != null) {
			declaration += " = '" + this.getRecord().getOccurrencePrimaryKey().format() + "'";
		}
		declaration += ";";

		// Initial start date
		declaration += "var " + Timeline.JS_VARNAME_INITIAL_START_DATE + " = ";
		if (this.isSnapToStartAndEnd()) {
			declaration += Timeline.formatDate(this.getStart(), true, false, this.getSnap());
		} else {
			declaration += Timeline.formatDate(this.getStart(), false, false, this.getSnap());
		}
		declaration += ";";

		// Initial end date
		declaration += "var " + Timeline.JS_VARNAME_INITIAL_END_DATE + " = ";
		if (this.isSnapToStartAndEnd()) {
			declaration += Timeline.formatDate(this.getEnd(), false, true, this.getSnap());
		} else {
			declaration += Timeline.formatDate(this.getEnd(), false, false, this.getSnap());
		}
		declaration += ";";

		// Min timeline date
		declaration += "var " + Timeline.JS_VARNAME_MIN_TIMELINE_DATE + " = ";
		if (this.isSnapToStartAndEnd()) {
			declaration += Timeline.formatDate(this.getMin(), true, false, this.getSnap());
		} else {
			declaration += Timeline.formatDate(this.getMin(), false, false, this.getSnap());
		}
		declaration += ";";

		// Max timeline date
		declaration += "var " + Timeline.JS_VARNAME_MAX_TIMELINE_DATE + " = ";
		if (this.isSnapToStartAndEnd()) {
			declaration += Timeline.formatDate(this.getMax(), false, true, this.getSnap());
		} else {
			declaration += Timeline.formatDate(this.getMax(), false, false, this.getSnap());
		}
		declaration += ";";

		// Initial custom date
		declaration += "var " + Timeline.JS_VARNAME_INITIAL_CUSTOM_TIME + " = ";
		if (this.getInitialCustomTime() == null) {
			declaration += "null";
		} else {
			if (this.isSnapToStartAndEnd()) {
				declaration += Timeline.formatDate(this.getInitialCustomTime(), true, false, this.getSnap());
			} else {
				declaration += Timeline.formatDate(this.getInitialCustomTime(), false, false, this.getSnap());
			}
		}
		declaration += ";";

		return declaration;
	}

}
