package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
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
 * Constraint enumeration to list the tables of a given dataset. You can also include a value "This" to represent the current table and restrict to "This".
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class TableConstraintEnumeration implements ConstraintEnumeration<String> {
	/**
	 * String value of "This".
	 *
	 * @since 1.8.0
	 */
	public static final String TABLE_THIS = "This";

	private Path dataspacePath;
	private Path datasetPath;
	private boolean restrictToThis = false;
	private boolean includeThis = false;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// not implemented
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		if (pValue.equals(TableConstraintEnumeration.TABLE_THIS)) {
			return Messages.get(this.getClass(), pLocale, "table.This");
		}

		Adaptation dataset = this.getDataset(pContext);
		if (dataset == null) {
			return pValue;
		}

		SchemaNode tableNode = dataset.getSchemaNode().getNode(Path.parse(pValue));
		return tableNode.getLabel(pLocale) + " (" + pValue + ")";
	}

	private Adaptation getDataset(final ValueContext pContext) {
		String dataspaceName = (String) pContext.getValue(this.getDataspacePath());
		if (dataspaceName == null || dataspaceName.equals(CommonsConstants.THIS)) {
			AdaptationHome thisDataspace = pContext.getAdaptationInstance().getHome();
			dataspaceName = thisDataspace.getKey().getName();
		}

		String datasetName = (String) pContext.getValue(this.getDatasetPath());
		if (datasetName != null && datasetName.equals(CommonsConstants.THIS)) {
			return pContext.getAdaptationInstance();
		}

		return RepositoryUtils.getDataSet(dataspaceName, datasetName);
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

	private ArrayList<SchemaNode> getTableNodes(final SchemaNode pNode) {
		ArrayList<SchemaNode> nodes = new ArrayList<>();
		if (pNode.isTableOccurrenceNode()) {
			return nodes;
		}
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isTableNode()) {
				nodes.add(node);
			} else {
				nodes.addAll(this.getTableNodes(node));
			}
		}
		return nodes;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		if (this.isIncludeThis()) {
			values.add(TableConstraintEnumeration.TABLE_THIS);
		}
		if (this.isRestrictToThis()) {
			return values;
		}

		Adaptation dataset = this.getDataset(pContext);
		if (dataset == null) {
			return values;
		}

		SchemaNode rootNode = dataset.getSchemaNode();

		ArrayList<SchemaNode> tableNodes = this.getTableNodes(rootNode);
		for (SchemaNode tableNode : tableNodes) {
			values.add(tableNode.getPathInSchema().format());
		}

		return values;
	}

	/**
	 * Getter for the includeThis parameter.
	 *
	 * @return true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public boolean isIncludeThis() {
		return this.includeThis;
	}

	/**
	 * Getter for the restrictToThis parameter.
	 *
	 * @return true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public boolean isRestrictToThis() {
		return this.restrictToThis;
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
	 * Setter for the includeThis parameter.
	 *
	 * @param pIncludeThis true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public void setIncludeThis(final boolean pIncludeThis) {
		this.includeThis = pIncludeThis;
	}

	/**
	 * Setter for the restrictToThis parameter.
	 *
	 * @param pRestrictToThis true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public void setRestrictToThis(final boolean pRestrictToThis) {
		this.restrictToThis = pRestrictToThis;
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
		} else {
			pContext.addError("The datasetPath parameter shall not be null");
		}
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "TableConstraintEnumeration.userDocumentation");
	}
}
