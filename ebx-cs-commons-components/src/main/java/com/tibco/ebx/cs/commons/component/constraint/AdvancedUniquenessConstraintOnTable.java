/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.RequestSortCriteria;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidationOnRecord;
import com.orchestranetworks.instance.ValueContextForValidationOnTable;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.ConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

/**
 * 
 *
 * <osd:constraint class="com.orchestranetworks.ps.constraint.ontable.UniquenessOnFilteredTable"> <uniqueFields>./field1,./field2</uniqueFields> <filter>an xpath filter</filter> </osd:constraint>
 * 
 * @author Mickaël Chevalier
 */
public class AdvancedUniquenessConstraintOnTable implements ConstraintOnTableWithRecordLevelCheck {
	private String uniqueFields;
	private String filter;
	private List<SchemaNode> uniqueNodes;
	private RequestSortCriteria criteria;
	private String fields;

	/**
	 * Default constructor
	 */
	public AdvancedUniquenessConstraintOnTable() {
		super();
	}

	@Override
	public void checkRecord(final ValueContextForValidationOnRecord pContext) {
		StringBuilder predicate = new StringBuilder();
		StringBuilder valueInMessage = new StringBuilder();
		StringBuilder fieldsInMessage = new StringBuilder();
		String value = null;
		SchemaNode node = null;

		String[] uniqueFieldsPaths = this.uniqueFields.split(",");
		predicate.append(this.filter + " and ");
		for (String path : uniqueFieldsPaths) {
			node = pContext.getRecord().getNode(Path.parse(path));
			value = node.formatToXsString(pContext.getRecord().getValue(Path.parse(path)));
			valueInMessage.append(value + " - ");
			fieldsInMessage.append(path + " - ");
			predicate.append(path + " = '" + value + "'");
			predicate.append(" and ");
		}
		predicate.delete(predicate.length() - 5, predicate.length());
		valueInMessage.delete(valueInMessage.length() - 3, valueInMessage.length());
		fieldsInMessage.delete(fieldsInMessage.length() - 3, fieldsInMessage.length());

		RequestResult result = pContext.getTable().createRequestResult(predicate.toString());
		try {
			if (result.isSizeGreaterOrEqual(2)) {
				if (uniqueFieldsPaths.length == 1) {
					String message = "[" + fieldsInMessage + "] value '" + value + "' must be unique in the table.";
					pContext.addMessage(node, UserMessage.createError(message));
				} else {
					String message = "Set of values [" + valueInMessage + "] from fields [" + fieldsInMessage + "] must be unique in the table.";
					pContext.addMessage(UserMessage.createError(message));
				}
			}
		} finally {
			result.close();
		}
	}

	@Override
	public void checkTable(final ValueContextForValidationOnTable pContext) {

		Request request = pContext.getTable().createRequest();
		request.setXPathFilter(this.filter);
		request.setSortCriteria(this.criteria);
		RequestResult result = request.execute();

		Adaptation previousRecord = null;
		Adaptation record = result.nextAdaptation();
		List<String> previousValues = null;
		Set<Adaptation> duplicates = new HashSet<>();
		List<String> values = null;

		try {
			while (record != null) {

				values = new ArrayList<>();
				for (SchemaNode node : this.uniqueNodes) {
					values.add(node.formatToXsString(record.get(node.getPathInAdaptation())));
				}

				if (previousRecord != null) {
					if (values.equals(previousValues)) {
						duplicates.add(previousRecord);
						duplicates.add(record);
					} else {
						this.addMessageToDuplicates(pContext, duplicates, values);
						duplicates = new HashSet<>();
					}
				}

				previousRecord = record;
				previousValues = values;

				record = result.nextAdaptation();
				if (record == null) {
					this.addMessageToDuplicates(pContext, duplicates, values);
				}
			}
			this.addMessageToDuplicates(pContext, duplicates, values);
		} finally {
			result.close();
		}
	}

	private StringBuilder addMessageToDuplicates(final ValueContextForValidationOnTable pContext, final Set<Adaptation> pDuplicates, final List<String> pValues) {
		StringBuilder valuesInMessage;
		valuesInMessage = new StringBuilder();
		for (String value : pValues) {
			valuesInMessage.append(value + " - ");
		}
		valuesInMessage.delete(valuesInMessage.length() - 3, valuesInMessage.length());
		for (Adaptation duplicate : pDuplicates) {
			if (pValues.size() == 1) {
				String message = "[" + this.fields + "] value '" + valuesInMessage + "' must be unique in the table.";
				pContext.addMessage(duplicate, this.uniqueNodes.get(0), UserMessage.createError(message));
			} else {
				String message = "Set of values [" + valuesInMessage + "] from fields [" + this.fields + "] must be unique in the table.";
				pContext.addMessage(duplicate, UserMessage.createError(message));
			}

		}
		return valuesInMessage;
	}

	public String getFilter() {
		return this.filter;
	}

	public String getUniqueFields() {
		return this.uniqueFields;
	}

	public void setFilter(final String filter) {
		this.filter = filter;
	}

	public void setUniqueFields(final String uniqueFields) {
		this.uniqueFields = uniqueFields;
	}

	@Override
	public void setup(final ConstraintContextOnTable pContext) {
		this.criteria = new RequestSortCriteria();
		this.uniqueNodes = new ArrayList<>();
		StringBuilder fieldsInMessage = new StringBuilder();
		String[] uniqueFieldsPaths = this.uniqueFields.split(",");
		for (String uniqueFieldPath : uniqueFieldsPaths) {
			Path path = Path.SELF.add(Path.parse(uniqueFieldPath));
			this.uniqueNodes.add(pContext.getSchemaNode().getNode(path));
			this.criteria.add(path);
			fieldsInMessage.append(path.getLastStep().format() + " - ");
		}
		fieldsInMessage.delete(fieldsInMessage.length() - 3, fieldsInMessage.length());
		this.fields = fieldsInMessage.toString();
	}

	@Override
	public String toUserDocumentation(final Locale arg0, final ValueContext arg1) throws InvalidSchemaException {
		return null;
	}

}
