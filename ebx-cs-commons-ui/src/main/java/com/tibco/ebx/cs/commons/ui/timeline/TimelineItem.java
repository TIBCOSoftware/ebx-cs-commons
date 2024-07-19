/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.timeline;

import java.util.Date;

/**
 * The Class TimelineItem defines an item of the timeline.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class TimelineItem {
	/**
	 * The Class Type.
	 *
	 * @since 1.0.0
	 */
	public static class Type {
		private Type() {
			super();
		}

		/**
		 * "background" value for the type of timeline item.
		 */
		public static final String BACKGROUND = "background";
	}

	protected static class JsObjectName {
		private JsObjectName() {
			super();
		}

		protected static class ClassName {
			private ClassName() {
				super();
			}

			protected static final String JS_NAME = "className";
		}

		protected static class Content {
			private Content() {
				super();
			}

			protected static final String JS_NAME = "content";
		}

		protected static class End {
			private End() {
				super();
			}

			protected static final String JS_NAME = "end";
		}

		protected static class Group {
			private Group() {
				super();
			}

			protected static final String JS_NAME = "group";
		}

		protected static class IsEndless {
			private IsEndless() {
				super();
			}

			protected static final String JS_NAME = "isEndless";
		}

		protected static class IsFixed {
			private IsFixed() {
				super();
			}

			protected static final String JS_NAME = "isFixed";
		}

		protected static class NoMove {
			private NoMove() {
				super();
			}

			protected static final String JS_NAME = "noMove";
		}

		protected static class NoRemove {
			private NoRemove() {
				super();
			}

			protected static final String JS_NAME = "noRemove";
		}

		protected static class NoUpdate {
			private NoUpdate() {
				super();
			}

			protected static final String JS_NAME = "noUpdate";
		}

		protected static class PK {
			private PK() {
				super();
			}

			protected static final String JS_NAME = "pk";
		}

		protected static class Start {
			private Start() {
				super();
			}

			protected static final String JS_NAME = "start";
		}

		protected static class Style {
			private Style() {
				super();
			}

			protected static final String JS_NAME = "style";
		}

		protected static class Type {
			private Type() {
				super();
			}

			protected static final String JS_NAME = "type";
		}
	}

	private String group;
	private Date start;
	private Date end;
	private String type;
	private String content;
	private String className;
	private boolean isFixed;
	private Boolean isEndless;
	private boolean noUpdate;
	private boolean noMove;
	private boolean noRemove;
	private String style;
	private String pk;
	private boolean snapToStartAndEnd = false;
	private String snapType;

	/**
	 * Instantiates a new timeline item.
	 *
	 * @param group   the group
	 * @param start   the start
	 * @param end     the end
	 * @param content the content
	 * @since 1.0.0
	 */
	public TimelineItem(final String group, final Date start, final Date end, final String content) {
		this.group = group;
		this.start = start;
		this.end = end;
		this.content = content;
	}

	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 * @since 1.0.0
	 */
	public String getClassName() {
		if (this.className == null) {
			return "";
		} else {
			return this.className;
		}
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 * @since 1.0.0
	 */
	public String getContent() {
		if (this.content == null) {
			return "";
		} else {
			return this.content;
		}
	}

	/**
	 * Gets the end.
	 *
	 * @return the end
	 * @since 1.0.0
	 */
	public Date getEnd() {
		return this.end;
	}

	/**
	 * Gets the group.
	 *
	 * @return the group
	 * @since 1.0.0
	 */
	public String getGroup() {
		if (this.group == null) {
			return "";
		} else {
			return this.group;
		}
	}

	/**
	 * Gets the no move.
	 *
	 * @return the no move
	 * @since 1.0.0
	 */
	public boolean getNoMove() {
		return this.noMove;
	}

	/**
	 * Gets the no remove.
	 *
	 * @return the no remove
	 * @since 1.0.0
	 */
	public boolean getNoRemove() {
		return this.noRemove;
	}

	/**
	 * Gets the no update.
	 *
	 * @return the no update
	 * @since 1.0.0
	 */
	public boolean getNoUpdate() {
		return this.noUpdate;
	}

	/**
	 * Gets the pk.
	 *
	 * @return the pk
	 * @since 1.0.0
	 */
	public String getPk() {
		if (this.pk == null) {
			return "";
		} else {
			return this.pk;
		}
	}

	/**
	 * Gets the snap type.
	 *
	 * @return the snap type
	 * @since 1.0.0
	 */
	public String getSnapType() {
		return this.snapType;
	}

	/**
	 * Gets the start.
	 *
	 * @return the start
	 * @since 1.0.0
	 */
	public Date getStart() {
		return this.start;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 * @since 1.0.0
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 * @since 1.0.0
	 */
	public String getType() {
		if (this.type == null) {
			return "";
		} else {
			return this.type;
		}
	}

	/**
	 * Checks if is endless.
	 *
	 * @return true, if is endless
	 * @since 1.0.0
	 */
	public boolean isEndless() {
		if (this.isEndless == null) {
			return (this.getEnd() == null);
		} else {
			return this.isEndless.booleanValue();
		}

	}

	/**
	 * Checks if is fixed.
	 *
	 * @return true, if is fixed
	 * @since 1.0.0
	 */
	public boolean isFixed() {
		return this.isFixed;
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
	 * Sets the class name.
	 *
	 * @param className the new class name
	 * @since 1.0.0
	 */
	public void setClassName(final String className) {
		this.className = className;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 * @since 1.0.0
	 */
	public void setContent(final String content) {
		this.content = content;
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
	 * Sets the endless.
	 *
	 * @param isEndless the new endless
	 * @since 1.0.0
	 */
	public void setEndless(final boolean isEndless) {
		this.isEndless = isEndless;
	}

	/**
	 * Sets the group.
	 *
	 * @param group the new group
	 * @since 1.0.0
	 */
	public void setGroup(final String group) {
		this.group = group;
	}

	/**
	 * Sets the checks if is fixed.
	 *
	 * @param isFixed the new checks if is fixed
	 * @since 1.0.0
	 */
	public void setIsFixed(final boolean isFixed) {
		this.isFixed = isFixed;
	}

	/**
	 * Sets the no move.
	 *
	 * @param noMove the new no move
	 * @since 1.0.0
	 */
	public void setNoMove(final boolean noMove) {
		this.noMove = noMove;
	}

	/**
	 * Sets the no remove.
	 *
	 * @param noRemove the new no remove
	 * @since 1.0.0
	 */
	public void setNoRemove(final boolean noRemove) {
		this.noRemove = noRemove;
	}

	/**
	 * Sets the no update.
	 *
	 * @param noUpdate the new no update
	 * @since 1.0.0
	 */
	public void setNoUpdate(final boolean noUpdate) {
		this.noUpdate = noUpdate;
	}

	/**
	 * Sets the pk.
	 *
	 * @param pk the new pk
	 * @since 1.0.0
	 */
	public void setPk(final String pk) {
		this.pk = pk;
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
	 * Sets the snap type.
	 *
	 * @param snapType the new snap type
	 * @since 1.0.0
	 */
	public void setSnapType(final String snapType) {
		this.snapType = snapType;
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
	 * Sets the style.
	 *
	 * @param style the new style
	 * @since 1.0.0
	 */
	public void setStyle(final String style) {
		this.style = style;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 * @see TimelineItem.Type
	 * @since 1.0.0
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * Overlap.
	 *
	 * @param start1 the start1
	 * @param end1   the end1
	 * @param start2 the start2
	 * @param end2   the end2
	 * @return true, if successful
	 * @since 1.0.0
	 */
	private boolean overlap(final Date start1, final Date end1, final Date start2, final Date end2) {
		if (end1 == null && start1 != null) {
			return start1.before(end2);
		} else if (start1 == null && end1 != null) {
			return end1.before(end2);
		} else if (start1 != null) {
			return (start1.before(start2) && end1.after(start2) || start1.before(end2) && end1.after(end2) || start1.before(start2) && end1.after(end2) || start1.equals(end2) || end1.equals(start2));
		} else {
			return false;
		}
	}

	/**
	 * Builds the js object content. Content means the key / value pair. Example: id: '12456789', content: 'Value A' The content is then encapsulated within the curly brackets. This allows to override
	 * this method while extending the class to add custom key/value pairs in the jsObject.
	 *
	 * @return the string representing the content of the JavaScript object.
	 * @since 1.0.0
	 */
	protected String buildJsObjectContent() {
		String jsObjectContent = "";
		jsObjectContent += JsObjectName.Start.JS_NAME + ": ";

		if (this.isSnapToStartAndEnd()) {
			jsObjectContent += Timeline.formatDate(this.getStart(), true, false, this.getSnapType());
		} else {
			jsObjectContent += Timeline.formatDate(this.getStart(), false, false, this.getSnapType());
		}

		jsObjectContent += ", ";

		jsObjectContent += JsObjectName.End.JS_NAME + ": ";
		if (this.getEnd() == null) {
			jsObjectContent += "null";
		} else {
			jsObjectContent += Timeline.formatDate(this.getEnd(), false, true, this.getSnapType());
		}
		jsObjectContent += ", ";
		if (this.group != null) {
			jsObjectContent += JsObjectName.Group.JS_NAME + ": '" + this.getGroup() + "', ";
		}

		if (this.type != null) {
			jsObjectContent += JsObjectName.Type.JS_NAME + ": '" + this.getType() + "', ";
		}
		jsObjectContent += JsObjectName.Content.JS_NAME + ": '" + this.getContent() + "', ";
		jsObjectContent += JsObjectName.ClassName.JS_NAME + ": '" + this.getClassName() + "', ";
		jsObjectContent += JsObjectName.Style.JS_NAME + ": '" + this.getStyle() + "', ";
		jsObjectContent += JsObjectName.PK.JS_NAME + ": '" + this.getPk() + "', ";
		jsObjectContent += JsObjectName.IsFixed.JS_NAME + ": " + this.isFixed() + ", ";
		jsObjectContent += JsObjectName.IsEndless.JS_NAME + ": " + this.isEndless() + ", ";
		jsObjectContent += JsObjectName.NoUpdate.JS_NAME + ": " + this.getNoUpdate() + ", ";
		jsObjectContent += JsObjectName.NoMove.JS_NAME + ": " + this.getNoMove() + ", ";
		jsObjectContent += JsObjectName.NoRemove.JS_NAME + ": " + this.getNoRemove();

		return jsObjectContent;
	}

	/**
	 * Equal items.
	 *
	 * @param otherItem the other item to compare.
	 * @return true, if equals
	 * @since 1.0.0
	 */
	protected boolean equalItems(final TimelineItem otherItem) {
		if (this.getType().equals(TimelineItem.Type.BACKGROUND)) {
			return false;
		}
		if (otherItem.getType().equals(TimelineItem.Type.BACKGROUND)) {
			return false;
		}

		boolean equal = true;

		if (!this.getGroup().equals(otherItem.getGroup())) {
			equal = false;
		}

		if (!this.getContent().equals(otherItem.getContent())) {
			equal = false;
		}

		Date startDate = this.getStart();
		Date endDate = this.getEnd();
		Date otherStartDate = otherItem.getStart();
		Date otherEndDate = otherItem.getEnd();

		if (startDate == null) {
			equal = false;
		} else {
			if (!this.overlap(startDate, endDate, otherStartDate, otherEndDate)) {
				equal = false;
			}
		}

		return equal;
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
}
