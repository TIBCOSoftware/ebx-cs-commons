/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * Constraint enumeration to list the tables of a given schema.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class SchemaTableConstraintEnumeration implements ConstraintEnumeration<String> {

	private Path schemaPath;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) {
		String schemaLocation = (String) pContext.getValue(this.schemaPath);

		if (schemaLocation == null || schemaLocation.trim().isEmpty()) {
			return;
		}

		SchemaNode datasetRootNode = this.getDatasetRootNode(pContext.getHome().getRepository(), schemaLocation);
		if (datasetRootNode == null) {
			pContext.addError("No table at '" + pValue + "'");
		} else {
			SchemaNode node = datasetRootNode.getNode(Path.parse(pValue));
			if (node == null || !node.isTableNode()) {
				pContext.addError("No table at '" + pValue + "'");
			}
		}
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		Repository repository = pContext.getHome().getRepository();
		String schemaLocation = (String) pContext.getValue(this.schemaPath);

		if (schemaLocation == null || schemaLocation.trim().isEmpty()) {
			return pValue;
		}

		SchemaNode datasetRootNode = this.getDatasetRootNode(repository, schemaLocation);

		if (datasetRootNode != null) {
			ArrayList<SchemaNode> tableNodes = this.getTableNodes(datasetRootNode);
			for (SchemaNode tableNode : tableNodes) {
				String tablePath = tableNode.getPathInSchema().format();
				if (tablePath.equals(pValue)) {
					return tableNode.getLabel(pLocale) + " (" + pValue + ")";
				}
			}
		}

		return pValue;
	}

	private SchemaNode getDatasetRootNode(final Repository pRepository, final String pSchemaLocation) {
		ArrayList<AdaptationHome> dataspaces = this.getDataspaces(pRepository.getReferenceBranch());

		for (AdaptationHome dataspace : dataspaces) {
			List<Adaptation> rootDatasets = dataspace.findAllRoots();

			for (Adaptation dataset : rootDatasets) {
				String datasetSchemaLocation = dataset.getSchemaLocation().format();
				if (datasetSchemaLocation.equals(pSchemaLocation)) {
					return dataset.getSchemaNode();
				}
			}
		}

		return null;
	}

	private ArrayList<AdaptationHome> getDataspaces(final AdaptationHome pDataspace) {
		ArrayList<AdaptationHome> dataspaces = new ArrayList<>();

		if (pDataspace == null) {
			return dataspaces;
		}

		List<AdaptationHome> children = null;

		if (pDataspace.isBranch()) {
			children = pDataspace.getVersionChildren();
		} else {
			children = pDataspace.getBranchChildren();
		}

		for (AdaptationHome child : children) {
			if (child.isTechnicalBranch() || child.isTechnicalVersion()) {
				continue;
			}

			if (child.isBranch() && child.isOpen()) {
				dataspaces.add(child);
			}
			dataspaces.addAll(this.getDataspaces(child));
		}
		return dataspaces;
	}

	/**
	 * Getter for the schemaPath parameter.
	 *
	 * @return the path of the schema field.
	 * @since 1.8.0
	 */
	public Path getSchemaPath() {
		return this.schemaPath;
	}

	private ArrayList<SchemaNode> getTableNodes(final SchemaNode pNode) {
		List<SchemaNode> nodeChildren = Arrays.asList(pNode.getNodeChildren());

		ArrayList<SchemaNode> tableNodes = new ArrayList<>();

		for (SchemaNode nodeChild : nodeChildren) {
			if (nodeChild.isTableNode()) {
				tableNodes.add(nodeChild);
			} else {
				tableNodes.addAll(this.getTableNodes(nodeChild));
			}
		}

		return tableNodes;
	}

	private ArrayList<String> getTables(final Repository pRepository, final String pSchemaLocation) {
		ArrayList<String> tables = new ArrayList<>();

		SchemaNode datasetRootNode = this.getDatasetRootNode(pRepository, pSchemaLocation);

		if (datasetRootNode != null) {
			ArrayList<SchemaNode> tableNodes = this.getTableNodes(datasetRootNode);
			for (SchemaNode tableNode : tableNodes) {
				tables.add(tableNode.getPathInSchema().format());
			}
		}

		return tables;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) {
		Repository repository = pContext.getHome().getRepository();
		String schemaLocation = (String) pContext.getValue(this.schemaPath);

		if (repository == null || schemaLocation == null || schemaLocation.trim().isEmpty()) {
			return new ArrayList<>();
		}

		return this.getTables(repository, schemaLocation);
	}

	/**
	 * Setter for the schemaPath parameter. Cannot be null.
	 *
	 * @param pSchemaPath the path of the schema field.
	 * @since 1.8.0
	 */
	public void setSchemaPath(final Path pSchemaPath) {
		this.schemaPath = pSchemaPath;
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		SchemaNode rootSchemaNode = pContext.getSchemaNode();

		if (this.schemaPath == null) {
			pContext.addError("The schemaPath parameter shall not be null");
		} else {
			SchemaNode schemaNode = rootSchemaNode.getNode(this.schemaPath);
			if (schemaNode != null) {
				pContext.addDependencyToModify(schemaNode);
			} else {
				pContext.addError("The path [" + this.schemaPath.format() + "] does not exist in the table");
			}
		}
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) {
		return Messages.get(this.getClass(), pLocale, "SchemaTableConstraintEnumeration.userDocumentation");
	}
}
