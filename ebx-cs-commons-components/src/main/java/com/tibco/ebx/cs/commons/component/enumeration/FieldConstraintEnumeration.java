package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.repository.RepositoryUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Constraint enumeration to list the fields of a given table.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class FieldConstraintEnumeration implements ConstraintEnumeration<String> {
	private Path dataspacePath;
	private Path datasetPath;
	private Path tablePath;
	private boolean excludeFK = false;
	private boolean excludeAssociation = false;
	private boolean excludeGroup = false;
	private boolean restrictToFK = false;
	private boolean restrictToAssociation = false;
	private boolean restrictToGroup = false;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		AdaptationTable table = this.getTable(pContext);
		if (table == null) {
			return pValue;
		}

		SchemaNode rootNode = table.getTableOccurrenceRootNode();

		SchemaNode node = rootNode.getNode(Path.SELF.add(Path.parse(pValue)));

		String stringLabel = node.getLabel(pLocale) + " (" + pValue + ")";

		return stringLabel;
	}

	/**
	 * Getter for the datasetPath parameter.
	 *
	 * @return the path of the dataset node.
	 * @since 1.8.0
	 */
	public Path getDatasetPath() {
		return this.datasetPath;
	}

	/**
	 * Getter for the dataspacePath parameter.
	 *
	 * @return the path of the dataspace node.
	 * @since 1.8.0
	 */
	public Path getDataspacePath() {
		return this.dataspacePath;
	}

	private ArrayList<SchemaNode> getNodes(final SchemaNode pNode) {
		ArrayList<SchemaNode> nodes = new ArrayList<>();

		for (SchemaNode node : pNode.getNodeChildren()) {
			if (!node.isTerminalValue()) {
				if (!this.excludeGroup && !this.restrictToAssociation && !this.restrictToFK) {
					nodes.add(node);
				}

				nodes.addAll(this.getNodes(node));
			} else if (node.getFacetOnTableReference() != null && !this.excludeFK && !this.restrictToAssociation && !this.restrictToGroup) {
				nodes.add(node);
			} else if (node.isAssociationNode() && !this.excludeAssociation && !this.restrictToFK && !this.restrictToGroup) {
				nodes.add(node);
			} else if (!this.restrictToAssociation && !this.restrictToFK && !this.restrictToGroup) {
				nodes.add(node);
			}
		}

		return nodes;
	}

	private AdaptationTable getTable(final ValueContext pContext) {
		String dataspaceName = (String) pContext.getValue(this.getDataspacePath());
		if (dataspaceName == null || dataspaceName.equals(CommonsConstants.THIS)) {
			AdaptationHome thisDataspace = pContext.getAdaptationInstance().getHome();
			dataspaceName = thisDataspace.getKey().getName();
		}

		String datasetName = (String) pContext.getValue(this.getDatasetPath());
		if (datasetName == null || datasetName.equals(CommonsConstants.THIS)) {
			Adaptation thisDataset = pContext.getAdaptationInstance();
			datasetName = thisDataset.getAdaptationName().getStringName();
		}

		String tablePathString = (String) pContext.getValue(this.getTablePath());
		if (tablePathString != null && tablePathString.equals(TableConstraintEnumeration.TABLE_THIS)) {
			AdaptationTable thisTable = pContext.getAdaptationTable();
			tablePathString = thisTable.getTablePath().format();
		}

		return RepositoryUtils.getTable(dataspaceName, datasetName, tablePathString);
	}

	/**
	 * Getter for the tablePath parameter.
	 *
	 * @return the path of the table node.
	 * @since 1.8.0
	 */
	public Path getTablePath() {
		return this.tablePath;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		ArrayList<String> values = new ArrayList<>();

		AdaptationTable table = this.getTable(pContext);
		if (table == null) {
			return values;
		}

		SchemaNode rootNode = table.getTableOccurrenceRootNode();

		ArrayList<SchemaNode> nodes = this.getNodes(rootNode);

		for (SchemaNode node : nodes) {
			values.add(Path.SELF.add(node.getPathInAdaptation()).format());
		}

		return values;
	}

	/**
	 * Getter for the excludeAssociation parameter.
	 *
	 * @return true if the enumeration is excluding association nodes, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isExcludeAssociation() {
		return this.excludeAssociation;
	}

	/**
	 * Getter for the excludeFK parameter.
	 *
	 * @return true if the enumeration is excluding foreign key fields, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isExcludeFK() {
		return this.excludeFK;
	}

	/**
	 * Getter for the excludeGroup parameter.
	 *
	 * @return true if the enumeration is excluding groupe nodes, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isExcludeGroup() {
		return this.excludeGroup;
	}

	/**
	 * Getter for the restrictToAssociation parameter.
	 *
	 * @return true if the enumeration is restricted to association nodes, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isRestrictToAssociation() {
		return this.restrictToAssociation;
	}

	/**
	 * Getter for the restrictToFK parameter.
	 *
	 * @return true if the enumeration is restricted to foreign key fields, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isRestrictToFK() {
		return this.restrictToFK;
	}

	/**
	 * Getter for the restrictToGroup parameter.
	 *
	 * @return true if the enumeration is restricted to groupe node, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isRestrictToGroup() {
		return this.restrictToGroup;
	}

	/**
	 * Setter for the datasetPath parameter. Can be null.
	 *
	 * @param pDatasetPath the path of the dataset node.
	 * @since 1.8.0
	 */
	public void setDatasetPath(final Path pDatasetPath) {
		this.datasetPath = pDatasetPath;
	}

	/**
	 * Setter for the dataspacePath parameter. Can be null.
	 *
	 * @param pDataspacePath the path of the dataspace node.
	 * @since 1.8.0
	 */
	public void setDataspacePath(final Path pDataspacePath) {
		this.dataspacePath = pDataspacePath;
	}

	/**
	 * Setter for the excludeAssociation parameter.
	 *
	 * @param pExcludeAssociation true if the enumeration is excluding association nodes, false otherwise.
	 * @since 1.8.0
	 */
	public void setExcludeAssociation(final boolean pExcludeAssociation) {
		this.excludeAssociation = pExcludeAssociation;
	}

	/**
	 * Setter for the excludeFK parameter.
	 *
	 * @param pExcludeFK true if the enumeration is excluding foreign key fields, false otherwise.
	 * @since 1.8.0
	 */
	public void setExcludeFK(final boolean pExcludeFK) {
		this.excludeFK = pExcludeFK;
	}

	/**
	 * Setter for the excludeGroup parameter.
	 *
	 * @param pExcludeGroup true if the enumeration is excluding groupe nodes, false otherwise.
	 * @since 1.8.0
	 */
	public void setExcludeGroup(final boolean pExcludeGroup) {
		this.excludeGroup = pExcludeGroup;
	}

	/**
	 * Setter for the restrictToAssociation parameter.
	 *
	 * @param pRestrictToAssociation true if the enumeration is restricted to association nodes, false otherwise.
	 * @since 1.8.0
	 */
	public void setRestrictToAssociation(final boolean pRestrictToAssociation) {
		this.restrictToAssociation = pRestrictToAssociation;
	}

	/**
	 * Setter for the restrictToFK parameter.
	 *
	 * @param pRestrictToFK true if the enumeration is restricted to foreign key fields, false otherwise.
	 * @since 1.8.0
	 */
	public void setRestrictToFK(final boolean pRestrictToFK) {
		this.restrictToFK = pRestrictToFK;
	}

	/**
	 * Setter for the restrictToGroup parameter.
	 *
	 * @param pRestrictToGroup true if the enumeration is restricted to group nodes, false otherwise.
	 * @since 1.8.0
	 */
	public void setRestrictToGroup(final boolean pRestrictToGroup) {
		this.restrictToGroup = pRestrictToGroup;
	}

	/**
	 * Setter for the tablePath parameter.
	 *
	 * @param pTablePath the path of the table node.
	 * @since 1.8.0
	 */
	public void setTablePath(final Path pTablePath) {
		this.tablePath = pTablePath;
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		SchemaNode schemaNode = pContext.getSchemaNode();

		if (this.dataspacePath != null) {
			SchemaNode dataspaceNode = schemaNode.getNode(this.dataspacePath);
			if (dataspaceNode != null) {
				pContext.addDependencyToModify(dataspaceNode);
			} else {
				pContext.addError("The path [" + this.dataspacePath.format() + "] does not exist in the table");
			}
		}

		if (this.datasetPath != null) {
			SchemaNode datasetNode = schemaNode.getNode(this.datasetPath);
			if (datasetNode != null) {
				pContext.addDependencyToModify(datasetNode);
			} else {
				pContext.addError("The path [" + this.datasetPath.format() + "] does not exist in the table");
			}
		}

		if (this.tablePath != null) {
			SchemaNode tableNode = schemaNode.getNode(this.tablePath);
			if (tableNode != null) {
				pContext.addDependencyToModify(tableNode);
			} else {
				pContext.addError("The path [" + this.tablePath.format() + "] does not exist in the table");
			}
		} else {
			pContext.addError("The tablePath parameter shall not be null");
		}
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "FieldConstraintEnumeration.userDocumentation");
	}
}
