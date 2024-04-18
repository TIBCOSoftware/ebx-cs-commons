package com.tibco.ebx.cs.commons.component.constraint;

import java.util.Locale;

import org.apache.commons.validator.routines.checkdigit.IBANCheckDigit;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.InvalidSchemaException;

/**
 * IBAN validity control following ISO ISO 7064
 * 
 * @author Mickaël Chevalier
 */
public class IBANConstraint implements Constraint<String> {

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {

		String iban = pValue;

		iban = iban.replaceAll(" ", "").replaceAll("-", "");

		if (iban.startsWith("IBAN")) {
			iban = iban.substring(4);
		}

		IBANCheckDigit checker = new IBANCheckDigit();

		if (checker.isValid(iban)) {
			pContext.addError("This IBAN code does not appear to be valid.");
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
