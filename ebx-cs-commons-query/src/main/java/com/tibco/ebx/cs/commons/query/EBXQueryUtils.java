package com.tibco.ebx.cs.commons.query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.XPathExpressionHelper;
import com.onwbp.adaptation.xpath.XPathPredicateParser;
import com.onwbp.org.apache.commons.lang3.StringUtils;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.query.QueryResult;
import com.orchestranetworks.query.Tuple;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.PathAccessException;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.Step;
import com.orchestranetworks.schema.info.AssociationLink;
import com.orchestranetworks.schema.info.AssociationLinkByLinkTable;
import com.orchestranetworks.schema.info.AssociationLinkByTableRefInverse;
import com.orchestranetworks.schema.info.AssociationLinkByXPathLink;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;

public class EBXQueryUtils {

	private EBXQueryUtils() {
		// hide public constructor
	}

	public static final String PRIMARY_KEY_SQL_FIELD = "$pk";

	public static final String ADAPTATION_SQL_FIELD = "$adaptation";

	private static final String RESOLVER_PATTERN = "\\$\\{([^}]+)\\}";

	public static String fromPath(Path param) {
		List<String> steps = new ArrayList<>();
		for(Step step : param.getSteps()) {
			if(!step.isRelative()) {
				String stepString = step.format();
				if(!StringUtils.equals("root", stepString))
					steps.add(step.format());
			}
		}
		return StringUtils.join(steps, "\".\"");
	}

	public static String[] fromPaths(Path[] params) {
		return Stream.of(params).map(EBXQueryUtils::fromPath).collect(Collectors.toList()).toArray(new String[0]);
	}

	public static Tuple lookupFirstResultMatchingQuery(EBXQueryBuilder queryBuilder) {
		try(QueryResult<Tuple> result = queryBuilder.execute()) {
			Iterator<Tuple> iterator = result.iterator();
			if(iterator.hasNext())
				return iterator.next();
			return null;
		}
	}

	public static Adaptation lookupFirstRecordMatchingQuery(EBXQueryBuilder queryBuilder) {
		Tuple result = lookupFirstResultMatchingQuery(queryBuilder);
		if(result != null && result.get(0) instanceof Adaptation)
			return (Adaptation) result.get(0);

		return null;
	}

	public static Tuple lookupResultByPrimaryKey(Adaptation dataset, Path table, String key) {
		return buildPrimaryKeyQuery(dataset, table, key);
	}

	public static Adaptation lookupAdaptationByPrimaryKey(Adaptation dataset, Path table, String key) {
		Tuple result = buildPrimaryKeyQuery(dataset, table, key);
		if(result != null)
			return (Adaptation) result.get(0);

		return null;
	}

	public static Tuple lookupResultByPrimaryKey(Adaptation dataset, Path table, PrimaryKey key) {
		return buildPrimaryKeyQuery(dataset, table, key.format());
	}

	public static Adaptation lookupAdaptationByPrimaryKey(Adaptation dataset, Path table, PrimaryKey key) {
		Tuple result = buildPrimaryKeyQuery(dataset, table, key.format());
		if(result != null)
			return (Adaptation) result.get(0);

		return null;
	}

	public static Tuple buildPrimaryKeyQuery(Adaptation dataset, Path table, String key) {
		EBXQueryBuilder builder = EBXQueryBuilder.init(dataset, table).selectAdaptation().from(table)
				.where(new EBXQueryCondition(PRIMARY_KEY_SQL_FIELD, "=", key)).limit(1);
		return lookupFirstResultMatchingQuery(builder);
	}

	public static List<Tuple> getLinkedResults(Adaptation pRecord, Path pPath) {
		final SchemaNode node = getNode(pRecord, pPath);
		if(node.getFacetOnTableReference() != null) {
			SchemaFacetTableRef schemaRef = node.getFacetOnTableReference();
			AdaptationTable targetTable = schemaRef.getTable(pRecord.createValueContext());
			EBXQueryCondition condition = buildTableRefCondition(pRecord, pPath, node);
			if(condition != null) {
				EBXQueryBuilder builder = EBXQueryBuilder
						.init(targetTable.getContainerAdaptation(), targetTable.getTablePath()).selectAdaptation()
						.from().where(condition);
				if(node.getMaxOccurs() <= 1) {
					builder = builder.limit(1);
				}
				QueryResult<Tuple> result = builder.execute();
				return getLinkedResultsFromQuery(result);
			}
			return Collections.emptyList();
		} else if(node.isAssociationNode() || node.isSelectNode()) {
			AssociationLink link = node.getAssociationLink();
			if(link.isTableRefInverse()) {
				QueryResult<Tuple> result = buildAssociationCondition(pRecord, link);
				if(result != null)
					return getLinkedResultsFromQuery(result);
			} else if(link.isXPathLink()) {
				QueryResult<Tuple> result = buildAssociationConditionXpath(pRecord, link);
				if(result != null)
					return getLinkedResultsFromQuery(result);
			} else if(link.isLinkTable()) {
				QueryResult<Tuple> result = buildAssociationRefLink(pRecord, link);
				if(result != null)
					return getLinkedResultsFromQuery(result);
			}
			return Collections.emptyList();
		} else {
			throw new IllegalArgumentException(
					"The specified path must lead to a foreign key, an association or a selection node.");
		}
	}

	public static List<Tuple> getLinkedResults(ValueContext pContext, Path pPath) {
		final SchemaNode node = pContext.getNode(pPath);
		if(node.getFacetOnTableReference() != null) {
			SchemaFacetTableRef schemaRef = node.getFacetOnTableReference();
			AdaptationTable targetTable = schemaRef.getTable(pContext);
			EBXQueryCondition condition = buildTableRefCondition(pContext, pPath, node);
			if(condition != null) {
				EBXQueryBuilder builder = EBXQueryBuilder
						.init(targetTable.getContainerAdaptation(), targetTable.getTablePath()).selectAdaptation()
						.from().where(condition);
				if(node.getMaxOccurs() <= 1) {
					builder = builder.limit(1);
				}
				QueryResult<Tuple> result = builder.execute();
				return getLinkedResultsFromQuery(result);
			}
			return Collections.emptyList();
		} else if(node.isAssociationNode() || node.isSelectNode()) {
			AssociationLink link = node.getAssociationLink();
			if(link.isTableRefInverse()) {
				QueryResult<Tuple> result = buildAssociationCondition(pContext, link);
				if(result != null)
					return getLinkedResultsFromQuery(result);
			} else if(link.isXPathLink()) {
				QueryResult<Tuple> result = buildAssociationConditionXpath(pContext, link);
				if(result != null)
					return getLinkedResultsFromQuery(result);
			}
			return Collections.emptyList();
		} else {
			throw new IllegalArgumentException(
					"The specified path must lead to a foreign key, an association or a selection node.");
		}
	}

	public static List<Tuple> getLinkedResultsFromQuery(QueryResult<Tuple> result) {
		List<Tuple> linkedResults = new ArrayList<>();
		for(Tuple t : result) {
			linkedResults.add(t);
		}
		return linkedResults;
	}

	public static List<Adaptation> getLinkedRecords(Adaptation pRecord, Path pPath, Optional<String> pPredicate) {
		final SchemaNode node = getNode(pRecord, pPath);
		if(node.getFacetOnTableReference() != null) {
			SchemaFacetTableRef schemaRef = node.getFacetOnTableReference();
			AdaptationTable targetTable = schemaRef.getTable(pRecord.createValueContext());
			EBXQueryCondition condition = buildTableRefCondition(pRecord, pPath, node);
			if(condition != null) {
				EBXQueryBuilder builder = EBXQueryBuilder
						.init(targetTable.getContainerAdaptation(), targetTable.getTablePath()).selectAdaptation()
						.from().where(condition);
				if(node.getMaxOccurs() <= 1) {
					builder = builder.limit(1);
				}
				QueryResult<Tuple> result = builder.execute();
				return getLinkedRecordsFromQuery(pPredicate, result);
			}
			return Collections.emptyList();
		} else if(node.isAssociationNode() || node.isSelectNode()) {
			AssociationLink link = node.getAssociationLink();
			if(link.isTableRefInverse()) {
				QueryResult<Tuple> result = buildAssociationCondition(pRecord, link);
				if(result != null)
					return getLinkedRecordsFromQuery(pPredicate, result);
			} else if(link.isXPathLink()) {
				QueryResult<Tuple> result = buildAssociationConditionXpath(pRecord, link);
				if(result != null)
					return getLinkedRecordsFromQuery(pPredicate, result);
			} else if(link.isLinkTable()) {
				QueryResult<Tuple> result = buildAssociationRefLink(pRecord, link);
				if(result != null)
					return getLinkedRecordsFromQuery(pPredicate, result);
			}
			return Collections.emptyList();
		} else {
			throw new IllegalArgumentException(
					"The specified path must lead to a foreign key, an association or a selection node.");
		}
	}

	public static List<Adaptation> getLinkedRecords(ValueContext pContext, Path pPath, Optional<String> pPredicate) {
		final SchemaNode node = pContext.getNode(pPath);
		if(node.getFacetOnTableReference() != null) {
			SchemaFacetTableRef schemaRef = node.getFacetOnTableReference();
			AdaptationTable targetTable = schemaRef.getTable(pContext);
			EBXQueryCondition condition = buildTableRefCondition(pContext, pPath, node);
			if(condition != null) {
				EBXQueryBuilder builder = EBXQueryBuilder
						.init(targetTable.getContainerAdaptation(), targetTable.getTablePath()).selectAdaptation()
						.from().where(condition);
				if(node.getMaxOccurs() <= 1) {
					builder = builder.limit(1);
				}
				QueryResult<Tuple> result = builder.execute();
				return getLinkedRecordsFromQuery(pPredicate, result);
			}
			return Collections.emptyList();
		} else if(node.isAssociationNode() || node.isSelectNode()) {
			AssociationLink link = node.getAssociationLink();
			if(link.isTableRefInverse()) {
				QueryResult<Tuple> result = buildAssociationCondition(pContext, link);
				if(result != null)
					return getLinkedRecordsFromQuery(pPredicate, result);
			} else if(link.isXPathLink()) {
				QueryResult<Tuple> result = buildAssociationConditionXpath(pContext, link);
				if(result != null)
					return getLinkedRecordsFromQuery(pPredicate, result);
			}
			return Collections.emptyList();
		} else {
			throw new IllegalArgumentException(
					"The specified path must lead to a foreign key, an association or a selection node.");
		}
	}

	public static List<Adaptation> getLinkedRecordsFromQuery(Optional<String> pPredicate, QueryResult<Tuple> result) {
		List<Adaptation> linkedRecords = new ArrayList<>();
		for(Tuple t : result) {
			linkedRecords.add((Adaptation) t.get(0));
		}
		if(pPredicate.isPresent()) {
			linkedRecords = filterListOfRecordOrDataset(linkedRecords, pPredicate.get());
		}
		return linkedRecords;
	}

	public static QueryResult<Tuple> buildAssociationRefLink(Adaptation pRecord, AssociationLink link) {
		AssociationLinkByLinkTable refLinkTable = (AssociationLinkByLinkTable) link;
		AdaptationTable linkTable = refLinkTable.getLinkTable(pRecord.getContainer());
		AdaptationTable targetTable = refLinkTable.getTargetTable(pRecord.getContainer());
		EBXQueryCondition condition = new EBXQueryCondition(
				getFkAsString(pRecord.getContainerTable().getTablePath(), linkTable.getTablePath(),
						Path.SELF.add(refLinkTable.getFieldToSourcePath().getLastStep())),
				"=", pRecord.getOccurrencePrimaryKey().format());
		EBXQueryBuilder builder = EBXQueryBuilder
				.init(targetTable.getContainerAdaptation(), targetTable.getTablePath(), linkTable.getTablePath())
				.selectAdaptation().from().join(linkTable.getTablePath())
				.on("t0.\"$pk\" = " + EBXQueryUtils.getFkAsString(targetTable.getTablePath(), linkTable.getTablePath(),
						"t1", Path.SELF.add(refLinkTable.getFieldToTargetPath().getLastStep())))
				.where(condition);

		return builder.execute();
	}

	public static QueryResult<Tuple> buildAssociationCondition(Adaptation pRecord, AssociationLink link) {
		AssociationLinkByTableRefInverse refInvLink = (AssociationLinkByTableRefInverse) link;
		AdaptationTable targetTable = refInvLink.getTargetTable(pRecord.getContainer());
		EBXQueryCondition condition = new EBXQueryCondition(getFkAsString(pRecord.getContainerTable().getTablePath(),
				targetTable.getTablePath(), refInvLink.getFieldToSourcePath()), "=",
				pRecord.getOccurrencePrimaryKey().format());
		EBXQueryBuilder builder = EBXQueryBuilder.init(targetTable.getContainerAdaptation(), targetTable.getTablePath())
				.selectAdaptation().from().where(condition);
		return builder.execute();
	}

	public static QueryResult<Tuple> buildAssociationCondition(ValueContext pContext, AssociationLink link) {
		Optional<Adaptation> pRecordOpt = getRecordForValueContext(pContext);
		if(!pRecordOpt.isPresent())
			return null;

		Adaptation pRecord = pRecordOpt.get();
		AssociationLinkByTableRefInverse refInvLink = (AssociationLinkByTableRefInverse) link;
		AdaptationTable targetTable = refInvLink.getTargetTable(pRecord.getContainer());
		EBXQueryCondition condition = new EBXQueryCondition(getFkAsString(pRecord.getContainerTable().getTablePath(),
				targetTable.getTablePath(), refInvLink.getFieldToSourcePath()), "=",
				pRecord.getOccurrencePrimaryKey().format());
		EBXQueryBuilder builder = EBXQueryBuilder.init(targetTable.getContainerAdaptation(), targetTable.getTablePath())
				.selectAdaptation().from().where(condition);
		return builder.execute();
	}

	public static QueryResult<Tuple> buildAssociationConditionXpath(Adaptation pRecord, AssociationLink link) {
		AssociationLinkByXPathLink xPathLink = (AssociationLinkByXPathLink) link;
		AdaptationTable targetTable = xPathLink.getTargetTable(pRecord.getContainer());
		String xPath = XPathExpressionHelper.getPredicateForXPath(xPathLink.getPredicate());
		List<String> paths = XPathPredicateParser.getAtomicPaths(xPath);
		List<String> values = XPathPredicateParser.getAtomicValues(xPath);
		EBXQueryCondition condition = getXPathCondition(pRecord, paths, values);
		EBXQueryBuilder builder = EBXQueryBuilder.init(targetTable.getContainerAdaptation(), targetTable.getTablePath())
				.selectAdaptation().from().where(condition);
		return builder.execute();
	}

	public static QueryResult<Tuple> buildAssociationConditionXpath(ValueContext pContext, AssociationLink link) {
		Optional<Adaptation> pRecordOpt = getRecordForValueContext(pContext);
		if(!pRecordOpt.isPresent())
			return null;

		Adaptation pRecord = pRecordOpt.get();
		AssociationLinkByXPathLink xPathLink = (AssociationLinkByXPathLink) link;
		AdaptationTable targetTable = xPathLink.getTargetTable(pRecord.getContainer());
		String xPath = XPathExpressionHelper.getPredicateForXPath(xPathLink.getPredicate());
		List<String> paths = XPathPredicateParser.getAtomicPaths(xPath);
		List<String> values = XPathPredicateParser.getAtomicValues(xPath);
		EBXQueryCondition condition = getXPathCondition(pRecord, paths, values);
		EBXQueryBuilder builder = EBXQueryBuilder.init(targetTable.getContainerAdaptation(), targetTable.getTablePath())
				.selectAdaptation().from().where(condition);
		return builder.execute();
	}

	public static EBXQueryCondition getXPathCondition(Adaptation pRecord, List<String> paths, List<String> values) {
		EBXQueryCondition condition = null;
		for(int i = 0; i < paths.size(); i++) {
			String path = paths.get(i);
			String value = values.get(i);
			Pattern labelPattern = Pattern.compile(RESOLVER_PATTERN);
			Matcher m = labelPattern.matcher(value);
			if(m.find()) {
				String pathString = m.group(1);
				Object valueObject = pRecord.get(Path.SELF.add(Path.parse(pathString).getLastStep()));
				if(valueObject != null) {
					if(condition == null)
						condition = new EBXQueryCondition(Path.parse(path), "=", valueObject);
					else
						condition = condition.and(new EBXQueryCondition(Path.parse(path), "=", valueObject));
				}
			}
		}

		return condition;
	}

	public static EBXQueryCondition buildTableRefCondition(Adaptation pRecord, Path pPath, final SchemaNode node) {
		if(node.getMaxOccurs() > 1) {
			List<String> valuesList = pRecord.getList(pPath);
			if(valuesList != null && !valuesList.isEmpty()) {
				String values = valuesList.stream().reduce("", (r, e) -> r = r + wrapSingleQuotes(e) + ", ");
				values = "(" + values.substring(0, values.length() - 2) + ")";
				return new EBXQueryCondition(PRIMARY_KEY_SQL_FIELD, "IN", values);
			}
		} else {
			String value = pRecord.getString(pPath);
			if(StringUtils.isNotBlank(value))
				return new EBXQueryCondition(PRIMARY_KEY_SQL_FIELD, "=", value);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static EBXQueryCondition buildTableRefCondition(ValueContext pContext, Path pPath, final SchemaNode node) {
		if(node.getMaxOccurs() > 1) {
			List<String> valuesList = (List<String>) pContext.getValue(pPath);
			if(valuesList != null && !valuesList.isEmpty()) {
				String values = valuesList.stream().reduce("", (r, e) -> r = r + wrapSingleQuotes(e) + ", ");
				values = "(" + values.substring(0, values.length() - 2) + ")";
				return new EBXQueryCondition(PRIMARY_KEY_SQL_FIELD, "IN", values);
			}
		} else {
			String value = (String) pContext.getValue(pPath);
			if(StringUtils.isNotBlank(value))
				return new EBXQueryCondition(PRIMARY_KEY_SQL_FIELD, "=", value);
		}

		return null;
	}

	public static String wrapSingleQuotes(String string) {
		return StringUtils.wrap(string, '\'');
	}

	public static String wrapDoubleQuotes(String string) {
		return StringUtils.wrap(string, '"');
	}

	public static String[] wrapDoubleQuotes(String[] strings) {
		return Stream.of(strings).map(EBXQueryUtils::wrapDoubleQuotes).collect(Collectors.toList())
				.toArray(new String[0]);
	}

	public static String getFkAsString(String fkField) {
		return "FK_AS_STRING(" + fkField + ")";
	}

	public static String getFkAsString(Path fkFieldPath) {
		return getFkAsString(EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkFieldPath)));
	}

	public static String getFkAsString(String datasetAlias, Path linkedTablePath, String fkField) {
		return "FK_AS_STRING('" + datasetAlias + "', '" + linkedTablePath.format() + "', " + fkField + ")";
	}

	public static String getFkAsString(Path linkedTablePath, String fkField) {
		return getFkAsString("_public", linkedTablePath, fkField);
	}

	public static String getFkAsString(Adaptation dataset, Path linkedTablePath, String fkField) {
		return getFkAsString(dataset.getAdaptationName().getStringName(), linkedTablePath, fkField);
	}

	public static String getFkAsString(Adaptation dataset, Path linkedTablePath, Path fkTablePath, Path fkFieldPath) {
		return getFkAsString(dataset.getAdaptationName().getStringName(), linkedTablePath,
				getConnectedFkField(fkTablePath, fkFieldPath));
	}

	public static String getFkAsString(Path linkedTablePath, Path fkTablePath, Path fkFieldPath) {
		return getFkAsString(linkedTablePath, getConnectedFkField(fkTablePath, fkFieldPath));
	}

	public static String getFkAsString(Path linkedTablePath, Path fkTablePath, String alias, Path fkFieldPath) {
		return getFkAsString(linkedTablePath, getConnectedFkField(fkTablePath, alias, fkFieldPath));
	}

	public static String getFkAsString(Path linkedTablePath, Path fkFieldPath) {
		return getFkAsString(linkedTablePath, EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkFieldPath)));
	}

	public static String getFkAsString(String dataset, Path linkedTablePath, Path fkTablePath, Path fkFieldPath) {
		return getFkAsString(dataset, linkedTablePath, getConnectedFkField(fkTablePath, fkFieldPath));
	}

	private static String getConnectedFkField(Path fkTablePath, Path fkFieldPath) {
		String table = EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkTablePath));
		String field = EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkFieldPath));
		field = StringUtils.replace(field, table, "");
		return "AliasFor".concat(fkTablePath.format()).concat(".").concat(field).replace("..", ".");
	}

	private static String getConnectedFkField(Path fkTablePath, String alias, Path fkFieldPath) {
		String table = EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkTablePath));
		String field = EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fkFieldPath));
		field = StringUtils.replace(field, table, "");
		return alias.concat(".").concat(field).replace("..", ".");
	}

	public static SchemaNode getNode(final Adaptation pRecordOrDataset, Path pPath) {
		if(pPath.format().startsWith("/")) {
			pPath = Path.SELF.add(pPath);
		}
		SchemaNode node = pRecordOrDataset.getSchemaNode().getNode(pPath);
		if(node == null) {
			throw new PathAccessException(pPath, "Path '" + pPath.format() + "' not found in '"
					+ pRecordOrDataset.getLabel(Locale.getDefault()) + "'");
		}
		return node;
	}

	public static SchemaNode getNode(final ValueContext pValueContext, Path pPath) {
		if(pPath.format().startsWith("/")) {
			pPath = Path.SELF.add(pPath);
		}
		SchemaNode node = pValueContext.getNode().getNode(pPath);
		if(node == null) {
			throw new PathAccessException(pPath, "Path '" + pPath.format() + "' not found");
		}
		return node;
	}

	public static List<Adaptation> filterListOfRecordOrDataset(final List<Adaptation> pRecordsOrDatasets,
			final String pPredicate) {
		List<Adaptation> records = new ArrayList<>();
		for(Adaptation record : pRecordsOrDatasets) {
			if(StringUtils.isBlank(pPredicate) || record.matches(pPredicate)) {
				records.add(record);
			}
		}
		return records;
	}

	public static Optional<Adaptation> getRecordForValueContext(final ValueContext pValueContext) {
		AdaptationTable table = pValueContext.getAdaptationTable();
		if(table == null) {
			throw new IllegalArgumentException("The value context is not in a table.");
		}
		return Optional.ofNullable(table.lookupAdaptationByPrimaryKey(pValueContext));
	}

}
