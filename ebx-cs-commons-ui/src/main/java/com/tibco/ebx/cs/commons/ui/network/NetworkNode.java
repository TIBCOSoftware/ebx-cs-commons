package com.tibco.ebx.cs.commons.ui.network;

/**
 * The Class NetworkNode.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class NetworkNode {
	private String id;
	private String label;
	private NetworkNodeOptions options;

	/**
	 * Instantiates a new network node.
	 *
	 * @since 1.0.0
	 */
	public NetworkNode() {
	}

	/**
	 * Instantiates a new network node.
	 *
	 * @param id    the id
	 * @param label the label
	 * @since 1.0.0
	 */
	public NetworkNode(final String id, final String label) {
		this.id = id;
		this.label = label;
	}

	/**
	 * Instantiates a new network node.
	 *
	 * @param id      the id
	 * @param label   the label
	 * @param options the options
	 * @since 1.0.0
	 */
	public NetworkNode(final String id, final String label, final NetworkNodeOptions options) {
		this.id = id;
		this.label = label;
		this.options = options;
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
	 * Gets the label.
	 *
	 * @return the label
	 * @since 1.0.0
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 * @since 1.0.0
	 */
	public NetworkNodeOptions getOptions() {
		return this.options;
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
	 * Sets the label.
	 *
	 * @param label the new label
	 * @since 1.0.0
	 */
	public void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the new options
	 * @since 1.0.0
	 */
	public void setOptions(final NetworkNodeOptions options) {
		this.options = options;
	}

	/**
	 * Gets the JS object.
	 *
	 * @return the JS object
	 * @since 1.0.0
	 */
	protected String getJSObject() {
		String jsObject = "{";

		jsObject += "id: '" + this.getId() + "', ";
		jsObject += "label: '" + this.getLabel() + "'";
		if (this.options != null) {
			jsObject += ", options: " + this.getOptions().getJsObject();
		}
		jsObject += "}";

		return jsObject;
	}
}
