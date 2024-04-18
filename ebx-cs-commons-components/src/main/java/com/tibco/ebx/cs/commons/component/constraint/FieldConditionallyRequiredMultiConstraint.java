package com.tibco.ebx.cs.commons.component.constraint;

import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintOnNull;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.utils.PathUtils;

/**
 * Configured with sibling field paths and a sibling field values and compare operators, this constraint on null will check, when the sibling fields compare to the specified values, that this field
 * (on which the constraint is declared) requires a value. If the mere existence of a sibling value makes this field required, set the otherFieldValue to "&lt;not-null&gt;".
 * 
 * @author Mickaël Chevalier
 * 
 * @param <T> Type of the field
 */
public class FieldConditionallyRequiredMultiConstraint<T> implements Constraint<T>, ConstraintOnNull {
	private static String NOTNULL = "<not-null>";

	private static enum Op {
		EQUALS("is equal to"), NOT_EQUALS("is not equal to"), STARTS_WITH("starts with", true), ENDS_WITH("ends with", true), CONTAINS("contains", true), GREATER_THAN("is greater than"),
		GREATER_THAN_EQ("is greater than or equal to"), LESS_THAN("is less than"), LESS_THAN_EQ("is less than or equal to");

		private final String text;
		private final boolean stringoper;

		private Op(final String text) {
			this(text, false);
		}

		private Op(final String text, final boolean stringoper) {
			this.text = text;
			this.stringoper = stringoper;
		}

	}

	private String pathsAsString;
	private String valuesAsString;
	private String opsAsString;
	private String message;

	private SchemaNode[] otherFieldNodes;
	@SuppressWarnings("rawtypes")
	private Comparable[] otherFieldValues;
	private Op[] operators;

	/**
	 * Default constructor
	 */
	public FieldConditionallyRequiredMultiConstraint() {
		super();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void checkNull(final ValueContextForValidation context) throws InvalidSchemaException {
		// 'and' together the component conditions so we can fast break if a
		// non-match
		for (int i = 0; i < otherFieldNodes.length; i++) {
			Object value = context.getValue(otherFieldNodes[i]);
			Comparable otherFieldValue = otherFieldValues[i];
			Op op = operators[i];
			if (!matches(value, otherFieldValue, op)) {
				return;
			}
		}
		context.addError(message);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static boolean matches(final Object value, final Comparable otherFieldValue, final Op op) {
		if (value == null) {
			return false;
		}
		if (NOTNULL.equals(otherFieldValue) && Op.EQUALS == op) {
			return true;
		}
		if (op.stringoper) {
			String stringValue = String.valueOf(value);
			switch (op) {
			case STARTS_WITH:
				return stringValue.startsWith((String) otherFieldValue);
			case ENDS_WITH:
				return stringValue.endsWith((String) otherFieldValue);
			case CONTAINS:
				return stringValue.contains((String) otherFieldValue);
			default:
				break;
			}
		} else {
			switch (op) {
			case EQUALS:
				return otherFieldValue.equals(value);
			case NOT_EQUALS:
				return !otherFieldValue.equals(value);
			case GREATER_THAN:
				return otherFieldValue.compareTo(value) < 0;
			case GREATER_THAN_EQ:
				return otherFieldValue.compareTo(value) <= 0;
			case LESS_THAN:
				return otherFieldValue.compareTo(value) > 0;
			case LESS_THAN_EQ:
				return otherFieldValue.compareTo(value) >= 0;
			default:
				break;
			}
		}
		return false;
	}

	@Override
	public void checkOccurrence(final T value, final ValueContextForValidation context) throws InvalidSchemaException {
		// nothing to do
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setup(final ConstraintContext context) {
		if (pathsAsString == null) {
			context.addError("Conditionally required field constraint requires paths to other fields in the record");
		}
		List<Path> paths = PathUtils.convertStringToPathList(pathsAsString, null);
		int size = paths.size();
		otherFieldNodes = new SchemaNode[size];
		operators = new Op[size];
		otherFieldValues = new Comparable[size];
		String[] otherFieldStrings = valuesAsString.split(";");
		if (size != otherFieldStrings.length) {
			context.addError("Should have exactly one value for each path specified");
			return;
		}
		String[] opStrings = null;
		if (opsAsString != null) {
			opStrings = opsAsString.split(";");
			if (size != opStrings.length) {
				context.addError("Should have exactly one operator for each path specified");
				return;
			}
		}
		SchemaNode rootNode = context.getSchemaNode().getTableNode();
		for (int i = 0; i < size; i++) {
			Path path = paths.get(i);
			SchemaNode node = PathUtils.setupFieldNode(context, rootNode, path, "otherField", true, true);
			if (node == null) {
				return;
			}
			otherFieldNodes[i] = node;
			String otherFieldString = otherFieldStrings[i];
			if (NOTNULL.equals(otherFieldString)) {
				otherFieldValues[i] = otherFieldString;
			} else {
				otherFieldValues[i] = (Comparable) node.parseXsString(otherFieldString);
			}
			if (opStrings == null) {
				operators[i] = Op.EQUALS;
			} else {
				operators[i] = Op.valueOf(opStrings[i]);
			}
		}
		Locale locale = Locale.getDefault();
		String prefix = context.getSchemaNode().getLabel(locale) + " is required ";
		if (message == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(prefix);
			sb.append(" when ");
			for (int i = 0; i < otherFieldNodes.length; i++) {
				SchemaNode schemaNode = otherFieldNodes[i];
				if (i > 0) {
					sb.append(" and ");
				}
				sb.append(schemaNode.getLabel(locale));
				String otherValue = otherFieldStrings[i];
				if (NOTNULL.equals(otherValue)) {
					sb.append(" is not null");
				} else {
					sb.append(" ").append(operators[i].text).append(" ").append(otherValue);
				}
			}
			message = sb.toString();
		} else if (!message.startsWith(prefix)) {
			message = prefix + message;
		}
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return message;
	}

	public String getPathsAsString() {
		return pathsAsString;
	}

	public void setPathsAsString(final String pathsAsString) {
		this.pathsAsString = pathsAsString;
	}

	public String getValuesAsString() {
		return valuesAsString;
	}

	public void setValuesAsString(final String valuesAsString) {
		this.valuesAsString = valuesAsString;
	}

	public String getOpsAsString() {
		return opsAsString;
	}

	public void setOpsAsString(final String opsAsString) {
		this.opsAsString = opsAsString;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}

}
