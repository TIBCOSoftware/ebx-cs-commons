/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 */
package com.tibco.ebx.cs.commons.lib.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationReference;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.PathAccessException;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.AssociationLink;
import com.orchestranetworks.schema.info.AssociationLinkByLinkTable;
import com.orchestranetworks.schema.info.AssociationLinkByTableRefInverse;
import com.orchestranetworks.schema.info.AssociationLinkByXPathLink;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.schema.info.SchemaNodeDefaultView;
import com.orchestranetworks.schema.info.SchemaNodeInformation;
import com.orchestranetworks.schema.info.SelectionLink;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.userservice.ObjectKey;
import com.orchestranetworks.userservice.UserServiceObjectContext;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.repository.RepositoryUtils;

/**
 * Utility class to manipulate schemas.
 *
 * @author Mickaël Chevalier
 */
public final class SchemaUtils {

	/**
	 * Get the list of external data models from a given node.
	 * 
	 * @param pDatasets         datasets
	 * @param pInitialHome      initial home
	 * @param pNode             current node
	 * @param pIncludeIndirects include indirects or not ?
	 */
	public static void collectLinkedDatasets(final Set<Adaptation> pDatasets, final HomeKey pInitialHome, final SchemaNode pNode, final boolean pIncludeIndirects) {
		for (SchemaNode node : pNode.getNodeChildren()) {
			AdaptationReference datasetReference = null;
			HomeKey homeKey = null;
			SchemaNode extNode = null;
			if (node.isAssociationNode()) {
				datasetReference = node.getAssociationLink().getDataSetReference();
				homeKey = node.getAssociationLink().getDataSpaceReference();
				extNode = node.getAssociationLink().getTableNode();
			} else if (node.getFacetOnTableReference() != null) {
				datasetReference = node.getFacetOnTableReference().getContainerReference();
				homeKey = node.getFacetOnTableReference().getContainerHome();
				extNode = node.getFacetOnTableReference().getTableNode();
			} else if (node.isTableNode()) {
				node = node.getTableOccurrenceRootNode();
				collectLinkedDatasets(pDatasets, pInitialHome, node, pIncludeIndirects);
			} else {
				collectLinkedDatasets(pDatasets, pInitialHome, node, pIncludeIndirects);
			}

			if (datasetReference != null) {
				if (homeKey == null) {
					homeKey = pInitialHome;
				}
				Adaptation dataset = RepositoryUtils.getDataSet(homeKey.getName(), datasetReference.getStringName());
				if (!pDatasets.contains(dataset) && pIncludeIndirects) {
					pDatasets.add(dataset);
					collectLinkedDatasets(pDatasets, pInitialHome, getSchemaRootNode(extNode), pIncludeIndirects);
				}
			}
		}
	}

	/**
	 * Get the list of association nodes from a given node.
	 *
	 * @param pRootNode the node from where to search for association nodes
	 * @return the list of association nodes
	 *
	 */
	public static List<SchemaNode> getAssociationNodes(final SchemaNode pRootNode) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pRootNode.getNodeChildren()) {
			if (node.isAssociationNode()) {
				nodes.add(node);
			} else {
				nodes.addAll(getTerminalNodes(node));
			}
		}
		return nodes;
	}

	/**
	 * Return a list a schemas locations representing all referenced data models.
	 *
	 * @since 2.0.0
	 * @param pDataset          The dataset from which to search for reference to other data models.
	 * @param pIncludeIndirects If true, if a referenced data model is itself referencing data models, include them and so on.
	 * @return the list a SchemaLocation representing all referenced data models
	 */
	public static Set<Adaptation> getLinkedDataModels(final Adaptation pDataset, final boolean pIncludeIndirects) {
		Set<Adaptation> datasets = new HashSet<>();
		collectLinkedDatasets(datasets, pDataset.getHome().getKey(), pDataset.getSchemaNode(), pIncludeIndirects);
		datasets.remove(pDataset);
		return datasets;
	}

	/**
	 * Return the file name without extension of the data model, empty if not in module.
	 *
	 * @since 1.0.0
	 * @param pSchemaLocation The schema location of the data model.
	 * @return The file name without extension of the data model or empty if the model is not in a module.
	 */
	public static Optional<String> getModelFileName(final SchemaLocation pSchemaLocation) {
		File file = pSchemaLocation.getFileOrNull();
		if (file == null) {
			return Optional.empty();
		}
		int index = file.getName().lastIndexOf('.');
		if (index == -1) {
			return Optional.ofNullable(file.getName());
		}
		return Optional.ofNullable(file.getName().substring(0, index));
	}

	/**
	 * Get a node from a table occurrence or an instance. It secures the absolute path turning them into a relative path.
	 *
	 * @since 2.0.0
	 * @param pRecordOrDataset A table occurrence or an instance.
	 * @param pPath            Path from the root of the first parameter.
	 * @return The schema node located a the given path.
	 * @throw PathAccessException If the node is not found.
	 */
	public static SchemaNode getNode(final Adaptation pRecordOrDataset, Path pPath) {
		if (pPath.format().startsWith("/")) {
			pPath = Path.SELF.add(pPath);
		}
		SchemaNode node = pRecordOrDataset.getSchemaNode().getNode(pPath);
		if (node == null) {
			throw new PathAccessException(pPath, "Path '" + pPath.format() + "' not found in '" + pRecordOrDataset.getLabel(Locale.getDefault()) + "'");
		}
		return node;
	}

	/**
	 * Get a node from a table occurrence or an instance. It secures the absolute path turning them into a relative path.
	 *
	 * @since 2.0.0
	 * @param pValueContext An EBX value context.
	 * @param pPath         Path from the position of the first parameter.
	 * @return The schema node located a the given path.
	 * @throw PathAccessException If the node is not found.
	 */
	public static SchemaNode getNode(final ValueContext pValueContext, Path pPath) {
		if (pPath.format().startsWith("/")) {
			pPath = Path.SELF.add(pPath);
		}
		SchemaNode node = pValueContext.getNode().getNode(pPath);
		if (node == null) {
			throw new PathAccessException(pPath, "Path '" + pPath.format() + "' not found");
		}
		return node;
	}

	/**
	 * Get the path to the root of a record from a node
	 *
	 * @param pNode a schema node
	 *
	 * @return the path to the root of the record from the given node.
	 *
	 * @throws IllegalArgumentException if the specified node is neither a table or a table occurrence node.
	 */
	public static Path getPathToRecordRoot(final SchemaNode pNode) {
		if (pNode.isTableNode()) {
			return Path.SELF;
		}
		SchemaNode node = pNode.getParent();
		Path pathToRecordRoot = Path.SELF;
		while (node != null) {
			node = node.getParent();
			pathToRecordRoot = pathToRecordRoot.add(Path.PARENT);
		}
		return pathToRecordRoot;
	}

	/**
	 * Get the dataset that is referenced from a foreign key definition.
	 *
	 * @param pFacetOnTableRef The nodes facet describing the foreign key.
	 * @param pDataset         The initial dataset from where the reference has to be resolved.
	 * @return dataset Adaptation
	 * @throws EBXResourceNotFoundException EBXResourceNotFoundException
	 * @since 2.0.12
	 */
	public static Adaptation getRelatedDataset(final SchemaFacetTableRef pFacetOnTableRef, Adaptation pDataset) throws EBXResourceNotFoundException {
		AdaptationReference instance = pFacetOnTableRef.getContainerReference();
		if (instance == null) {
			return pDataset;
		}
		String home = pFacetOnTableRef.getContainerHome() == null ? null : pFacetOnTableRef.getContainerHome().getName();
		pDataset = AdaptationUtils.getDataset(pDataset.createValueContext(), Optional.ofNullable(home), Optional.ofNullable(instance.getStringName()));
		return pDataset;
	}

	/**
	 * Get the path in module of a schema from its full location.
	 *
	 * @author MCH
	 *
	 * @param pLocation The SchemaLocation from which to retrieve the path in module.
	 *
	 * @return the path of the schema in the module.
	 */
	public static String getSchemaPathInModule(final SchemaLocation pLocation) {
		return pLocation.format().substring(pLocation.format().lastIndexOf(':') + 1);
	}

	/**
	 * From any node, gets the root node of the underlying schema.
	 *
	 * @param pNode a schema node
	 *
	 * @return a schema node root of the schema
	 */
	private static SchemaNode getSchemaRootNode(final SchemaNode pNode) {
		if (pNode.getParent() == null) {
			return pNode;
		}
		return getSchemaRootNode(pNode.getParent());
	}

	/**
	 * Get the table node above a node
	 *
	 * @author MCH
	 * @param pNode a schema node
	 *
	 * @return the table node aboce a current occurrence node.
	 *
	 * @throws IllegalArgumentException if the specified node is neither a table or a table occurrence node.
	 */
	public static SchemaNode getTableNode(final SchemaNode pNode) {
		return pNode.getNode(SchemaUtils.getPathToRecordRoot(pNode));
	}

	/**
	 * For a given node, if the node is a relationship node (foreign key, association or selection node), return the target table of the relationship.
	 *
	 * @param node the node
	 * @return the table node of the related table or null if the node is not a relationship node
	 */
	public static SchemaNode getTableNodeForRelated(final SchemaNode node) {
		return getTableNodeForRelated(node, null);
	}

	/**
	 * For a given node, if the node is a relationship node (foreign key, association or selection node), return the target table of the relationship.
	 *
	 * @param node    the node
	 * @param dataSet the data set for the table you are starting from
	 * @return the table node of the related table or null if the node is not a relationship node
	 */
	public static SchemaNode getTableNodeForRelated(final SchemaNode node, Adaptation dataSet) {
		SchemaFacetTableRef tableRef = node.getFacetOnTableReference();
		if (tableRef != null) {
			return tableRef.getTableNode();
		} else if (node.isAssociationNode()) {
			AssociationLink link = node.getAssociationLink();
			if (link == null) {
				return null;
			}
			HomeKey dataSpaceKey = link.getDataSpaceReference();
			AdaptationName dataSetKey = link.getDataSetReference();
			SchemaNode dataSetRoot = node;
			if (dataSetKey != null) {
				if (dataSet == null) {
					throw new IllegalArgumentException("If an association uses a table from another data set, an original data set is required to find the target table");
				}
				AdaptationHome dataSpace = dataSet.getHome();
				if (dataSpaceKey != null) {
					dataSpace = dataSpace.getRepository().lookupHome(dataSpaceKey);
				}
				dataSet = dataSpace.findAdaptationOrNull(dataSetKey);
				dataSetRoot = dataSet.getSchemaNode();
			}
			if (link.isLinkTable()) {
				AssociationLinkByLinkTable alink = (AssociationLinkByLinkTable) link;
				Path targetPath = alink.getFieldToTargetPath();
				SchemaNode fieldNode = dataSetRoot.getNode(targetPath);
				return getTableNodeForRelated(fieldNode, dataSet);
			} else if (link.isXPathLink()) {
				AssociationLinkByXPathLink alink = (AssociationLinkByXPathLink) link;
				return alink.getTableNode();
			} else {
				AssociationLinkByTableRefInverse alink = (AssociationLinkByTableRefInverse) link;
				Path sourcePath = alink.getFieldToSourcePath();
				return dataSetRoot.getNode(sourcePath).getTableNode();
			}
		} else if (node.isSelectNode()) {
			SelectionLink link = node.getSelectionLink();
			return link.getTableNode();
		}
		return null;
	}

	/**
	 * Get the list of table nodes from a given node.
	 *
	 *
	 * @param pNode the node from where to search for table nodes
	 * @return the list of table nodes
	 *
	 */
	public static List<SchemaNode> getTableNodes(final SchemaNode pNode) {
		List<SchemaNode> nodes = new ArrayList<>();
		if (pNode.isTableOccurrenceNode()) {
			return nodes;
		}
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isTableNode()) {
				nodes.add(node);
			} else {
				nodes.addAll(getTableNodes(node));
			}
		}
		return nodes;
	}

	/**
	 * Get the list of tabs from a given node.
	 *
	 *
	 * @param pRootNode the node from where to search for tabs
	 * @return the list of terminal nodes
	 *
	 */
	public static List<SchemaNode> getTabs(final SchemaNode pRootNode) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pRootNode.getNodeChildren()) {
			if (isDisplayedAsTab(node)) {
				nodes.add(node);
			} else {
				nodes.addAll(getTabs(node));
			}
		}
		return nodes;
	}

	/**
	 * Get the list of terminal nodes from a given node.
	 *
	 *
	 * @param pRootNode the node from where to search for terminal nodes
	 * @return the list of terminal nodes
	 *
	 */
	public static List<SchemaNode> getTerminalNodes(final SchemaNode pRootNode) {
		return getTerminalNodes(pRootNode, null);
	}

	/**
	 * Get the list of terminal nodes from a given node.
	 *
	 *
	 * @param pRootNode                          the node from where to search for terminal nodes
	 * @param pIgnoreFieldsWithInformationEquals value of information for attributes to ignore.
	 * @return the list of terminal nodes
	 *
	 */
	public static List<SchemaNode> getTerminalNodes(final SchemaNode pRootNode, final String pIgnoreFieldsWithInformationEquals) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pRootNode.getNodeChildren()) {
			if (!StringUtils.isBlank(pIgnoreFieldsWithInformationEquals) && node.getInformation() != null && pIgnoreFieldsWithInformationEquals.equals(node.getInformation().getInformation())) {
				continue;
			}
			if (node.isTerminalValue()) {
				nodes.add(node);
			} else {
				nodes.addAll(getTerminalNodes(node, pIgnoreFieldsWithInformationEquals));
			}
		}
		return nodes;

	}

	/**
	 * Get the list of nodes from a given node which are visible by a user for a given record.
	 *
	 * @param pNode       the node from where to search for nodes
	 * @param pSession    the session to get the permission from
	 * @param pAdaptation the adaptation to get the permission from. Can be null.
	 * @return the list of nodes
	 *
	 */
	public static List<SchemaNode> getVisibleNodes(final SchemaNode pNode, final Session pSession, final Adaptation pAdaptation) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (isNodeVisible(node, pSession, pAdaptation)) {
				nodes.add(node);
			}
			nodes.addAll(getVisibleNodes(node, pSession, pAdaptation));
		}
		return nodes;
	}

	/**
	 * Get the list of terminal nodes from a given node which are visible by a user.
	 *
	 *
	 * @param pRecordOrDataset the record to get the permission from, a data set otherwise.
	 * @param pNode            the node from where to search for terminal nodes
	 * @param tabs
	 * @param pSession         the session to get the permission from
	 * @return the list of terminal nodes
	 *
	 */
	public static List<SchemaNode> getVisibleTerminalNodes(final Adaptation pRecordOrDataset, final SchemaNode pNode, final List<SchemaNode> excludedNodes, final Session pSession) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (excludedNodes != null && excludedNodes.contains(node)) {
				continue;
			}
			if (node.isTerminalValue()) {
				if (!pSession.getPermissions().getNodeAccessPermission(node, pRecordOrDataset).isHidden() && !node.getDefaultViewProperties().isHidden()) {
					nodes.add(node);
				}
			} else {
				nodes.addAll(getVisibleTerminalNodes(pRecordOrDataset, node, excludedNodes, pSession));
			}
		}
		return nodes;
	}

	/**
	 * Get the list of terminal nodes from a given node which are visible by a user for a given record.
	 *
	 *
	 * @param pNode       the node from where to search for nodes
	 * @param pSession    the session to get the permission from
	 * @param pAdaptation the adaptation to get the permission from. Can be null.
	 * @return the list of terminal nodes
	 *
	 */
	public static List<SchemaNode> getVisibleTerminalNodes(final SchemaNode pNode, final Session pSession, final Adaptation pAdaptation) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (node.isTerminalValue()) {
				if (isNodeVisible(node, pSession, pAdaptation)) {
					nodes.add(node);
				}
			} else {
				nodes.addAll(getVisibleTerminalNodes(node, pSession, pAdaptation));
			}
		}
		return nodes;
	}

	/**
	 * Get the list of terminal nodes from a given object key which are visible by a user.
	 *
	 *
	 * @param pContext       the context of validation of a user service.
	 * @param pKey           the key of the object registered in the service to introspect.
	 * @param pExcludedNodes list of nodes to be excluded from the visible nodes.
	 * @return the list of visible terminal nodes
	 *
	 */
	public static List<SchemaNode> getVisibleTerminalNodes(final SchemaNode pNode, final UserServiceObjectContext pContext, final ObjectKey pKey, final List<SchemaNode> pExcludedNodes) {
		List<SchemaNode> nodes = new ArrayList<>();
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (pExcludedNodes != null && pExcludedNodes.contains(node)) {
				continue;
			}
			if (node.isTerminalValue()) {
				if (!pContext.getPermission(pKey, Path.SELF.add(node.getPathInAdaptation())).isHidden() && !node.getDefaultViewProperties().isHidden()) {
					nodes.add(node);
				}
			} else {
				nodes.addAll(getVisibleTerminalNodes(node, pContext, pKey, pExcludedNodes));
			}
		}
		return nodes;
	}

	/**
	 * Check if the node has children.
	 *
	 * @since 2.0.0
	 * @param pNode The node to test.
	 * @return true if the node has children.
	 */
	private static boolean hasChildren(final SchemaNode node) {
		return node.getNodeChildren().length == 0;
	}

	/**
	 * Checks if a node display mode is tab.
	 *
	 * @param pNode the node
	 * @return true, if display mode is tab
	 */
	// TODO replace private code
	public static boolean isDisplayedAsTab(final SchemaNode pNode) {
		SchemaNodeDefaultView properties = pNode.getDefaultViewProperties();
		if (properties == null) {
			return false;
		}
		return "tab".equals(properties.getDisplayMode());
	}

	/**
	 * Checks if a node is a list.
	 *
	 * @param pNode the node
	 * @return true, if is list
	 */
	public static boolean isList(final SchemaNode pNode) {
		return pNode.getMaxOccurs() > 1;
	}

	/**
	 * Check if a node is a list of complex elements.
	 *
	 * @since 2.0.0
	 * @param pNode The node to test.
	 * @return true if the node is a list of complex.
	 */
	private static boolean isListOfComplex(final SchemaNode pNode) {
		return pNode.isComplex() && pNode.getMaxOccurs() > 1;
	}

	/**
	 * Checks if a node is mandatory regarding a specific type. This method is used by DynamicFormBasedOnType
	 *
	 * The information of an field is expected to be a succession of token separated by ';'. Each token is composed as the type and the associated mandatory boolean separated by a ','. Example :
	 * typeA,true;typeB,false -&gt; the node belongs to 2 types typeA and typeB. the node is mandatory for type typeA.
	 *
	 * @see DynamicAccessRuleBasedOnTypes
	 * @see DynamicForm
	 *
	 * @param pNode  the node
	 * @param pTypes the category
	 * @return true, if the node is mandatory in the category.
	 */
	public static boolean isNodeMandatory(final SchemaNode pNode, final List<String> pTypes) {
		SchemaNodeInformation nodeInformation = pNode.getInformation();
		if (nodeInformation == null) {
			return pNode.getMinOccurs() > 0;
		}

		String information = nodeInformation.getInformation();
		if (information == null) {
			return pNode.getMinOccurs() > 0;
		}

		List<String> tokens = Arrays.asList(information.split(";"));
		for (String token : tokens) {
			String[] split = token.split(",");
			if (split.length != 2) {
				continue;
			}
			String type = split[0];
			if (pTypes.contains(type)) {
				return Boolean.valueOf(split[1]).booleanValue();
			}
		}
		return pNode.getMinOccurs() > 0;
	}

	/**
	 * Verify if a node is valued by verifying if it is null or an empty list.
	 *
	 * @since 1.0.0
	 * @param pAdaptation A table occurrence or a dataset.
	 * @param pNode       The node to test.
	 * @return true if the node is valued.
	 */
	public static boolean isNodeValued(final Adaptation pAdaptation, final SchemaNode pNode) {
		if (pNode.getMaxOccurs() > 1) {
			return !pAdaptation.getList(pNode.getPathInAdaptation()).isEmpty();
		} else {
			return pAdaptation.get(pNode) != null;
		}
	}

	/**
	 * Verify if a node is valued by verifying if it is null or an empty list.
	 *
	 * @since 1.0.0
	 * @param pContext An EBX value context.
	 * @param pNode    The node to test.
	 * @return true if the node is valued.
	 */
	public static boolean isNodeValued(final ValueContext pContext, final SchemaNode pNode) {
		if (pNode.getMaxOccurs() > 1) {
			return !((List<?>) pContext.getValue(pNode.getPathInAdaptation())).isEmpty();
		} else {
			return pContext.getValue(pNode) != null;
		}
	}

	/**
	 * Verify if a node is visible to a user.
	 *
	 *
	 * @param pNode       the node from where to search for nodes
	 * @param pSession    the session to get the permission from
	 * @param pAdaptation the adaptation to get the permission from. Can be null.
	 * @return True if the node is visible.
	 *
	 */
	public static boolean isNodeVisible(final SchemaNode pNode, final Session pSession, final Adaptation pAdaptation) {
		return !pNode.getDefaultViewProperties().isHidden() && (pAdaptation == null || !pSession.getPermissions().getNodeAccessPermission(pNode, pAdaptation).isHidden());
	}

	/**
	 * Checks if is primary key.
	 *
	 * @param pNode the node
	 * @return true, if is primary key
	 */
	public static boolean isPrimaryKey(final SchemaNode pNode) {
		if (!pNode.isTableOccurrenceNode()) {
			return false;
		}
		SchemaNode tableNode = pNode.getTableNode();
		if (tableNode == null) {
			return false;
		}

		SchemaNode[] pkNodes = tableNode.getTablePrimaryKeyNodes();
		for (SchemaNode aPKNode : pkNodes) {
			if (aPKNode.equals(pNode)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the node carries a value under a terminal.
	 *
	 * @since 2.0.0
	 * @param node The node to test.
	 * @return true if the node is terminal value descendant and terminal itself.
	 */
	public static boolean isTerminalUnderTerminal(final SchemaNode node) {
		return node.isTerminalValueDescendant() && (isListOfComplex(node) || hasChildren(node) || node.getAccessMode().isReadWrite());
	}

	private SchemaUtils() {
	}
}
