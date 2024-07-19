/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
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
 * Constraint enumeration to list the fields of a given table (through schema).
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class SchemaFieldConstraintEnumeration implements ConstraintEnumeration<String> {
	private Path schemaPath;
	private Path tablePath;
	private boolean excludeFK = false;
	private boolean excludeAssociation = false;
	private boolean excludeGroup = false;
	private boolean restrictToFK = false;
	private boolean restrictToAssociation = false;
	private boolean restrictToGroup = false;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// no implementation
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		Repository repository = pContext.getHome().getRepository();
		String schemaLocation = (String) pContext.getValue(this.schemaPath);
		String tablePathString = (String) pContext.getValue(this.tablePath);

		if (repository == null || schemaLocation == null || schemaLocation.trim().isEmpty() || tablePathString == null || tablePathString.trim().isEmpty()) {
			return pValue;
		}

		Optional<SchemaNode> optionalTableRootNode = this.getTableNode(repository, schemaLocation, tablePathString);
		if (!optionalTableRootNode.isPresent()) {
			return pValue;
		}

		SchemaNode node = optionalTableRootNode.get().getNode(Path.SELF.add(Path.parse(pValue)));

		return node.getLabel(pLocale) + " (" + pValue + ")";
	}

	private Adaptation getDataset(final Repository pRepository, final String pSchemaLocation) {
		ArrayList<AdaptationHome> dataspaces = this.getDataspaces(pRepository.getReferenceBranch());

		for (AdaptationHome dataspace : dataspaces) {
			List<Adaptation> rootDatasets = dataspace.findAllRoots();

			for (Adaptation dataset : rootDatasets) {
				String datasetSchemaLocation = dataset.getSchemaLocation().format();
				if (datasetSchemaLocation.equals(pSchemaLocation)) {
					return dataset;
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

	/**
	 * Getter for the schemaPath parameter.
	 *
	 * @return the path of the schema node.
	 * @since 1.8.0
	 */
	public Path getSchemaPath() {
		return this.schemaPath;
	}

	private Optional<SchemaNode> getTableNode(final Repository pRepository, final String pSchemaLocation, final String pTablePathString) {
		Adaptation dataset = this.getDataset(pRepository, pSchemaLocation);
		if (dataset == null) {
			return Optional.empty();
		}

		AdaptationTable table = dataset.getTable(Path.parse(pTablePathString));
		if (table == null) {
			return Optional.empty();
		}

		return Optional.of(table.getTableOccurrenceRootNode());
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
		Repository repository = pContext.getHome().getRepository();
		String schemaLocation = (String) pContext.getValue(this.schemaPath);
		String tablePathString = (String) pContext.getValue(this.tablePath);

		if (repository == null || schemaLocation == null || schemaLocation.trim().isEmpty() || tablePathString == null || tablePathString.trim().isEmpty()) {
			return new ArrayList<>();
		}

		Optional<SchemaNode> optionalTableRootNode = this.getTableNode(repository, schemaLocation, tablePathString);
		if (!optionalTableRootNode.isPresent()) {
			return new ArrayList<>();
		}

		ArrayList<SchemaNode> nodes = this.getNodes(optionalTableRootNode.get());

		ArrayList<String> values = new ArrayList<>();
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
	 * Setter for the schemaPath parameter.
	 *
	 * @param pSchemaPath the path of the schema node.
	 * @since 1.8.0
	 */
	public void setSchemaPath(final Path pSchemaPath) {
		this.schemaPath = pSchemaPath;
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
		SchemaNode rootSchemaNode = pContext.getSchemaNode();

		if (this.schemaPath != null) {
			SchemaNode schemaNode = rootSchemaNode.getNode(this.schemaPath);
			if (schemaNode != null) {
				pContext.addDependencyToModify(schemaNode);
			} else {
				pContext.addError("The path [" + this.schemaPath.format() + "] does not exist in the table");
			}
		}

		if (this.tablePath != null) {
			SchemaNode schemaNode = rootSchemaNode.getNode(this.tablePath);
			if (schemaNode != null) {
				pContext.addDependencyToModify(schemaNode);
			} else {
				pContext.addError("The path [" + this.tablePath.format() + "] does not exist in the table");
			}
		} else {
			pContext.addError("The tablePath parameter shall not be null");
		}
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "SchemaFieldConstraintEnumeration.userDocumentation");
	}
}
