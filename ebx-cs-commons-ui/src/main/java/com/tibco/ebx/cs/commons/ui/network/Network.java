/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.network;

import java.util.List;

import com.orchestranetworks.ui.UIComponentWriter;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * The Class Network.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class Network {
	private List<NetworkNode> nodes;
	private List<NetworkEdge> edges;
	private NetworkOptions options;

	/**
	 * Instantiates a new network.
	 *
	 * @since 1.0.0
	 */
	public Network() {
	}

	/**
	 * Instantiates a new network.
	 *
	 * @param nodes   the nodes
	 * @param edges   the edges
	 * @param options the options
	 * @since 1.0.0
	 */
	public Network(final List<NetworkNode> nodes, final List<NetworkEdge> edges, final NetworkOptions options) {
		this.nodes = nodes;
		this.edges = edges;
		this.options = options;
	}

	/**
	 * Draw.
	 *
	 * @param pWriter            the writer
	 * @param pNetworkDivId      the network div id
	 * @param pContainerDivStyle the container div style
	 * @param pModuleName        module name
	 * @since 1.0.0
	 */
	public void draw(final UIComponentWriter pWriter, final String pNetworkDivId, final String pContainerDivStyle,
			final String pModuleName) {
		Network.addDependencies(pWriter, pModuleName);
		Network.addNetworkDiv(pWriter, pNetworkDivId, pContainerDivStyle);
		this.addNodes(pWriter);
		this.addEdges(pWriter);
		this.addOptions(pWriter);
		Network.addNetworkImplementation(pWriter, pNetworkDivId);
	}

	/**
	 * Gets the edges.
	 *
	 * @return the edges
	 * @since 1.0.0
	 */
	public List<NetworkEdge> getEdges() {
		return this.edges;
	}

	/**
	 * Gets the nodes.
	 *
	 * @return the nodes
	 * @since 1.0.0
	 */
	public List<NetworkNode> getNodes() {
		return this.nodes;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 * @since 1.0.0
	 */
	public NetworkOptions getOptions() {
		return this.options;
	}

	/**
	 * Sets the edges.
	 *
	 * @param pEdges the new edges
	 * @since 1.0.0
	 */
	public void setEdges(final List<NetworkEdge> pEdges) {
		this.edges = pEdges;
	}

	/**
	 * Sets the nodes.
	 *
	 * @param pNodes the new nodes
	 * @since 1.0.0
	 */
	public void setNodes(final List<NetworkNode> pNodes) {
		this.nodes = pNodes;
	}

	/**
	 * Sets the options.
	 *
	 * @param pOptions the new options
	 * @since 1.0.0
	 */
	public void setOptions(final NetworkOptions pOptions) {
		this.options = pOptions;
	}

	/**
	 * Adds the dependencies.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private static void addDependencies(final UIComponentWriter pWriter, final String pModuleName) {
		Presales_UIUtils.addCssLink(pWriter, "vis.css", pModuleName);
	}

	/**
	 * Adds the edges.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addEdges(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr();
		pWriter.addJS_cr("var edges = new vis.DataSet();");
		pWriter.addJS_cr();
		pWriter.addJS_cr("edges.add([");
		pWriter.addJS_cr();

		if (this.edges != null) {
			int nbEdges = this.edges.size();
			for (int i = 0; i < nbEdges; i++) {
				NetworkEdge edge = this.edges.get(i);
				pWriter.addJS("    " + edge.getJSObject());
				if (i + 1 < nbEdges) {
					pWriter.addJS_cr(",");
				}
				pWriter.addJS_cr();
			}
		}
		pWriter.addJS_cr();
		pWriter.addJS_cr("]);");
	}

	/**
	 * Adds the network div.
	 *
	 * @param pWriter       the writer
	 * @param pNetworkDivId the network div id
	 * @param pStyle        the style
	 * @since 1.0.0
	 */
	private static void addNetworkDiv(final UIComponentWriter pWriter, final String pNetworkDivId,
			final String pStyle) {
		pWriter.add("<div>");
		pWriter.add("<div id=\"" + pNetworkDivId + "\" style=\"" + pStyle + "\"></div>");
		pWriter.add("</div>");
	}

	/**
	 * Adds the network implementation.
	 *
	 * @param pWriter       the writer
	 * @param pNetworkDivId the network div id
	 * @since 1.0.0
	 */
	private static void addNetworkImplementation(final UIComponentWriter pWriter, final String pNetworkDivId) {
		pWriter.addJS_cr();
		pWriter.addJS_cr("var container = document.getElementById('" + pNetworkDivId + "');");
		pWriter.addJS_cr();
		pWriter.addJS_cr("var data = {");
		pWriter.addJS_cr("    nodes: nodes,");
		pWriter.addJS_cr("    edges: edges");
		pWriter.addJS_cr("};");
		pWriter.addJS_cr();
		pWriter.addJS_cr("var network = new vis.Network(container, data, options);");
		pWriter.addJS_cr();
	}

	/**
	 * Adds the nodes.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addNodes(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS_cr();
		pWriter.addJS_cr("var nodes = new vis.DataSet();");
		pWriter.addJS_cr();
		pWriter.addJS_cr("nodes.add([");
		pWriter.addJS_cr();
		if (this.nodes != null) {
			int nbNodes = this.nodes.size();
			for (int i = 0; i < nbNodes; i++) {
				NetworkNode node = this.nodes.get(i);
				pWriter.addJS("    " + node.getJSObject());
				if (i + 1 < nbNodes) {
					pWriter.addJS_cr(",");
				}
				pWriter.addJS_cr();
			}
		}
		pWriter.addJS_cr();
		pWriter.addJS_cr("]);");
	}

	/**
	 * Adds the options.
	 *
	 * @param pWriter the writer
	 * @since 1.0.0
	 */
	private void addOptions(final UIComponentWriter pWriter) {
		pWriter.addJS_cr();
		pWriter.addJS("var options = ");
		if (this.options != null) {
			pWriter.addJS(this.options.getJsObject());
		} else {
			pWriter.addJS("{}");
		}
		pWriter.addJS_cr(";");
		pWriter.addJS_cr();
	}

}
