/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.network;

/**
 * The Class NetworkInteractionOptions.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public class NetworkInteractionOptions {
	/**
	 * Gets the js object.
	 *
	 * @return the js object
	 * @since 1.0.0
	 */
	protected String getJsObject() {
		return "{dragNodes: false, selectable: false, selectConnectedEdges:false, hoverConnectedEdges:false}";
	}
}
