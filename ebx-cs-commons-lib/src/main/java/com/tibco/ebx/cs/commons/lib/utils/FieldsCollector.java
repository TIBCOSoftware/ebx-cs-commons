package com.tibco.ebx.cs.commons.lib.utils;

import java.util.ArrayList;
import java.util.List;

import com.orchestranetworks.schema.SchemaNode;

/**
 * Collector of fields
 * 
 * @author Mickaël Chevalier
 */
public class FieldsCollector {

	/** The including sub terminals. */
	private boolean includingSubTerminals = false;

	/** The including over terminals. */
	private boolean includingOverTerminals = false;

	/** The including selection nodes. */
	private boolean includingSelectionNodes = false;

	/** The including associations. */
	private boolean includingAssociations = false;

	/** The including computed. */
	private boolean includingComputed = false;

	/** The categories. */
	private final List<String> categories = new ArrayList<>();

	/**
	 * Adds the category.
	 *
	 * @param pCategory the category
	 */
	public void addCategory(final String pCategory) {
		this.categories.add(pCategory);
	}

	/**
	 * Collect fields in table.
	 *
	 * @param pRootNode the root node
	 * @return the list
	 */
	public List<SchemaNode> collectFieldsInTable(final SchemaNode pRootNode) {
		if (!pRootNode.isTableNode() && !pRootNode.isTableOccurrenceNode()) {
			throw new IllegalArgumentException("The specified node is not an occurrence node");
		}

		return this.collectNodes(pRootNode);
	}

	/**
	 * Collect nodes.
	 *
	 * @param pNode the node
	 * @return the list
	 */
	private List<SchemaNode> collectNodes(final SchemaNode pNode) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {

			if (!this.categories.isEmpty() && !this.categories.contains(node.getCategory())) {
				continue;
			}

			if (!node.isTerminalValue() && !node.isTerminalValueDescendant() && !this.includingOverTerminals) {
				nodes.addAll(this.collectNodes(node));
				continue;
			}

			if (node.isAssociationNode() && !this.includingAssociations || node.isSelectNode() && !this.includingSelectionNodes || node.isValueFunction() && !this.includingComputed) {
				continue;
			}

			nodes.add(node);

			if (node.isTerminalValue() && !this.includingSubTerminals) {
				continue;
			}

			nodes.addAll(this.collectNodes(node));
		}
		return nodes;
	}

	/**
	 * Checks if is including associations.
	 *
	 * @return true, if is including associations
	 */
	public boolean isIncludingAssociations() {
		return this.includingAssociations;
	}

	/**
	 * Checks if is including computed.
	 *
	 * @return true, if is including computed
	 */
	public boolean isIncludingComputed() {
		return this.includingComputed;
	}

	/**
	 * Checks if is including over terminals.
	 *
	 * @return true, if is including over terminals
	 */
	public boolean isIncludingOverTerminals() {
		return this.includingOverTerminals;
	}

	/**
	 * Checks if is including selection nodes.
	 *
	 * @return true, if is including selection nodes
	 */
	public boolean isIncludingSelectionNodes() {
		return this.includingSelectionNodes;
	}

	/**
	 * Checks if is including sub terminals.
	 *
	 * @return true, if is including sub terminals
	 */
	public boolean isIncludingSubTerminals() {
		return this.includingSubTerminals;
	}

	/**
	 * Sets the including associations.
	 *
	 * @param includingAssociations the new including associations
	 */
	public void setIncludingAssociations(final boolean includingAssociations) {
		this.includingAssociations = includingAssociations;
	}

	/**
	 * Sets the including computed.
	 *
	 * @param includingComputed the new including computed
	 */
	public void setIncludingComputed(final boolean includingComputed) {
		this.includingComputed = includingComputed;
	}

	/**
	 * Sets the including over terminals.
	 *
	 * @param includingOverTerminals the new including over terminals
	 */
	public void setIncludingOverTerminals(final boolean includingOverTerminals) {
		this.includingOverTerminals = includingOverTerminals;
	}

	/**
	 * Sets the including selection nodes.
	 *
	 * @param includingSelectionNodes the new including selection nodes
	 */
	public void setIncludingSelectionNodes(final boolean includingSelectionNodes) {
		this.includingSelectionNodes = includingSelectionNodes;
	}

	/**
	 * Sets the including sub terminals.
	 *
	 * @param includingSubTerminals the new including sub terminals
	 */
	public void setIncludingSubTerminals(final boolean includingSubTerminals) {
		this.includingSubTerminals = includingSubTerminals;
	}

}
