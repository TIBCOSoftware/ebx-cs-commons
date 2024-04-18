package com.tibco.ebx.cs.commons.component.function;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ValueFunction;
import com.orchestranetworks.schema.ValueFunctionContext;

/**
 * @author Mickaël Chevalier
 * 
 *         Gets the user who created the record.
 */
public class CreationDateFunction implements ValueFunction {

	/*
	 * @see com.orchestranetworks.schema.ValueFunction#getValue(com.onwbp.adaptation.Adaptation)
	 */
	@Override
	public Object getValue(final Adaptation pRecord) {

		return pRecord.getTimeOfCreation();
	}

	/*
	 * @see com.orchestranetworks.schema.ValueFunction#setup(com.orchestranetworks.schema.ValueFunctionContext)
	 */
	@Override
	public void setup(final ValueFunctionContext pContext) {
		// no implementation
	}

}
