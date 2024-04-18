package com.tibco.ebx.cs.commons.lib.procedure;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationReference;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.Profile;

/**
 * Create a dataset from a SchemaLocation with an AdaptationName
 * 
 * @author Mickaël Chevalier
 */
public class CreateDatasetProcedure extends GenericProcedure {
	private Adaptation createdDataset = null;
	private final SchemaLocation schemaLocation;
	private final AdaptationName adaptationName;

	/**
	 * Instantiate a new procedure.
	 *
	 * @param pSchemaLocation A schema location
	 * @param pAdaptationName The adaptation name of the dataset to create.
	 */
	public CreateDatasetProcedure(final SchemaLocation pSchemaLocation, final AdaptationName pAdaptationName) {
		this.schemaLocation = pSchemaLocation;
		this.adaptationName = pAdaptationName;
	}

	@Override
	/**
	 * @see com.orchestranetworks.ps.procedure.GenericProcedure#doExecute(com.orchestranetworks.service.ProcedureContext)
	 */
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		AdaptationReference reference = AdaptationReference.forPersistentName(this.adaptationName.getStringName());
		this.createdDataset = pContext.doCreateRoot(this.schemaLocation, reference, Profile.ADMINISTRATOR);
	}

	/**
	 * @return the created dataset. Null before the execution of the procedure.
	 */
	public Adaptation getCreatedDataset() {
		return this.createdDataset;
	}

}
