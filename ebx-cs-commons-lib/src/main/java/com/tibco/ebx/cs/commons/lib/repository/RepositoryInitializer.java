/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.repository;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.HomeCreationSpec;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.procedure.CreateDatasetProcedure;

/**
 * Repository initializer
 * 
 * @author Mickaël Chevalier
 */
public class RepositoryInitializer {

	private String dataSpaceName;

	private String dataSetName;

	private String pathToModelInModule;

	private String moduleName;

	private String dataSpaceLabel;

	private Repository repository;

	/**
	 * Default constructor
	 * 
	 * @param cdataSpaceName       dDataspace name
	 * @param cdataSetName         dataset name
	 * @param cpathToModelInModule path to model
	 * @param cmoduleName          module name
	 * @param cdataSpaceLabel      dataspace label
	 * @param cRepo                repository
	 */
	public RepositoryInitializer(final String cdataSpaceName, final String cdataSetName, final String cpathToModelInModule, final String cmoduleName, final String cdataSpaceLabel,
			final Repository cRepo) {

		dataSpaceName = cdataSpaceName;
		dataSetName = cdataSetName;
		pathToModelInModule = cpathToModelInModule;
		moduleName = cmoduleName;
		dataSpaceLabel = cdataSpaceLabel;
		repository = cRepo;
	}

	/**
	 * Get or create dataspace from a module
	 * 
	 * @param session session
	 * @return dataspace AdaptationHome
	 * @throws OperationException OperationException
	 */
	public AdaptationHome getOrCreateModuleDataspace(final Session session) throws OperationException {

		HomeKey moduleKey = HomeKey.forBranchName(dataSpaceName);
		AdaptationHome dataspace = repository.lookupHome(moduleKey);
		if (dataspace == null) {
			HomeCreationSpec spec = new HomeCreationSpec();
			spec.setParent(repository.getReferenceBranch());
			spec.setKey(moduleKey);
			spec.setLabel(UserMessage.createInfo(dataSpaceLabel));
			spec.setOwner(Profile.ADMINISTRATOR);
			dataspace = repository.createHome(spec, session);
		}
		return dataspace;
	}

	/**
	 * Get or create dataset from a module
	 * 
	 * @param session   session
	 * @param dataspace dataspace
	 * @return dataset Adaptation
	 * @throws OperationException OperationException
	 */
	public Adaptation getOrCreateModuleDataset(final Session session, final AdaptationHome dataspace) throws OperationException {
		AdaptationName dataSetAdaptationName = AdaptationName.forName(this.dataSetName);
		Adaptation dataset = dataspace.findAdaptationOrNull(dataSetAdaptationName);
		SchemaLocation schemaLocation = SchemaLocation.forPathInModule(this.pathToModelInModule, this.moduleName);
		if (dataset == null) {
			CreateDatasetProcedure procedure = new CreateDatasetProcedure(schemaLocation, dataSetAdaptationName);
			procedure.executeWithProgrammaticService(dataspace, session);
			dataset = procedure.getCreatedDataset();
		}
		return dataset;
	}

}