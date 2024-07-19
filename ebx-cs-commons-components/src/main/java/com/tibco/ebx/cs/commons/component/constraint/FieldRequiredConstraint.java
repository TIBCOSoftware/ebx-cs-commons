/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.constraint;

import java.text.MessageFormat;
import java.util.Locale;

import com.onwbp.base.text.Severity;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintOnNull;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * This constraint on null will check if this field (on which the constraint is declared) has a value.
 *
 * Since setting a field as minOccurs=1 in the Data Modeling UI is will always result as an Error, this constraint is necessary when you want a required field to give a Warning or Info message
 * <severity>F=Fatal, E=Error (default), W=Warning, I=Information</severity>
 * 
 * @author Mickaël Chevalier
 *
 * @param <T> Type of the field
 */
public class FieldRequiredConstraint<T> implements Constraint<T>, ConstraintOnNull {

	private String severity = Severity.ERROR.toParsableString();

	private static final String EBX_MESSAGE = "Field {0} is required.";
	private String message;

	/**
	 * Constructor
	 */
	public FieldRequiredConstraint() {
		super();
	}

	@Override
	public void checkNull(final ValueContextForValidation context) throws InvalidSchemaException {
		context.addMessage(Messages.createUserMessage(message, Severity.parseFlag(severity)));
	}

	@Override
	public void checkOccurrence(final T value, final ValueContextForValidation context) throws InvalidSchemaException {
		// nothing to implement
	}

	@Override
	public void setup(final ConstraintContext context) {
		message = MessageFormat.format(EBX_MESSAGE, "'" + context.getSchemaNode().getLabel(Locale.getDefault()) + "'");
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return message;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(final String severity) {
		this.severity = severity;
	}

}
