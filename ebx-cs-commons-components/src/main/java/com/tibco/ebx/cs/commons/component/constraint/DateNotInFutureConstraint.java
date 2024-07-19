/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2016. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.constraint;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.SchemaTypeName;

/**
 * Constraint that can be used on a date or dateTime field to ensure that the value of the date is not in the future.
 */
public class DateNotInFutureConstraint implements Constraint<Date> {
	private static final String MESSAGE = "Date must not be in future.";

	@Override
	public void checkOccurrence(final Date value, final ValueContextForValidation valueContext) throws InvalidSchemaException {
		if (value != null) {
			Calendar cal = Calendar.getInstance();
			SchemaTypeName type = valueContext.getNode().getXsTypeName();
			if (SchemaTypeName.XS_DATE.equals(type)) {
				cal = DateUtils.truncate(cal, Calendar.DATE);
			}
			Date now = cal.getTime();
			if (now.before(value)) {
				valueContext.addError(MESSAGE);
			}
		}
	}

	@Override
	public void setup(final ConstraintContext context) {
		SchemaTypeName type = context.getSchemaNode().getXsTypeName();
		if (!SchemaTypeName.XS_DATE.equals(type) || SchemaTypeName.XS_DATETIME.equals(type)) {
			context.addError("Node must be of type Date or DateTime.");
		}
	}

	@Override
	public String toUserDocumentation(final Locale locale, final ValueContext valueContext) throws InvalidSchemaException {
		return MESSAGE;
	}
}
