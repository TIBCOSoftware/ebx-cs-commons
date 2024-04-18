package com.tibco.ebx.cs.commons.ui.timeline;

/**
 * The Class TimelineGroup.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class TimelineGroup {
	protected static class JsObjectName {
		private JsObjectName() {
			super();
		}

		protected static class Content {
			private Content() {
				super();
			}

			protected static final String JS_NAME = "content";
		}

		protected static class Id {
			private Id() {
				super();
			}

			protected static final String JS_NAME = "id";
		}

		protected static class NoAdd {
			private NoAdd() {
				super();
			}

			protected static final String JS_NAME = "noAdd";
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

		protected static class Order {
			private Order() {
				super();
			}

			protected static final String JS_NAME = "order";
		}
	}

	private String id;
	private String content;
	private boolean noAdd = true;
	private boolean noUpdate = true;
	private boolean noMove = true;
	private boolean noRemove = true;
	private Integer order = 100;
	private String itemStyle;
	private boolean schemaNode = false;

	/**
	 * Instantiates a new timeline group.
	 *
	 * @param id      the id
	 * @param content the content
	 * @since 1.0.0
	 */
	public TimelineGroup(final String id, final String content) {
		this.id = id;
		this.content = content;
	}

	/**
	 * Instantiates a new timeline group.
	 *
	 * @param id      the id
	 * @param content the content
	 * @param noAdd   the no add
	 * @since 1.0.0
	 */
	public TimelineGroup(final String id, final String content, final boolean noAdd) {
		this.id = id;
		this.content = content;
		this.noAdd = noAdd;
	}

	/**
	 * Instantiates a new timeline group.
	 *
	 * @param id      the id
	 * @param content the content
	 * @param noAdd   the no add
	 * @param order   the order
	 * @since 1.0.0
	 */
	public TimelineGroup(final String id, final String content, final boolean noAdd, final Integer order) {
		this.id = id;
		this.content = content;
		this.noAdd = noAdd;
		this.order = order;
	}

	/**
	 * Instantiates a new timeline group.
	 *
	 * @param id      the id
	 * @param content the content
	 * @param order   the order
	 * @since 1.0.0
	 */
	public TimelineGroup(final String id, final String content, final Integer order) {
		this.id = id;
		this.content = content;
		this.order = order;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 * @since 1.0.0
	 */
	public String getContent() {
		if (this.id == null) {
			return "";
		} else {
			return this.content;
		}
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 * @since 1.0.0
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Gets the item style.
	 *
	 * @return the item style
	 * @since 1.0.0
	 */
	public String getItemStyle() {
		if (this.itemStyle == null) {
			return "";
		} else {
			return this.itemStyle;
		}
	}

	/**
	 * Gets the order.
	 *
	 * @return the order
	 * @since 1.0.0
	 */
	public Integer getOrder() {
		if (this.order == null) {
			return 100;
		} else {
			return this.order;
		}
	}

	/**
	 * Checks if is no add.
	 *
	 * @return true, if is no add
	 * @since 1.0.0
	 */
	public boolean isNoAdd() {
		return this.noAdd;
	}

	/**
	 * Checks if is no move.
	 *
	 * @return true, if is no move
	 * @since 1.0.0
	 */
	public boolean isNoMove() {
		return this.noMove;
	}

	/**
	 * Checks if is no remove.
	 *
	 * @return true, if is no remove
	 * @since 1.0.0
	 */
	public boolean isNoRemove() {
		return this.noRemove;
	}

	/**
	 * Checks if is no update.
	 *
	 * @return true, if is no update
	 * @since 1.0.0
	 */
	public boolean isNoUpdate() {
		return this.noUpdate;
	}

	/**
	 * Checks if is schema node.
	 *
	 * @return true, if is schema node
	 * @since 1.0.0
	 */
	public boolean isSchemaNode() {
		return this.schemaNode;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 * @since 1.0.0
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Sets the item style.
	 *
	 * @param itemStyle the new item style
	 * @since 1.0.0
	 */
	public void setItemStyle(final String itemStyle) {
		this.itemStyle = itemStyle;
	}

	/**
	 * Sets the no add.
	 *
	 * @param noAdd the new no add
	 * @since 1.0.0
	 */
	public void setNoAdd(final boolean noAdd) {
		this.noAdd = noAdd;
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
	 * Sets the order.
	 *
	 * @param order the new order
	 * @since 1.0.0
	 */
	public void setOrder(final Integer order) {
		this.order = order;
	}

	/**
	 * Sets the schema node.
	 *
	 * @param schemaNode the new schema node
	 * @since 1.0.0
	 */
	public void setSchemaNode(final boolean schemaNode) {
		this.schemaNode = schemaNode;
	}

	/**
	 * Builds the js object content.
	 *
	 * @return the string
	 * @since 1.0.0
	 */
	protected String buildJsObjectContent() {
		String jsObjectContent = "";

		if (this.getId() != null) {
			jsObjectContent += JsObjectName.Id.JS_NAME + ": '" + this.getId() + "',";
		}
		jsObjectContent += JsObjectName.Content.JS_NAME + ": '" + this.getContent() + "',";
		jsObjectContent += JsObjectName.NoAdd.JS_NAME + ": " + this.isNoAdd() + ",";
		jsObjectContent += JsObjectName.NoUpdate.JS_NAME + ": " + this.isNoUpdate() + ",";
		jsObjectContent += JsObjectName.NoMove.JS_NAME + ": " + this.isNoMove() + ",";
		jsObjectContent += JsObjectName.NoRemove.JS_NAME + ": " + this.isNoRemove() + ",";
		jsObjectContent += JsObjectName.Order.JS_NAME + ": '" + this.getOrder() + "'";

		return jsObjectContent;
	}

	/**
	 * Gets the js object.
	 *
	 * @return the js object
	 * @since 1.0.0
	 */
	protected String getJsObject() {
		StringBuilder jsObject = new StringBuilder("{");
		jsObject.append(this.buildJsObjectContent());
		jsObject.append("}");
		return jsObject.toString();
	}

}
