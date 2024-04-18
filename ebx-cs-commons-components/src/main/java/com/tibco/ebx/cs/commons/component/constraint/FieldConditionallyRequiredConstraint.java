package com.tibco.ebx.cs.commons.component.constraint;

import java.text.MessageFormat;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.Severity;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintOnNull;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * Configured with a sibling field path and a sibling field value, this constraint on null will check, when the sibling field has the specified value, that this field (on which the constraint is
 * declared) requires a value. If the existence of the sibling value makes this field required, set the otherFieldValue to "&lt;not-null&gt;".
 * <p>
 * This can be considered as "not allowed" constraint when <code>mode</code> is set to <code>NOT_ALLOWED</code>. This can be considered as "Required else not allowed" constraint when <code>mode</code>
 * is set to <code>REQUIRED_ELSE_NOT_ALLOWED</code>.
 * <p>
 * If the otherFieldPath specified is a foreign key field and otherFieldForeignFieldPath is specified, then the value will be pulled from that field in the linked record instead.
 * <p>
 * This constraint can be ignored when the the passOver Path and Value are configured within the UI.
 *
 * <severity>F=Fatal, E=Error (default), W=Warning, I=Information</severity>
 * 
 * @author Mickaël Chevalier
 * @param <T> Type of the field
 */
public class FieldConditionallyRequiredConstraint<T> implements Constraint<T>, ConstraintOnNull {
	private String severity = Severity.ERROR.toParsableString();

	private static final String MODE_REQUIRED = "required";
	private static final String MODE_REQUIRED_ELSE_NOT_ALLOWED = "required else not allowed";
	private static final String MODE_NOT_ALLOWED = "not allowed";

	private static final String HELP_MESSAGE = "{0} {1} when {2} is {3}.";
	private static final String EBX_MESSAGE = "Field {0} is {1}.";
	private static final String IGNORE_CONSTRAINT_MESSAGE = " This constraint will be ignored when {0} contains the value {1}.";
	private static final String NOTNULL = "<not-null>";

	private Path otherFieldPath;
	private Path otherFieldForeignFieldPath;
	private SchemaNode otherFieldNode;
	private String otherFieldValue;
	private String mode = MODE_REQUIRED;

	// Note: Prefixed these data members with "passOver" so that they would be displayed
	// under the otherField parameters in UI. Would have preferred the prefix "ignore".
	private Path passOverConstraintFieldPath;
	private SchemaNode passOverConstraintFieldNode;
	private String passOverConstratintFieldValue;

	private boolean useEBXmessage;
	private String customMessage;

	private String message;
	private String helpMessage;

	/**
	 * Default constructor
	 */
	public FieldConditionallyRequiredConstraint() {
		super();
	}

	/**
	 * @return the passOverConstraintFieldPath
	 */
	public Path getPassOverConstraintFieldPath() {
		return this.passOverConstraintFieldPath;
	}

	/**
	 * @param passOverConstraintFieldPath the passOverConstraintFieldPath to set
	 */
	public void setPassOverConstraintFieldPath(final Path passOverConstraintFieldPath) {
		this.passOverConstraintFieldPath = passOverConstraintFieldPath;
	}

	/**
	 * @return the passOverConstratintFieldValue
	 */
	public String getPassOverConstratintFieldValue() {
		return this.passOverConstratintFieldValue;
	}

	/**
	 * @param passOverConstratintFieldValue the passOverConstratintFieldValue to set
	 */
	public void setPassOverConstratintFieldValue(final String passOverConstratintFieldValue) {
		this.passOverConstratintFieldValue = passOverConstratintFieldValue;
	}

	public Path getOtherFieldPath() {
		return this.otherFieldPath;
	}

	public void setOtherFieldPath(final Path otherFieldPath) {
		this.otherFieldPath = otherFieldPath;
	}

	public Path getOtherFieldForeignFieldPath() {
		return this.otherFieldForeignFieldPath;
	}

	public void setOtherFieldForeignFieldPath(final Path otherFieldForeignFieldPath) {
		this.otherFieldForeignFieldPath = otherFieldForeignFieldPath;
	}

	public String getOtherFieldValue() {
		return this.otherFieldValue;
	}

	public void setOtherFieldValue(final String otherFieldValue) {
		this.otherFieldValue = otherFieldValue;
	}

	public String getMode() {
		return this.mode;
	}

	public void setMode(final String mode) {
		this.mode = mode;
	}

	@Override
	public void checkNull(final ValueContextForValidation context) throws InvalidSchemaException {
		if (MODE_REQUIRED.equals(this.mode) || MODE_REQUIRED_ELSE_NOT_ALLOWED.equals(this.mode)) {
			this.checkOtherValue(context, true);
		}
	}

	@Override
	public void checkOccurrence(final T value, final ValueContextForValidation context) throws InvalidSchemaException {
		if (MODE_NOT_ALLOWED.equals(this.mode)) {
			this.checkOtherValue(context, true);
		} else if (MODE_REQUIRED_ELSE_NOT_ALLOWED.equals(this.mode)) {
			this.checkOtherValue(context, false);
		}
	}

	private void checkOtherValue(final ValueContextForValidation context, final boolean errorIfConditionIsTrue) {
		if (this.passOverConstraintFieldNode != null) {
			Object passOverConstraintValue = context.getValue(this.passOverConstraintFieldNode);
			if (this.passOverConstratintFieldValue.equals(passOverConstraintValue)) {
				return; // Pass Over value matches criteria so ignore constraint.
			}
		}

		boolean conditionIsTrue = this.conditionIsTrue(context);
		if (conditionIsTrue && errorIfConditionIsTrue || !conditionIsTrue && !errorIfConditionIsTrue) {
			context.addMessage(Messages.createUserMessage(this.message, Severity.parseFlag(this.severity)));
		}
	}

	private boolean conditionIsTrue(final ValueContextForValidation context) {
		Object value = this.getValueOfOtherFieldNode(context);
		if (value == null) {
			if (this.otherFieldValue == null) {
				return true;
			}
		} else {
			if (NOTNULL.equals(this.otherFieldValue) || String.valueOf(value).equals(this.otherFieldValue)) {
				return true;
			}
		}
		return false;
	}

	// This gets the value of the other field from the given context.
	// When otherFieldForeignFieldPath is specified, it follows the otherFieldPath foreign key and
	// pulls the value from the foreign record.
	// Otherwise, it simply returns the value of the otherFieldPath.
	private Object getValueOfOtherFieldNode(final ValueContext context) {
		if (this.otherFieldForeignFieldPath == null) {
			return context.getValue(this.otherFieldNode);
		}
		Adaptation linkedRecord = this.otherFieldNode.getFacetOnTableReference().getLinkedRecord(context);
		return linkedRecord == null ? null : linkedRecord.get(this.otherFieldForeignFieldPath);
	}

	@Override
	public void setup(final ConstraintContext context) {
		if (this.otherFieldPath == null) {
			context.addError("Conditionally required field constraint requires a path to another field in the record");
		} else {
			this.otherFieldNode = this.otherFieldPath.startsWith(Path.PARENT) ? context.getSchemaNode().getNode(this.otherFieldPath)
					: context.getSchemaNode().getTableNode().getTableOccurrenceRootNode().getNode(this.otherFieldPath);
			if (this.otherFieldNode == null) {
				context.addError(this.otherFieldPath.format() + " not found");
			} else {
				if (this.otherFieldForeignFieldPath != null) {
					SchemaFacetTableRef tableRef = this.otherFieldNode.getFacetOnTableReference();
					if (tableRef == null) {
						context.addError("otherFieldForeignFieldPath can only be specified when otherFieldPath is a foreign key.");
					} else {
						SchemaNode otherFieldForeignFieldNode = tableRef.getTableNode().getNode(this.otherFieldForeignFieldPath);
						if (otherFieldForeignFieldNode == null) {
							context.addError(this.otherFieldForeignFieldPath.format() + " not found");
						}
					}
				}

				this.helpMessage = MessageFormat.format(HELP_MESSAGE, context.getSchemaNode().getLabel(Locale.getDefault()), this.mode, this.otherFieldNode.getLabel(Locale.getDefault()),
						this.otherFieldValue == null ? "not specified" : this.otherFieldValue);
				if (this.customMessage != null) {
					this.message = this.customMessage;
				} else if (this.useEBXmessage) {
					this.message = MessageFormat.format(EBX_MESSAGE, "'" + context.getSchemaNode().getLabel(Locale.getDefault()) + "'", this.mode);
				} else {
					this.message = this.helpMessage;
				}

				context.addDependencyToInsertDeleteAndModify(this.otherFieldNode);
			}
		}

		if (this.mode == null) {
			context.addError("mode is required");
		} else if (!(MODE_REQUIRED.equals(this.mode) || MODE_NOT_ALLOWED.equals(this.mode) || MODE_REQUIRED_ELSE_NOT_ALLOWED.equals(this.mode))) {
			context.addError("mode " + this.mode + " is not valid. Must be '" + MODE_REQUIRED + "' or '" + MODE_NOT_ALLOWED + "' or '" + MODE_REQUIRED_ELSE_NOT_ALLOWED + "'");
		}

		this.setupPassOverConstraintWhen(context);
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return this.helpMessage;
	}

	/**
	 * Configure this constraint to be ignored if the passOver data members are populated. If the passOver members are configured then the associated message is also updated.
	 *
	 * @param context
	 */
	private void setupPassOverConstraintWhen(final ConstraintContext context) {
		if (this.passOverConstraintFieldPath == null) {
			return;
		}
		this.passOverConstraintFieldNode = this.passOverConstraintFieldPath.startsWith(Path.PARENT) ? context.getSchemaNode().getNode(this.passOverConstraintFieldPath)
				: context.getSchemaNode().getTableNode().getTableOccurrenceRootNode().getNode(this.passOverConstraintFieldPath);
		if (this.passOverConstraintFieldNode == null) {
			context.addError(this.passOverConstraintFieldPath.format() + " not found");
			return;
		}
		if (this.passOverConstratintFieldValue == null) {
			this.passOverConstratintFieldValue = String.valueOf(null);
		}

		String passOverMessage = MessageFormat.format(IGNORE_CONSTRAINT_MESSAGE, this.passOverConstraintFieldNode.getLabel(Locale.getDefault()), this.passOverConstratintFieldValue);

		this.helpMessage += passOverMessage;
	}

	public boolean isUseEBXmessage() {
		return this.useEBXmessage;
	}

	public void setUseEBXmessage(final boolean useEBXmessage) {
		this.useEBXmessage = useEBXmessage;
	}

	public String getCustomMessage() {
		return this.customMessage;
	}

	public void setCustomMessage(final String customMessage) {
		this.customMessage = customMessage;
	}

	public String getSeverity() {
		return this.severity;
	}

	public void setSeverity(final String severity) {
		this.severity = severity;
	}

}
