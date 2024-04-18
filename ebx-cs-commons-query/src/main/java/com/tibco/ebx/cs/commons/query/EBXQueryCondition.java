package com.tibco.ebx.cs.commons.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.orchestranetworks.schema.Path;

public class EBXQueryCondition {

	private Path table;
	private Path fieldPath;
	private String fieldString;
	private String operator;
	private Object value;

	private List<Pair<String, EBXQueryCondition>> linkedConditions;

	private boolean isRoot = false;

	public static EBXQueryCondition init() {
		return new EBXQueryCondition();
	}

	private EBXQueryCondition() {
		this.isRoot = true;
	}

	public EBXQueryCondition setCondition(EBXQueryCondition condition) {
		if(linkedConditions == null)
			linkedConditions = new ArrayList<>();

		linkedConditions.add(Pair.of("ROOT", condition));
		return this;
	}

	public EBXQueryCondition(EBXQueryCondition toCopy) {
		this.table = toCopy.getTable();
		this.fieldPath = toCopy.getFieldPath();
		this.fieldString = toCopy.getFieldString();
		this.operator = toCopy.getOperator();
		this.value = toCopy.getValue();
		if(toCopy.getLinkedConditions() != null) {
			this.linkedConditions = new ArrayList<>();
			for(Pair<String, EBXQueryCondition> pair : toCopy.getLinkedConditions())
				this.linkedConditions.add(Pair.of(pair.getLeft(), new EBXQueryCondition(pair.getRight())));
		}
	}

	public EBXQueryCondition(Path table, Path field, String operator, Object value) {
		this.table = table;
		this.fieldPath = field;
		this.operator = operator;
		this.value = value;
	}

	public EBXQueryCondition(Path field, String operator, Object value) {
		this.fieldPath = field;
		this.operator = operator;
		this.value = value;
	}

	public EBXQueryCondition(String field, String operator, Object value) {
		this.fieldString = field;
		this.operator = operator;
		this.value = value;
	}

	public EBXQueryCondition and(EBXQueryCondition condition) {
		if(linkedConditions == null)
			linkedConditions = new ArrayList<>();

		linkedConditions.add(Pair.of("AND", condition));
		return this;
	}

	public EBXQueryCondition or(EBXQueryCondition condition) {
		if(linkedConditions == null)
			linkedConditions = new ArrayList<>();

		linkedConditions.add(Pair.of("OR", condition));
		return this;
	}

	public Path getTable() {
		return table;
	}

	public void setTable(Path table) {
		this.table = table;
	}

	public Path getFieldPath() {
		return fieldPath;
	}

	public void setFieldPath(Path fieldPath) {
		this.fieldPath = fieldPath;
	}

	public String getFieldString() {
		return fieldString;
	}

	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<Pair<String, EBXQueryCondition>> getLinkedConditions() {
		return linkedConditions;
	}

	public void setLinkedConditions(List<Pair<String, EBXQueryCondition>> linkedConditions) {
		this.linkedConditions = linkedConditions;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public boolean isRoot() {
		return isRoot;
	}

}
