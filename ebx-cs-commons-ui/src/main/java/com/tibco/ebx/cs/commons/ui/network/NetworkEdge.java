package com.tibco.ebx.cs.commons.ui.network;

/**
 * The Class NetworkEdge.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class NetworkEdge {
	private String from;
	private String to;
	private NetworkEdgeOptions options;

	/**
	 * Instantiates a new network edge.
	 *
	 * @since 1.0.0
	 */
	public NetworkEdge() {
	}

	/**
	 * Instantiates a new network edge.
	 *
	 * @param from the from
	 * @param to   the to
	 * @since 1.0.0
	 */
	public NetworkEdge(final String from, final String to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Instantiates a new network edge.
	 *
	 * @param from    the from
	 * @param to      the to
	 * @param options the options
	 * @since 1.0.0
	 */
	public NetworkEdge(final String from, final String to, final NetworkEdgeOptions options) {
		this.from = from;
		this.to = to;
		this.options = options;
	}

	/**
	 * Gets the from.
	 *
	 * @return the from
	 * @since 1.0.0
	 */
	public String getFrom() {
		return this.from;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 * @since 1.0.0
	 */
	public NetworkEdgeOptions getOptions() {
		return this.options;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 * @since 1.0.0
	 */
	public String getTo() {
		return this.to;
	}

	/**
	 * Sets the from.
	 *
	 * @param from the new from
	 * @since 1.0.0
	 */
	public void setFrom(final String from) {
		this.from = from;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the new options
	 * @since 1.0.0
	 */
	public void setOptions(final NetworkEdgeOptions options) {
		this.options = options;
	}

	/**
	 * Sets the to.
	 *
	 * @param to the new to
	 * @since 1.0.0
	 */
	public void setTo(final String to) {
		this.to = to;
	}

	/**
	 * Gets the JS object.
	 *
	 * @return the JS object
	 * @since 1.0.0
	 */
	protected String getJSObject() {
		String jsObject = "{";

		jsObject += "from: '" + this.getFrom() + "', ";
		jsObject += "to: '" + this.getTo() + "'";

		jsObject += "}";

		return jsObject;
	}
}
