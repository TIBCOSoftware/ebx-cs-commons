package com.tibco.ebx.cs.commons.ui.form;

import java.util.ArrayList;

import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;
import com.tibco.ebx.cs.commons.ui.util.Presales_UIUtils;

/**
 * Define a UIFormPane with the sub-nodes of a group in the form.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class UIFormGroupPane implements UIFormPane {
	private final ArrayList<SchemaNode> nodes = new ArrayList<>();
	private boolean addTitle = false;
	private String groupTitle;
	private String nodeInformationToExclude;
	private int nbColumn = 1;
	private String style = null;

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNodes the list of nodes of the groups.
	 * @throws IllegalArgumentException if pNodes is null.
	 * @since 1.4.0
	 */
	public UIFormGroupPane(final ArrayList<SchemaNode> pNodes) throws IllegalArgumentException {
		super();
		if (pNodes == null) {
			throw new IllegalArgumentException("pNodes argument shall not be null");
		}
		this.nodes.addAll(pNodes);
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNodes                    the list of nodes of the groups.
	 * @param pAddTitle                 the boolean defining if titles (label of the parent node) is added as first row of the group.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if pNodes is null.
	 * @since 1.4.0
	 */
	public UIFormGroupPane(final ArrayList<SchemaNode> pNodes, final boolean pAddTitle, final String pNodeInformationToExclude) throws IllegalArgumentException {
		super();
		if (pNodes == null) {
			throw new IllegalArgumentException("pNodes argument shall not be null");
		}
		this.nodes.addAll(pNodes);
		this.addTitle = pAddTitle;
		this.nodeInformationToExclude = pNodeInformationToExclude;
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNodes                    the list of nodes of the groups.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if pNodes is null.
	 * @since 1.4.0
	 */
	public UIFormGroupPane(final ArrayList<SchemaNode> pNodes, final String pNodeInformationToExclude) throws IllegalArgumentException {
		super();
		if (pNodes == null) {
			throw new IllegalArgumentException("pNodes argument shall not be null");
		}
		this.nodes.addAll(pNodes);
		this.nodeInformationToExclude = pNodeInformationToExclude;
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNode the node of the group.
	 * @throws IllegalArgumentException if pNode is null.
	 * @since 1.0.0
	 */
	public UIFormGroupPane(final SchemaNode pNode) throws IllegalArgumentException {
		super();
		if (pNode == null) {
			throw new IllegalArgumentException("pNode argument shall not be null");
		}
		this.nodes.add(pNode);
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNode                     the node of the group.
	 * @param pAddTitle                 the boolean defining if a title (label of the parent node) is added as first row.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if pNode is null.
	 * @since 1.0.0
	 */
	public UIFormGroupPane(final SchemaNode pNode, final boolean pAddTitle, final String pNodeInformationToExclude) throws IllegalArgumentException {
		super();
		if (pNode == null) {
			throw new IllegalArgumentException("pNode argument shall not be null");
		}
		this.nodes.add(pNode);
		this.addTitle = pAddTitle;
		this.nodeInformationToExclude = pNodeInformationToExclude;
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNode                     the node of the group.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if pNode is null.
	 * @since 1.0.0
	 */
	public UIFormGroupPane(final SchemaNode pNode, final String pNodeInformationToExclude) throws IllegalArgumentException {
		super();
		if (pNode == null) {
			throw new IllegalArgumentException("pNode argument shall not be null");
		}
		this.nodes.add(pNode);
		this.nodeInformationToExclude = pNodeInformationToExclude;
	}

	/**
	 * Instantiates a new UIFormPane including the sub-nodes of the group(s).
	 *
	 * @param pNode                     the node of the group.
	 * @param pTitle                    the title added as first row.
	 * @param pNodeInformationToExclude the node information to check to exclude a node.
	 * @throws IllegalArgumentException if pNode is null.
	 * @since 1.0.0
	 */
	public UIFormGroupPane(final SchemaNode pNode, final String pTitle, final String pNodeInformationToExclude) throws IllegalArgumentException {
		super();
		if (pNode == null) {
			throw new IllegalArgumentException("pNode argument shall not be null");
		}
		this.nodes.add(pNode);
		this.addTitle = true;
		this.groupTitle = pTitle;
		this.nodeInformationToExclude = pNodeInformationToExclude;
	}

	/**
	 * Gets the number of column.
	 *
	 * @return the number of column.
	 * @since 1.4.0
	 */
	public int getNbColumn() {
		return this.nbColumn;
	}

	/**
	 * Gets the value of the NodeInformation to exclude corresponding nodes.
	 *
	 * @return the NodeInformation.
	 * @since 1.4.0
	 */
	public String getNodeInformationToExclude() {
		return this.nodeInformationToExclude;
	}

	/**
	 * Get the style set to the layout.
	 *
	 * @return the style.
	 * @since 1.5.0
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * Set the number of column.
	 *
	 * @param nbColumn the number of column.
	 * @since 1.4.0
	 */
	public void setNbColumn(final int nbColumn) {
		if (nbColumn < 1) {
			this.nbColumn = 1;
		} else {
			this.nbColumn = nbColumn;
		}
	}

	/**
	 * Set the value of the NodeInformation to exclude corresponding nodes.
	 *
	 * @param nodeInformationToExclude the NodeInformation.
	 * @since 1.4.0
	 */
	public void setNodeInformationToExclude(final String nodeInformationToExclude) {
		this.nodeInformationToExclude = nodeInformationToExclude;
	}

	/**
	 * Set the style of the layout.
	 *
	 * @param style the style.
	 * @since 1.5.0
	 */
	public void setStyle(final String style) {
		this.style = style;
	}

	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		for (SchemaNode node : this.nodes) {
			if (this.addTitle) {
				pWriter.startTableFormRow();

				String title = this.groupTitle;
				if (title == null) {
					title = node.getLabel(pContext.getLocale());
				}
				Presales_UIUtils.addTitleFormRow(pWriter, title);

				pWriter.endTableFormRow();
			}
			Presales_UIUtils.addColumnsLayout(pWriter, node, this.nbColumn, this.style, this.nodeInformationToExclude);
		}
		Presales_UIUtils.standardizeFieldLabelWidth(pWriter);
	}
}
