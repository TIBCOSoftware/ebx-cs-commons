package com.tibco.ebx.cs.commons.lib.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.XPathExpressionHelper;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsFunctionalReason;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;

/**
 * Utility class to manipulate table occurrences and datasets. @See {@link Adaptation}
 * 
 * @since 1.0.0
 * @author Mickaël Chevalier
 */
public final class AdaptationUtils {

	private AdaptationUtils() {
		super();
	}

	/**
	 * Check if a dataspace is the home in parameter or one of its descendants
	 *
	 * @since 2.0.0
	 * @param pHome               the home to compare
	 * @param pDataspace          the name of the dataspace to qualify
	 * @param pIncludeDescendants if true, will look if it is a descendant.
	 *
	 * @return a response as boolean. false if the dataspace has not been found.
	 * @throws EBXResourceNotFoundException EBXResourceNotFoundException
	 */
	public static boolean checkDataspace(final AdaptationHome pHome, final String pDataspace, final Boolean pIncludeDescendants) throws EBXResourceNotFoundException {
		if (pHome.getKey().getName().equals(pDataspace)) {
			return true;
		}
		if (pIncludeDescendants.booleanValue()) {
			AdaptationHome ds = getDataspace(pHome.getRepository(), pDataspace);
			if (ds != null) {
				while ((ds = ds.getParent()) != null) {
					if (ds.getKey().getName().equals(pDataspace)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get all tables in a dataset. It is equivalent of <code>getAllTables(dataSet, Path.ROOT)</code>
	 *
	 * @since 1.0.0
	 * @param pDataset A dataset.
	 * @return All tables or an empty list if none exist.
	 */
	public static List<AdaptationTable> getAllTables(final Adaptation pDataset) {
		return getAllTables(pDataset, Path.ROOT);
	}

	/**
	 * Get all tables in a dataset under a given node.
	 *
	 * @since 1.0.0
	 * @param pDataset A dataset.
	 * @param pPath    The path from which to look for tables.
	 * @return the tables for the data set under the given node, or an empty list if none exist
	 */
	public static List<AdaptationTable> getAllTables(final Adaptation pDataset, final Path pPath) {
		if (!pDataset.isSchemaInstance()) {
			throw new IllegalArgumentException(pDataset.getLabel(Locale.getDefault()) + " is not a dataset.");
		}
		SchemaNode node = SchemaUtils.getNode(pDataset, pPath);
		ArrayList<AdaptationTable> tables = new ArrayList<>();
		for (SchemaNode child : node.getNodeChildren()) {
			if (child.isTableNode()) {
				tables.add(pDataset.getTable(child.getPathInSchema()));
			} else {
				tables.addAll(getAllTables(pDataset, child.getPathInAdaptation()));
			}
		}
		return tables;
	}

	/**
	 * Get a data set from its identifier.
	 *
	 * @since 1.0.0
	 * @param pDataspaceOrSnapshot A dataspace or a snapshot in which to search for the dataset.
	 * @param pDataset             A data set name @see {@link AdaptationName}.
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException If the dataset has not been found.
	 */
	public static Adaptation getDataset(final AdaptationHome pDataspaceOrSnapshot, final String pDataset) throws EBXResourceNotFoundException {
		Adaptation dataset = pDataspaceOrSnapshot.findAdaptationOrNull(AdaptationName.forName(pDataset));
		if (dataset == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_DATASET_NOT_FOUND, pDataset, pDataspaceOrSnapshot.getKey().getName());
		}
		return dataset;
	}

	/**
	 * Get a data set from its identifier and the identifier of its dataspace
	 *
	 * @since 1.0.0
	 * @param pRepository A repository
	 * @param pDataspace  The identifier of a dataspace @see {@link HomeKey}
	 * @param pDataset    The identifier of a dataset @see {@link AdaptationName}
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException If the dataspace or the dataset has not been found.
	 */
	public static Adaptation getDataset(final Repository pRepository, final String pDataspace, final String pDataset) throws EBXResourceNotFoundException {
		return getDataset(getDataspace(pRepository, pDataspace), pDataset);
	}

	/**
	 * Get a dataset from its identifier and the identifier of its dataspace. These identifier can be null and in this case are resolved from the value context. If no data space name is specified, the
	 * data set will be searched in the current data space. If no data set name is specified, the current data set name will be searched.
	 *
	 * @since 1.0.0
	 * @param pContext   An EBX value context
	 * @param pDataspace The identifier of a dataspace where to find the data set, null if in current dataspace. @see {@link HomeKey}
	 * @param pDataset   The identifier of a dataset to find, if null current data set name will be searched. @see {@link AdaptationName}
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException If dataspace or dataset is not found
	 */
	public static Adaptation getDataset(final ValueContext pContext, final Optional<String> pDataspace, final Optional<String> pDataset) throws EBXResourceNotFoundException {

		AdaptationHome home = pContext.getHome();
		AdaptationName name = pContext.getAdaptationInstance().getAdaptationName();

		if (pDataspace.isPresent()) {
			home = getDataspace(pContext.getHome().getRepository(), pDataspace.get());
		}

		if (pDataset.isPresent()) {
			name = AdaptationName.forName(pDataset.get());
		}

		Adaptation dataset = home.findAdaptationOrNull(name);
		if (dataset == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_DATASET_NOT_FOUND, name.getStringName(), home.getKey().getName());
		}
		return dataset;
	}

	/**
	 * Get a dataspace from its identifier.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository.
	 * @param pDataspace  A dataspace identifier @see {@link HomeKey}.
	 * @return A dataspace.
	 * @throws EBXResourceNotFoundException If the dataspace has not been found.
	 */
	public static AdaptationHome getDataspace(final Repository pRepository, final String pDataspace) throws EBXResourceNotFoundException {
		AdaptationHome dataspace = pRepository.lookupHome(HomeKey.forBranchName(pDataspace));
		if (dataspace == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_DATASPACE_NOT_FOUND, pDataspace);
		}
		return dataspace;
	}

	/**
	 * Get the list of records from a foreign key, an association or a selection node.
	 *
	 * @since 1.0.0
	 * @param pRecordOrDataset A table occurrence or a dataset.
	 * @param pPath            The XPath of foreign key, an association or a selection node.
	 * @param pPredicate       An XPath predicate to be used as filter. Can be null.
	 * @return All linked records matching the predicate.
	 */
	public static List<Adaptation> getLinkedRecords(final Adaptation pRecordOrDataset, final Path pPath, final Optional<String> pPredicate) {
		final SchemaNode node = SchemaUtils.getNode(pRecordOrDataset, pPath);
		if (node.getFacetOnTableReference() != null) {
			List<Adaptation> linkedRecords = node.getFacetOnTableReference().getLinkedRecords(pRecordOrDataset);
			if (pPredicate.isPresent()) {
				linkedRecords = filterListOfRecordOrDataset(linkedRecords, pPredicate.get());
			}
			return linkedRecords;
		} else if (node.isAssociationNode() || node.isSelectNode()) {
			RequestResult result = requestLinkedRecords(pRecordOrDataset, pPath);
			try {
				return getListOfRecordsFromRequestResult(result, pPredicate);
			} finally {
				result.close();
			}
		} else {
			throw new IllegalArgumentException("The specified path must lead to a foreign key, an association or a selection node.");
		}
	}

	/**
	 * Filter a list of records or datasets with an XPath predicate.
	 *
	 * @since 2.0.0
	 * @param pRecordsOrDatasets A list of table occurrences or datasets.
	 * @param pPredicate         An XPath predicate to be used as filter.
	 * @return All records matching the predicate.
	 */
	public static List<Adaptation> filterListOfRecordOrDataset(final List<Adaptation> pRecordsOrDatasets, final String pPredicate) {
		List<Adaptation> records = new ArrayList<>();
		for (Adaptation record : pRecordsOrDatasets) {
			if (StringUtils.isBlank(pPredicate) || record.matches(pPredicate)) {
				records.add(record);
			}
		}
		return records;
	}

	/**
	 * Get the list of records from a foreign key, an association or a selection node.
	 *
	 * @since 1.0.0
	 * @param pContext   An EBX value context.
	 * @param pPath      The XPath of foreign key, an association or a selection node.
	 * @param pPredicate An XPath predicate to be used as filter. Can be null.
	 * @return All linked records matching the predicate.
	 */
	public static List<Adaptation> getLinkedRecords(final ValueContext pContext, final Path pPath, final Optional<String> pPredicate) {
		List<Adaptation> linkedRecords = new ArrayList<>();
		final SchemaNode node = pContext.getNode(pPath);
		if (node == null) {
			throw new IllegalArgumentException("No node found at path " + pPath.format());
		}
		if (node.getFacetOnTableReference() != null) {
			linkedRecords = node.getFacetOnTableReference().getLinkedRecords(pContext);
		} else if (node.isAssociationNode()) {
			Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext);
			if (record.isPresent()) {
				linkedRecords = getListOfRecordsFromRequestResult(node.getAssociationLink().getAssociationResult(record.get()), pPredicate);
			}
		} else if (node.isSelectNode()) {
			Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext);
			if (record.isPresent()) {
				linkedRecords = getListOfRecordsFromRequestResult(node.getSelectionLink().getSelectionResult(record.get()), pPredicate);
			}
		} else {
			throw new IllegalArgumentException("The specified path must lead to a foreign key, an association or a selection node.");
		}
		return linkedRecords;
	}

	/**
	 * Get a list of records from a request result.
	 *
	 * The instance of RequestResult in parameter is not closed. Its closing must be ensured by the caller.
	 *
	 * @since 1.0.0
	 * @param pResult    A request result
	 * @param pPredicate An XPath predicate to be used as filter. Can be null.
	 * @return The complete list of adaptations referenced by the request result.
	 */
	public static List<Adaptation> getListOfRecordsFromRequestResult(final RequestResult pResult, final Optional<String> pPredicate) {
		List<Adaptation> records = new ArrayList<>();
		Adaptation record = null;
		while ((record = pResult.nextAdaptation()) != null) {
			if (!pPredicate.isPresent() || StringUtils.isBlank(pPredicate.get()) || record.matches(pPredicate.get())) {
				records.add(record);
			}
		}
		pResult.close();
		return records;
	}

	/**
	 * Get a table occurrence from an XPath expression.
	 *
	 * @since 1.0.0
	 * @param pDataset         A dataset in which to look for the table occurrence.
	 * @param pXPathExpression An XPath expression identifying a unique record @see {@link XPathExpressionHelper}.
	 * @return A table occurrence.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 * @throws EBXResourceNotFoundException      If no record has been found.
	 */
	public static Adaptation getRecord(final Adaptation pDataset, final String pXPathExpression) throws EBXResourceNotIdentifiedException, EBXResourceNotFoundException {
		Request request = XPathExpressionHelper.createRequestForXPath(pDataset, pXPathExpression);
		RequestResult result = request.execute();
		try {
			Adaptation record = result.nextAdaptation();
			if (record == null) {
				String dataset = pDataset.getAdaptationName().getStringName();
				String dataspace = pDataset.getHome().getKey().getName();
				throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_RECORD_NOT_FOUND, pXPathExpression, dataset, dataspace);
			} else if (result.nextAdaptation() != null) {
				throw new EBXResourceNotIdentifiedException(EBXCommonsFunctionalReason.RESOURCE_XPATH_EXPRESSION_NOT_UNIQUE);
			}
			return record;
		} finally {
			result.close();
		}
	}

	/**
	 * Get a table occurrence from an XPath expression.
	 *
	 * @since 1.0.0
	 * @param pDataspaceOrSnapshot A dataspace or a snapshot in which to look for a dataset.
	 * @param pDataset             The identifier of the dataset in which to look for the table occurrence. @see {@link AdaptationName}
	 * @param pXPathExpression     An XPath expression identifying a unique record @see {@link XPathExpressionHelper}.
	 * @return A table occurrence.
	 * @throws EBXResourceNotFoundException      If the dataset or the record has not been found.
	 * @throws EBXResourceNotIdentifiedException if the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecord(final AdaptationHome pDataspaceOrSnapshot, final String pDataset, final String pXPathExpression)
			throws EBXResourceNotFoundException, EBXResourceNotIdentifiedException {
		return getRecord(getDataset(pDataspaceOrSnapshot, pDataset), pXPathExpression);
	}

	/**
	 * Get a table occurrence from an XPath expression.
	 *
	 * @since 1.0.0
	 * @author Mickaël Chevalier
	 * @param pRepository      A repository.
	 * @param pDataspace       The identifier of a dataspace @see {@link HomeKey}.
	 * @param pDataset         The identifier of a dataset @see {@link AdaptationName}
	 * @param pXPathExpression An XPath expression identifying a unique record @see {@link XPathExpressionHelper}.
	 * @return A table occurrence.
	 * @throws EBXResourceNotFoundException      If the dataspace, the dataset or the record has not been found.
	 * @throws EBXResourceNotIdentifiedException if the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecord(final Repository pRepository, final String pDataspace, final String pDataset, final String pXPathExpression)
			throws EBXResourceNotFoundException, EBXResourceNotIdentifiedException {
		return getRecord(getDataset(pRepository, pDataspace, pDataset), pXPathExpression);
	}

	/**
	 * Get a table occurrence from an XPath expression.
	 *
	 * @since 1.0.0
	 * @param pContext         An EBX value context.
	 * @param pDataspace       The identifier of a dataspace @see {@link HomeKey}.
	 * @param pDataset         The identifier of a dataset @see {@link AdaptationName}
	 * @param pXPathExpression An XPath expression identifying a unique record @see {@link XPathExpressionHelper}.
	 * @return A table occurrence.
	 * @throws EBXResourceNotFoundException      If the dataspace, the dataset or the record has not been found.
	 * @throws EBXResourceNotIdentifiedException if the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecord(final ValueContext pContext, final Optional<String> pDataspace, final Optional<String> pDataset, final String pXPathExpression)
			throws EBXResourceNotFoundException, EBXResourceNotIdentifiedException {
		Adaptation dataset = getDataset(pContext, pDataspace, pDataset);
		return getRecord(dataset, pXPathExpression);
	}

	/**
	 * Get a table occurrence from a value context.
	 *
	 * @since 1.0.0
	 * @param pValueContext An EBX value context positioned in a table occurrence.
	 * @return A table occurrence.
	 */
	public static Optional<Adaptation> getRecordForValueContext(final ValueContext pValueContext) {
		AdaptationTable table = pValueContext.getAdaptationTable();
		if (table == null) {
			throw new IllegalArgumentException("The value context is not in a table.");
		}
		return Optional.ofNullable(table.lookupAdaptationByPrimaryKey(pValueContext));
	}

	/**
	 * Get the same table occurrence or dataset from the initial snapshot of the dataspace.
	 *
	 * @since 1.0.0
	 * @param pRecordOrDataset A table occurrence or a dataset not in Reference dataspace.
	 * @return A table occurrence or a dataset, empty if it did not exist in the initial version.
	 */
	public static Optional<Adaptation> getRecordFromInitialVersion(final Adaptation pRecordOrDataset) {
		AdaptationHome currentHome = pRecordOrDataset.getHome();
		if (currentHome.isBranchReference()) {
			throw new IllegalArgumentException("Reference branch has no initial version.");
		}
		AdaptationHome initialVersion = currentHome.getParent();
		if (currentHome.isBranch()) {
			// If parent is a branch (dataspace), the current home is then a version. Initial version is then its parent.
			initialVersion = initialVersion.getParent();
		}
		return getRecordFromOtherDataSpaceOrSnapshot(pRecordOrDataset, initialVersion);
	}

	/**
	 * Get the same table occurrence or dataset from a different dataspace or snapshot.
	 *
	 * @since 1.0.0
	 * @param pRecordOrDataset     A table occurrence or a dataset.
	 * @param pDataspaceOrSnapshot A dataspace or a snapshot.
	 * @return A table occurrence or a dataset, empty if it does not exist.
	 */
	public static Optional<Adaptation> getRecordFromOtherDataSpaceOrSnapshot(final Adaptation pRecordOrDataset, final AdaptationHome pDataspaceOrSnapshot) {
		if (pRecordOrDataset.isSchemaInstance()) {
			return Optional.ofNullable(pDataspaceOrSnapshot.findAdaptationOrNull(pRecordOrDataset.getAdaptationName()));
		} else {
			Adaptation dataset = pRecordOrDataset.getContainer();
			Adaptation otherDataSet = pDataspaceOrSnapshot.findAdaptationOrNull(dataset.getAdaptationName());
			if (otherDataSet != null) {
				AdaptationTable table = pRecordOrDataset.getContainerTable();
				AdaptationTable otherTable = otherDataSet.getTable(table.getTablePath());
				if (otherTable != null) {
					return Optional.ofNullable(otherTable.lookupAdaptationByPrimaryKey(pRecordOrDataset.getOccurrencePrimaryKey()));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Get a table.
	 *
	 * @since 1.0.0
	 * @param pDataset   A dataset.
	 * @param pTablePath A path to a table.
	 * @return A table.
	 * @throws EBXResourceNotFoundException If the table has not been found.
	 */
	public static AdaptationTable getTable(final Adaptation pDataset, final Path pTablePath) throws EBXResourceNotFoundException {
		AdaptationTable table = pDataset.getTable(pTablePath);
		if (table == null) {
			String datasetName = pDataset.getAdaptationName().getStringName();
			String dataspaceName = pDataset.getHome().getKey().getName();
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_TABLE_NOT_FOUND, pTablePath.format(), datasetName, dataspaceName);
		}
		return table;
	}

	/**
	 * Get a table.
	 *
	 * @since 1.0.0
	 * @param pDataspaceOrSnapshot A dataspace or a snapshot in which to look for a dataset.
	 * @param pDataset             The identifier of the dataset in which to look for the table occurrence. @see {@link AdaptationName}
	 * @param pTablePath           A path to a table.
	 * @return A table.
	 * @throws EBXResourceNotFoundException If the dataset or the table has not been found.
	 */
	public static AdaptationTable getTable(final AdaptationHome pDataspaceOrSnapshot, final String pDataset, final Path pTablePath) throws EBXResourceNotFoundException {
		return getTable(getDataset(pDataspaceOrSnapshot, pDataset), pTablePath);
	}

	/**
	 * Get a table.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository.
	 * @param pDataspace  The identifier of a dataspace @see {@link HomeKey}.
	 * @param pDataset    The identifier of the dataset in which to look for the table occurrence. @see {@link AdaptationName}
	 * @param pTablePath  A path to a table.
	 * @return A table.
	 * @throws EBXResourceNotFoundException If the dataspace, dataset or table has not been found.
	 */
	public static AdaptationTable getTable(final Repository pRepository, final String pDataspace, final String pDataset, final Path pTablePath) throws EBXResourceNotFoundException {
		return getTable(getDataspace(pRepository, pDataspace), pDataset, pTablePath);
	}

	/**
	 * Get a table from its path and the identifier of its dataspace or snapshot and dataset. These identifier can be null and in this case are resolved from the value context. If no data space name
	 * is specified, the data set will be searched in the current data space. If no data set name is specified, the current data set name will be searched.
	 *
	 * @since 1.0.0
	 * @param pContext   An EBX value context.
	 * @param pDataspace The identifier of a dataspace where to find the data set, null if in current dataspace. @see {@link HomeKey}
	 * @param pDataset   The identifier of a dataset to find, if null current data set name will be searched. @see {@link AdaptationName}
	 * @param pTablePath A path to a table.
	 * @return A table.
	 * @throws EBXResourceNotFoundException If dataspace, dataset or table has not been found.
	 */
	public static AdaptationTable getTable(final ValueContext pContext, final Optional<String> pDataspace, final Optional<String> pDataset, final Path pTablePath) throws EBXResourceNotFoundException {
		return getTable(getDataset(pContext, pDataspace, pDataset), pTablePath);
	}

	/**
	 * Get a request result from an association or a selection node.
	 *
	 * @since 1.0.0
	 * @param pRecordOrDataset A table occurrence or a dataset.
	 * @param pPath            The XPath to an association or a selection node from the first parameter.
	 * @return A request result of linked table occurrences.
	 */
	public static RequestResult requestLinkedRecords(final Adaptation pRecordOrDataset, final Path pPath) {
		final SchemaNode node = SchemaUtils.getNode(pRecordOrDataset, pPath);
		if (node.isAssociationNode()) {
			return node.getAssociationLink().getResult(pRecordOrDataset, null);
		}
		if (node.isSelectNode()) {
			return node.getSelectionLink().getSelectionResult(pRecordOrDataset);
		}
		throw new IllegalArgumentException("The specified path must lead to an association or a selection node.");
	}
}
