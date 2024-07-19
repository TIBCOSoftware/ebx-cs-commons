/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.query.Query;
import com.orchestranetworks.query.QueryBuilder;
import com.orchestranetworks.query.QueryResult;
import com.orchestranetworks.query.Tuple;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

public class EBXQueryBuilder {

	private Adaptation dataset;
	private Adaptation additionalDataset;
	private int aliasCounter;
	private Map<Path, Pair<String, AdaptationTable>> tableAliases;
	private Map<Path, Pair<String, AdaptationTable>> addTableAliases;
	private AdaptationTable mainTable;

	private StringBuilder queryBuilder;
	private List<String> selectBuilder;
	private List<String> fromBuilder;
	private List<String> joinBuilder;
	private List<Object> parameters;

	private EBXQueryBuilder(Adaptation dataset, Path[] tables) {
		this.queryBuilder = new StringBuilder();
		this.selectBuilder = new ArrayList<>();
		this.fromBuilder = new ArrayList<>();
		this.joinBuilder = new ArrayList<>();
		this.dataset = dataset;
		this.aliasCounter = 0;
		this.parameters = new ArrayList<>();
		this.tableAliases = new HashMap<>();
		this.addTableAliases = new HashMap<>();
		for(Path tablePath : tables) {
			AdaptationTable table = this.dataset.getTable(tablePath);
			if(this.mainTable == null)
				this.mainTable = table;
			tableAliases.put(tablePath, Pair.of("t" + aliasCounter++, table));
		}
	}

	public static EBXQueryBuilder init(Adaptation dataset, Path... tables) {
		if(dataset == null || tables == null) {
			throw new IllegalArgumentException();
		}

		return new EBXQueryBuilder(dataset, tables);
	}

	public EBXQueryBuilder addTables(Adaptation addDataset, Path... addTables) {
		if(addDataset == null || addTables == null) {
			throw new IllegalArgumentException();
		}

		additionalDataset = addDataset;

		for(Path tablePath : addTables) {
			AdaptationTable table = addDataset.getTable(tablePath);
			addTableAliases.put(tablePath, Pair.of("t" + aliasCounter++, table));
		}

		return this;
	}

	public EBXQueryBuilder select(String... fields) {
		if(fields == null) {
			throw new IllegalArgumentException();
		}

		selectBuilder.addAll(Arrays.asList(fields));
		queryBuilder.append("SELECT ");
		queryBuilder.append(String.join(", ", selectBuilder));

		return this;
	}

	public EBXQueryBuilder select(Path... fields) {
		if(fields == null) {
			throw new IllegalArgumentException();
		}

		for(Path field : fields) {
			SchemaNode fieldNode = mainTable.getTableOccurrenceRootNode().getNode(field);
			selectBuilder.add(tableAliases.get(mainTable.getTablePath()).getLeft() + "."
					+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fieldNode.getPathInAdaptation())));
		}

		queryBuilder.append("SELECT ");
		queryBuilder.append(String.join(", ", selectBuilder));

		return this;
	}

	public EBXQueryBuilder from(String... tables) {
		if(tables == null) {
			throw new IllegalArgumentException();
		}

		fromBuilder.addAll(Arrays.asList(tables));
		queryBuilder.append(" FROM ");
		queryBuilder.append(String.join(", ", fromBuilder));

		return this;
	}

	public EBXQueryBuilder from() {
		fromBuilder.add(EBXQueryUtils.wrapDoubleQuotes(mainTable.getTablePath().format()) + " "
				+ tableAliases.get(mainTable.getTablePath()).getLeft());

		queryBuilder.append(" FROM ");
		queryBuilder.append(String.join(", ", fromBuilder));

		return this;
	}

	public EBXQueryBuilder from(Path... tables) {
		if(tables == null) {
			throw new IllegalArgumentException();
		}

		for(Path table : tables) {
			Pair<String, AdaptationTable> pair = tableAliases.get(table);
			if(pair == null) {
				throw new IllegalArgumentException();
			}
			fromBuilder.add(EBXQueryUtils.wrapDoubleQuotes(table.format()) + " " + pair.getLeft());
		}

		queryBuilder.append(" FROM ");
		queryBuilder.append(String.join(", ", fromBuilder));

		return this;
	}

	public EBXQueryBuilder join(String... tables) {
		if(tables == null) {
			throw new IllegalArgumentException();
		}

		joinBuilder.addAll(Arrays.asList(tables));
		queryBuilder.append(" JOIN ");
		queryBuilder.append(String.join(", ", joinBuilder));

		return this;
	}

	public EBXQueryBuilder join(Path... tables) {
		if(tables == null) {
			throw new IllegalArgumentException();
		}

		for(Path table : tables) {
			Pair<String, AdaptationTable> pair = tableAliases.get(table);
			boolean add = false;
			if(pair == null) {
				pair = addTableAliases.get(table);
				if(pair == null) {
					throw new IllegalArgumentException();
				}
				add = true;
			}
			if(add)
				joinBuilder.add(EBXQueryUtils.wrapDoubleQuotes(additionalDataset.getAdaptationName().getStringName())
						+ "." + EBXQueryUtils.wrapDoubleQuotes(table.format()) + " " + pair.getLeft());
			else
				joinBuilder.add(EBXQueryUtils.wrapDoubleQuotes(table.format()) + " " + pair.getLeft());
		}

		queryBuilder.append(" JOIN ");
		queryBuilder.append(String.join(", ", joinBuilder));

		return this;
	}

	public EBXQueryBuilder leftJoin(String table) {
		if(table == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" LEFT JOIN ");
		queryBuilder.append(table);

		return this;
	}

	public EBXQueryBuilder rightJoin(String table) {
		if(table == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" RIGHT JOIN ");
		queryBuilder.append(table);

		return this;
	}

	public EBXQueryBuilder where(String predicate) {
		if(predicate == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" WHERE (");
		queryBuilder.append(predicate);
		queryBuilder.append(")");

		return this;
	}

	public EBXQueryBuilder on(String predicate) {
		if(predicate == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" ON (");
		queryBuilder.append(predicate);
		queryBuilder.append(")");

		return this;
	}

	public EBXQueryBuilder orderBy(boolean isAscending, String... columns) {
		if(columns == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" ORDER BY ");
		queryBuilder.append(String.join(", ", columns));
		queryBuilder.append(isAscending ? " ASC " : " DESC ");

		return this;
	}

	public EBXQueryBuilder orderBy(String... columns) {
		return orderBy(true, columns);
	}

	public EBXQueryBuilder orderBy(boolean isAscending, Path... columns) {
		List<String> columnsString = new ArrayList<>();
		for(Path column : columns) {
			SchemaNode columnNode = mainTable.getTableOccurrenceRootNode().getNode(column);
			columnsString.add(tableAliases.get(mainTable.getTablePath()).getLeft() + "."
					+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(columnNode.getPathInAdaptation())));
		}

		return orderBy(isAscending, columnsString.toArray(new String[0]));
	}

	public EBXQueryBuilder orderBy(Path... columns) {
		List<String> columnsString = new ArrayList<>();
		for(Path column : columns) {
			SchemaNode columnNode = mainTable.getTableOccurrenceRootNode().getNode(column);
			columnsString.add(tableAliases.get(mainTable.getTablePath()).getLeft() + "."
					+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(columnNode.getPathInAdaptation())));
		}

		return orderBy(columnsString.toArray(new String[0]));
	}

	public EBXQueryBuilder limit(int count) {
		if(count < 0) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" LIMIT ");
		queryBuilder.append(count);

		return this;
	}

	public EBXQueryBuilder union(EBXQueryBuilder ebxQueryBuilder) {
		queryBuilder.append(" UNION ");
		queryBuilder.append(ebxQueryBuilder.toString());

		return this;
	}

	public EBXQueryBuilder selectPrimaryKey() {
		return select(tableAliases.get(mainTable.getTablePath()).getLeft() + "."
				+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.PRIMARY_KEY_SQL_FIELD));
	}

	public EBXQueryBuilder selectAdaptation() {
		return select(tableAliases.get(mainTable.getTablePath()).getLeft() + "."
				+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.ADAPTATION_SQL_FIELD));
	}

	public EBXQueryBuilder where(EBXQueryCondition condition) {
		if(condition == null) {
			throw new IllegalArgumentException();
		}

		queryBuilder.append(" WHERE (");
		queryBuilder.append(getCondition(condition));
		queryBuilder.append(")");

		return this;
	}

	private String getCondition(EBXQueryCondition condition) {
		StringBuilder conditionBuilder = new StringBuilder();
		if(!condition.isRoot()) {
			if(condition.getFieldString() != null) {
				if(condition.getFieldString().contains("AliasFor")) {
					String processed = condition.getFieldString();
					for(Entry<Path, Pair<String, AdaptationTable>> path : tableAliases.entrySet()) {
						processed = processed.replace("AliasFor".concat(path.getKey().format()),
								path.getValue().getLeft());
					}
					conditionBuilder.append(processed);
				} else
					conditionBuilder.append(condition.getFieldString());
			} else {
				Path conditionTable;
				if(condition.getTable() != null)
					conditionTable = condition.getTable();
				else
					conditionTable = mainTable.getTablePath();

				if(condition.getFieldPath() != null) {
					Pair<String, AdaptationTable> pair = tableAliases.get(conditionTable);
					if(pair == null) {
						throw new IllegalArgumentException();
					}
					SchemaNode fieldNode = pair.getRight().getTableOccurrenceRootNode()
							.getNode(condition.getFieldPath());
					conditionBuilder.append(pair.getLeft() + "."
							+ EBXQueryUtils.wrapDoubleQuotes(EBXQueryUtils.fromPath(fieldNode.getPathInAdaptation())));
				}
			}

			conditionBuilder.append(" ").append(condition.getOperator());
			if(condition.getValue() != null) {
				conditionBuilder.append(" ");
				if(StringUtils.equals(condition.getOperator(), "IN"))
					conditionBuilder.append(condition.getValue());
				else {
					conditionBuilder.append("?");
					parameters.add(condition.getValue());
				}
			}
		}
		if(condition.getLinkedConditions() != null) {
			for(Pair<String, EBXQueryCondition> linked : condition.getLinkedConditions()) {
				if(!StringUtils.equals(linked.getLeft(), "ROOT"))
					conditionBuilder.append(" ").append(linked.getLeft()).append(" ");

				conditionBuilder.append(getCondition(linked.getRight()));
			}
		}
		return "(" + conditionBuilder.toString() + ")";
	}

	public QueryResult<Tuple> execute() {
		QueryBuilder builder = dataset.createQueryBuilder();
		if(additionalDataset != null) {
			builder.addDataset(additionalDataset.getAdaptationName().getStringName(), additionalDataset);
		}
		Query<Tuple> query = builder.build(toString());
		for(int i = 0; i < parameters.size(); i++) {
			query.setParameter(i, parameters.get(i));
		}
		try(QueryResult<Tuple> result = query.getResult()) {
			return result;
		}
	}

	@Override
	public String toString() {
		return queryBuilder.toString();
	}

}
