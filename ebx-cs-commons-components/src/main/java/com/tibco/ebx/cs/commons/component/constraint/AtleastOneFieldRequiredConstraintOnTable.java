package com.tibco.ebx.cs.commons.component.constraint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
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

/**
 * Configured with a set of paths, each representing a field node, this constraint will ensure that at least one of the configured fields have a value.
 * 
 * @author Mickaël Chevalier
 */
//TODO Test it especially with empty lists.
public class AtleastOneFieldRequiredConstraintOnTable implements ConstraintOnTableWithRecordLevelCheck {

	private List<SchemaNode> fields;
	private String message;
	private String predicate;

	private String pathsAsString;

	/**
	 * Default constructor
	 */
	public AtleastOneFieldRequiredConstraintOnTable() {
		super();
	}

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
		this.fields.stream().map(e -> "osd:is-null(" + e.getPathInAdaptation().format() + ") and ").forEach(sb::append);
		sb.replace(sb.length() - 5, sb.length() - 1, "");
		return sb.toString();
	}

	@Override
	public String toUserDocumentation(final Locale locale, final ValueContext context) throws InvalidSchemaException {
		return this.message;
	}

	private String createMessage(final Locale locale) {
		StringBuilder sb = new StringBuilder();
		sb.append("At least one of ");
		this.fields.stream().map(e -> e.getLabel(locale) + ", ").forEach(sb::append);
		sb.replace(sb.length() - 3, sb.length() - 1, "");
		sb.append(" must have a value.");
		return sb.toString();
	}

	@Override
	public void checkTable(final ValueContextForValidationOnTable pContext) {
		RequestResult result = pContext.getTable().createRequestResult(this.predicate);
		try {
			Adaptation record = null;
			while ((record = result.nextAdaptation()) != null) {
				pContext.addMessage(record, Messages.getError(this.getClass(), this.message));
			}
		} finally {
			result.close();
		}
	}

	@Override
	public void checkRecord(final ValueContextForValidationOnRecord pContext) {
		List<SchemaNode> fieldsWithValues = new ArrayList<>();
		for (SchemaNode schemaNode : this.fields) {
			Object value = pContext.getRecord().getValue(schemaNode);
			if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
				continue;
			}
			if (value != null) {
				fieldsWithValues.add(schemaNode);
			}
		}
		int size = fieldsWithValues.size();
		if (size == 0) {
			pContext.addMessage(Messages.getError(this.getClass(), this.message));
		}
	}

}
