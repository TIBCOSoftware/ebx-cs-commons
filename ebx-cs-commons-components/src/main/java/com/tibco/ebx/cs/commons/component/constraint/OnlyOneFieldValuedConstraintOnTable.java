/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.RequestSortCriteria;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidationOnRecord;
import com.orchestranetworks.instance.ValueContextForValidationOnTable;
import com.orchestranetworks.schema.ConstraintContextOnTable;
import com.orchestranetworks.schema.ConstraintOnTableWithRecordLevelCheck;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.PathUtils;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * Configured with a set of paths, each representing a field node, this constraint will ensure that at least one of the configured fields have a value.
 * 
 * @author Mickaël Chevalier
 */
//TODO Test it especially with empty lists.
public class OnlyOneFieldValuedConstraintOnTable implements ConstraintOnTableWithRecordLevelCheck {

	private List<SchemaNode> fields;
	private String message;
	private String predicate;

	private String pathsAsString;

	public String getPathsAsString() {
		return this.pathsAsString;
	}

	public void setPathsAsString(final String pathsAsString) {
		this.pathsAsString = pathsAsString;
	}

	@Override
	public void setup(final ConstraintContextOnTable context) {
		if (this.fields == null && this.pathsAsString != null) {
			List<Path> pathList = PathUtils.convertStringToPathList(this.pathsAsString, null);
			this.fields = new ArrayList<>();
			for (Path path : pathList) {
				SchemaNode fieldNode = PathUtils.setupFieldNode(context, path, null, false);
				if (fieldNode != null) {
					this.fields.add(fieldNode);
					context.addDependencyToInsertDeleteAndModify(fieldNode);
				}
			}
		}
		if (this.fields == null || this.fields.isEmpty()) {
			context.addError("At least one field must be specified for this constraint.");
		}

		this.message = this.createMessage(Locale.getDefault());
		this.predicate = this.createPredicate();
	}

	private String createPredicate() {
		StringBuilder sb = new StringBuilder();
		this.fields.stream().map(e -> "osd:is-not-null(" + e.getPathInAdaptation().format() + ") or ").forEach(sb::append);
		sb.replace(sb.length() - 5, sb.length() - 1, "");
		return sb.toString();
	}

	@Override
	public String toUserDocumentation(final Locale locale, final ValueContext context) throws InvalidSchemaException {
		return this.message;
	}

	private String createMessage(final Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append("Only one of ");
		this.fields.stream().map(e -> e.getLabel(locale) + ", ").forEach(sb::append);
		sb.replace(sb.length() - 3, sb.length() - 1, "");
		sb.append(" must have a value.");
		return sb.toString();
	}

	@Override
	public void checkTable(final ValueContextForValidationOnTable pContext) {
		RequestSortCriteria sort = new RequestSortCriteria();
		for (SchemaNode node : this.fields) {
			sort.add(node.getPathInAdaptation(), false);
		}

		RequestResult result = pContext.getTable().createRequestResult(this.predicate);
		try {
			Adaptation record = null;
			while ((record = result.nextAdaptation()) != null) {
				int count = 0;
				for (SchemaNode node : this.fields) {
					if (SchemaUtils.isNodeValued(record, node)) {
						count++;
					}
				}
				if (count > 1) {
					pContext.addMessage(record, Messages.getError(this.getClass(), this.message));
				}
				if (count == 0) {
					break;
				}
			}
		} finally {
			result.close();
		}
	}

	@Override
	public void checkRecord(final ValueContextForValidationOnRecord pContext) {
		int count = 0;

		for (SchemaNode node : this.fields) {
			if (SchemaUtils.isNodeValued(pContext.getRecord(), node)) {
				count++;
			}
		}
		if (count > 1) {
			pContext.addMessage(Messages.getError(this.getClass(), this.message));
		}
	}
}
