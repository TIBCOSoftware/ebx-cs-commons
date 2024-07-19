/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.function;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

/**
 * @author Mickaël Chevalier
 * 
 *         Gets the id of the user who last modifies the record.
 */
public class LastUpdateUserFunction implements ValueFunction {

	/*
	 * @see com.orchestranetworks.schema.ValueFunction#getValue(com.onwbp.adaptation.Adaptation)
	 */
	@Override
	public Object getValue(final Adaptation pRecord) {
		return pRecord.getLastUser();
	}

	/*
	 * @see com.orchestranetworks.schema.ValueFunction#setup(com.orchestranetworks.schema.ValueFunctionContext)
	 */
	@Override
	public void setup(final ValueFunctionContext arg0) {
		// no implementation
	}

}
