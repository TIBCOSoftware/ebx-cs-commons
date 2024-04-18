package com.tibco.ebx.cs.commons.component.constraint;

import java.util.Locale;

import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * EAN validity control
 * 
 * @author Mickaël Chevalier
 */
public class EAN13Constraint implements Constraint<String> {

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		String ean = pValue;
		EAN13CheckDigit checker = new EAN13CheckDigit();
		if (checker.isValid(ean)) {
			pContext.addError("This EAN code does not appear to be valid.");
		}
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		pContext.setDependencyToLocalNode();
	}

	@Override
	public String toUserDocumentation(final Locale local, final ValueContext valueContext) throws InvalidSchemaException {
		return null;
	}

}
