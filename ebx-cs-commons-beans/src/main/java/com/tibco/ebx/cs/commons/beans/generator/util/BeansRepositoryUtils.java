/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.util;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.HomeCreationSpec;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataModel;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataModelDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataModelsDAO;
import com.tibco.ebx.cs.commons.lib.procedure.CreateDatasetProcedure;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * Utility class to manipulate the repository in regards of dataspace and dataset dedicated to this module.
 *
 * @author Mickaël Chevalier
 * @since 1.1.6
 */
public final class BeansRepositoryUtils {
	public static final AdaptationName DATA_MODELS_DATASET_NAME = AdaptationName.forName("DataModels");
	private static final AdaptationName PERMISSIONS_DATASET_NAME = AdaptationName.forName("Permissions");
	protected static final String MODULE_NAME = "ebx-cs-commons";
	public static final HomeKey COMMONS_DATASPACE_KEY = HomeKey.forBranchName(MODULE_NAME);
	protected static final String DATA_MODELS_MODEL_PATH_IN_MODULE = "/WEB-INF/ebx/schemas/DataModels.xsd";

	/**
	 * Get main dataspace nammed "ebx-cs-commons", default container of configuration datasets for this module.
	 *
	 * @param pRepository the repository
	 *
	 * @return the dataspace nammed "ebx-cs-commons".
	 */
	public static AdaptationHome getCommonsDataspace(final Repository pRepository) {
		return pRepository.lookupHome(COMMONS_DATASPACE_KEY);
	}

	/**
	 * Get dataset nammed "DataModels", container of configurations about data models.
	 *
	 * @param pRepository the repository
	 *
	 * @return the dataset nammed "DatModels" in dataspace ebx-cs-commons.
	 */
	public static Adaptation getDataModelsDataset(final Repository pRepository) {
		AdaptationHome dataspace = getCommonsDataspace(pRepository);
		if (dataspace == null) {
			return null;
		}
		return dataspace.findAdaptationOrNull(DATA_MODELS_DATASET_NAME);
	}

	/**
	 * Get dataset nammed "Permissions", container of configurations about permissions.
	 *
	 * @param pRepository the repository
	 *
	 * @return the dataset nammed "Permissions" in dataspace ebx-cs-commons.
	 */
	public static Adaptation getPermissionsDataset(final Repository pRepository) {
		AdaptationHome dataspace = getCommonsDataspace(pRepository);
		if (dataspace == null) {
			return null;
		}
		return dataspace.findAdaptationOrNull(PERMISSIONS_DATASET_NAME);
	}

	/**
	 * Get or create main dataspace nammed "ebx-cs-commons", default container of configuration datasets for this module.
	 *
	 * @param pRepository the repository
	 * @param pSession    A session having the right to create a dataspace under Reference dataspace.
	 *
	 * @return the dataspace nammed "ebx-cs-commons".
	 */
	private static AdaptationHome getOrCreateCommonsDataspace(final Repository pRepository, final Session pSession) throws OperationException {
		AdaptationHome dataspace = pRepository.lookupHome(COMMONS_DATASPACE_KEY);
		if (dataspace == null) {
			HomeCreationSpec spec = new HomeCreationSpec();
			spec.setParent(pRepository.getReferenceBranch());
			spec.setKey(COMMONS_DATASPACE_KEY);
			spec.setLabel(UserMessage.createInfo("EBX Commons Configuration"));
			spec.setOwner(Profile.ADMINISTRATOR);
			dataspace = pRepository.createHome(spec, pSession);
		}
		return dataspace;
	}

	/**
	 * Get or create dataset nammed "DataModels", container of configurations about data models for this module.
	 *
	 * @param pRepository the repository
	 * @param pSession    A session having the right to create a dataset in dataspace ebx-cs-commons.
	 *
	 * @return the dataset nammed "DatModels" in dataspace ebx-cs-commons.
	 * @throws OperationException OperationException
	 */
	public static Adaptation getOrCreateDataModelsDataset(final Repository pRepository, final Session pSession) throws OperationException {
		AdaptationHome dataspace = getOrCreateCommonsDataspace(pRepository, pSession);
		Adaptation dataset = dataspace.findAdaptationOrNull(DATA_MODELS_DATASET_NAME);
		SchemaLocation schemaLocation = SchemaLocation.forPathInModule(DATA_MODELS_MODEL_PATH_IN_MODULE, MODULE_NAME);
		if (dataset == null) {
			CreateDatasetProcedure procedure = new CreateDatasetProcedure(schemaLocation, DATA_MODELS_DATASET_NAME);
			procedure.executeWithProgrammaticService(dataspace, pSession);
			dataset = procedure.getCreatedDataset();
		}
		return dataset;
	}

	/**
	 * Get a java bean from the record of configuration corresponding to the schema location in parameter
	 *
	 * @param pSchemaLocation the schema location corresponding the data model to be returned.
	 * @param pRepository     the repository
	 *
	 * @return an instance of DataModel corresponding to the schema location.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public static DataModel getDataModel(final Repository pRepository, final SchemaLocation pSchemaLocation) throws BeansTechnicalException {

		String predicate = DataModelDAO.path_to_field_moduleName.format() + " = '" + pSchemaLocation.getModuleName() + "'";
		predicate += " and " + DataModelDAO.path_to_field_schemaLocation.format() + " = '" + SchemaUtils.getSchemaPathInModule(pSchemaLocation) + "'";
		List<Adaptation> result = getDataModelTable(pRepository).selectOccurrences(predicate);
		if (result.size() > 1) {
			throw new BeansTechnicalException("More than one data model correspond to the following schema location : " + pSchemaLocation.format());
		} else if (result.isEmpty()) {
			return null;
		} else {
			return DataModelDAO.getInstance().read(result.get(0));
		}
	}

	private BeansRepositoryUtils() {
		super();
	}

	/**
	 * Get the table of data models configuration
	 *
	 * @param pRepository the repository
	 *
	 * @return the AdaptationTable hosting data models configuration.
	 */
	public static AdaptationTable getDataModelTable(final Repository pRepository) {
		DataModelsDAO dao = new DataModelsDAO(getDataModelsDataset(pRepository));
		return dao.getDataModelTable();
	}
}
