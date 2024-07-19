/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.utils;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.interactions.InteractionHelper.ParametersMap;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.DataContextReadOnly;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsFunctionalReason;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;
import com.tibco.ebx.cs.commons.lib.exception.WorkflowConfigurationException;
import com.tibco.ebx.cs.commons.lib.workflow.WorkflowConstants;

/**
 * Utility class to operate on workflows normalizing workflow data context @see {@link WorkflowConstants}.
 *
 * @since 1.0.0
 * @author Mickaël Chevalier
 */
public final class WorkflowUtils {

	private WorkflowUtils() {
		super();
	}

	/**
	 * From a record creation user task, get the created table occurrence from internal parameters.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository
	 * @param pSession    An EBX user session.
	 * @return The table occurrence just created based on internal parameters
	 * @throws WorkflowConfigurationException    If interaction internal parameter {@value WorkflowConstants#PARAM_CREATED} is not found or not valued.
	 * @throws EBXResourceNotFoundException      If the dataspace, dataset or record is not found or if the session is not in a workflow.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 */
	public static Adaptation getCreatedRecordInWorkspaceFromInteraction(final Repository pRepository, final Session pSession)
			throws EBXResourceNotFoundException, WorkflowConfigurationException, EBXResourceNotIdentifiedException {
		Adaptation dataset = getDatasetInWorkspaceFromInteraction(pRepository, pSession);
		String created = WorkflowUtils.getInteractionInternalParameter(pSession, WorkflowConstants.PARAM_CREATED);
		return AdaptationUtils.getRecord(dataset, created);
	}

	/**
	 * Get a dataset in initial dataspace from data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext A process instance data context.
	 * @param pRepository  A repository.
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException   If the dataspace or the dataset has not been found.
	 * @throws WorkflowConfigurationException If the data context variable {@value WorkflowConstants#VAR_DATASET} is not found.
	 */
	public static Adaptation getDatasetFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		AdaptationHome dataspace = getDataspaceFromDataContext(pDataContext, pRepository);
		String dataset = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_DATASET);
		return AdaptationUtils.getDataset(dataspace, dataset);
	}

	/**
	 * Get the dataset from the session interaction.
	 *
	 * @since 1.0.0
	 * @author Mickaël Chevalier
	 * @param pRepository A repository.
	 * @param pSession    A session.
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException   If the dataspace or dataset has not been found or if the session in not in workflow.
	 * @throws WorkflowConfigurationException If interaction input parameter {@value WorkflowConstants#PARAM_INSTANCE} is not found or not valued.
	 */
	public static Adaptation getDatasetFromInteraction(final Repository pRepository, final Session pSession) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		AdaptationHome dataspace = getDataspaceFromInteraction(pRepository, pSession);
		String dataset = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_INSTANCE);
		return AdaptationUtils.getDataset(dataspace, dataset);
	}

	/**
	 * Get the dataset in workspace from data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext A process instance data context.
	 * @param pRepository  A repository.
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException   If the workspace or the dataset has not been found.
	 * @throws WorkflowConfigurationException If the data context variable {@value WorkflowConstants#VAR_DATASET} is not found.
	 */
	public static Adaptation getDatasetInWorkspaceFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository)
			throws EBXResourceNotFoundException, WorkflowConfigurationException {
		AdaptationHome workspace = getWorkspaceFromDataContext(pDataContext, pRepository);
		String dataset = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_DATASET);
		return AdaptationUtils.getDataset(workspace, dataset);
	}

	/**
	 * Get the dataset in workspace from the session interaction.
	 *
	 * @since 1.0.0
	 * @author Mickaël Chevalier
	 * @param pRepository A repository.
	 * @param pSession    A session.
	 * @return A dataset.
	 * @throws EBXResourceNotFoundException   If the dataspace or dataset has not been found or if the session in not in workflow.
	 * @throws WorkflowConfigurationException If interaction input parameter {@value WorkflowConstants#PARAM_INSTANCE} is not found or not valued.
	 */
	public static Adaptation getDatasetInWorkspaceFromInteraction(final Repository pRepository, final Session pSession) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		AdaptationHome workspace = getDataspaceFromInteraction(pRepository, pSession);
		String dataset = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_INSTANCE);
		return AdaptationUtils.getDataset(workspace, dataset);
	}

	/**
	 * Get the initial dataspace from data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext The process data context.
	 * @param pRepository  A repository.
	 * @return A dataspace.
	 * @throws EBXResourceNotFoundException   If the dataspace has not been found.
	 * @throws WorkflowConfigurationException If the data context variable {@value WorkflowConstants#VAR_DATASPACE} is not found.
	 */
	public static AdaptationHome getDataspaceFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		String dataspace = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_DATASPACE);
		return AdaptationUtils.getDataspace(pRepository, dataspace);
	}

	/**
	 * Get initial dataspace from interaction.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository.
	 * @param pSession    An EBX user session.
	 * @return A dataspace.
	 * @throws EBXResourceNotFoundException   If the dataspace has not been found or if the session in not in workflow.
	 * @throws WorkflowConfigurationException If interaction input parameter {@value WorkflowConstants#PARAM_BRANCH} is not found or not valued.
	 */
	public static AdaptationHome getDataspaceFromInteraction(final Repository pRepository, final Session pSession) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		String dataspace = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_BRANCH);
		return AdaptationUtils.getDataspace(pRepository, dataspace);
	}

	/**
	 * Get an input parameter value from the interaction.
	 *
	 * @since 1.0.0
	 * @author Mickaël Chevalier
	 * @param pSession       An EBX user session
	 * @param pParameterName A parameters name
	 * @return The parameters value
	 * @throws EBXResourceNotFoundException   If the session in not in workflow.
	 * @throws WorkflowConfigurationException if no value or blank value found.
	 */
	public static String getInteractionInputParameter(final Session pSession, final String pParameterName) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		SessionInteraction interaction = pSession.getInteraction(true);
		if (interaction == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_INTERACTION_NOT_FOUND);
		}

		ParametersMap inputParameters = interaction.getInputParameters();
		if (inputParameters != null) {
			String value = inputParameters.getVariableString(pParameterName);
			if (!StringUtils.isBlank(value)) {
				return value;
			}
		}
		throw new WorkflowConfigurationException(EBXCommonsFunctionalReason.WORKFLOW_INTERACTION_PARAM_NOT_FOUND, pParameterName);
	}

	/**
	 * Get an internal parameter value from the interaction.
	 *
	 * @since 1.0.0
	 * @param pSession       An EBX user session
	 * @param pParameterName A parameters name
	 * @return The parameters value
	 * @throws EBXResourceNotFoundException   If the session in not in workflow.
	 * @throws WorkflowConfigurationException if no or blank value found.
	 */
	public static String getInteractionInternalParameter(final Session pSession, final String pParameterName) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		SessionInteraction interaction = pSession.getInteraction(true);
		if (interaction == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_INTERACTION_NOT_FOUND);
		}

		ParametersMap internalParameters = interaction.getInternalParameters();
		if (internalParameters != null) {
			String value = internalParameters.getVariableString(pParameterName);
			if (!StringUtils.isBlank(value)) {
				return value;
			}
		}
		throw new WorkflowConfigurationException(EBXCommonsFunctionalReason.WORKFLOW_INTERACTION_PARAM_NOT_FOUND, pParameterName);
	}

	/**
	 * Get the record, subject of the workflow, from the data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext the process data context
	 * @param pRepository  the repository
	 * @return the record
	 * @throws WorkflowConfigurationException    If data context variable {@value WorkflowConstants#VAR_RECORD} is not found or not valued.
	 * @throws EBXResourceNotFoundException      If the dataspace, dataset or record is not found.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecordFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository)
			throws EBXResourceNotFoundException, WorkflowConfigurationException, EBXResourceNotIdentifiedException {
		Adaptation dataset = getDatasetFromDataContext(pDataContext, pRepository);
		String xpath = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_RECORD);
		return AdaptationUtils.getRecord(dataset, xpath);
	}

	/**
	 * Get the table occurrence subject of the workflow from the initial dataspace.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository
	 * @param pSession    An EBX user session.
	 * @return The table occurrence subject of the workflow.
	 * @throws WorkflowConfigurationException    If interaction input parameter {@value WorkflowConstants#PARAM_XPATH} is not found or not valued.
	 * @throws EBXResourceNotFoundException      If the dataspace, dataset or record is not found or if the session is not in a workflow.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecordFromInteraction(final Repository pRepository, final Session pSession)
			throws EBXResourceNotFoundException, WorkflowConfigurationException, EBXResourceNotIdentifiedException {
		Adaptation dataset = getDatasetFromInteraction(pRepository, pSession);
		String xpath = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_XPATH);
		return AdaptationUtils.getRecord(dataset, xpath);
	}

	/**
	 * Get the record, subject of the workflow, in workspace, from the data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext A process instance data context.
	 * @param pRepository  A repository
	 * @return The table occurrence just created based on internal parameters
	 * @throws WorkflowConfigurationException    If data context variable {@value WorkflowConstants#VAR_RECORD} is not found or not valued.
	 * @throws EBXResourceNotFoundException      If the dataspace, dataset or record is not found or if the session is not in a workflow.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecordInWorkspaceFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository)
			throws EBXResourceNotIdentifiedException, EBXResourceNotFoundException, WorkflowConfigurationException {
		Adaptation dataset = getDatasetInWorkspaceFromDataContext(pDataContext, pRepository);
		String xpath = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_RECORD);
		return AdaptationUtils.getRecord(dataset, xpath);
	}

	/**
	 * Get the table occurrence subject of the workflow from the workspace.
	 *
	 * @since 1.0.0
	 * @author Mickaël Chevalier
	 * @param pRepository A repository
	 * @param pSession    An EBX user session.
	 * @return The table occurrence subject of the workflow.
	 * @throws WorkflowConfigurationException    If interaction input parameter {@value WorkflowConstants#PARAM_XPATH} is not found or not valued.
	 * @throws EBXResourceNotFoundException      If the dataspace, dataset or record is not found or if the session is not in a workflow.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 */
	public static Adaptation getRecordInWorkspaceFromInteraction(final Repository pRepository, final Session pSession)
			throws EBXResourceNotFoundException, WorkflowConfigurationException, EBXResourceNotIdentifiedException {
		Adaptation dataset = getDatasetInWorkspaceFromInteraction(pRepository, pSession);
		String xpath = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_XPATH);
		return AdaptationUtils.getRecord(dataset, xpath);
	}

	/**
	 * Get service key from the interaction.
	 *
	 * @since 1.1.0
	 * @param pSession An EBX user session.
	 * @return ServiceKey of the current user task.
	 * @throws EBXResourceNotFoundException If the session in not in workflow.
	 */
	public static Optional<ServiceKey> getServiceKeyFromInteraction(final Session pSession) throws EBXResourceNotFoundException {
		SessionInteraction interaction = pSession.getInteraction(true);
		if (interaction == null) {
			throw new EBXResourceNotFoundException(EBXCommonsFunctionalReason.RESOURCE_INTERACTION_NOT_FOUND);
		}
		return Optional.ofNullable(interaction.getServiceKey());
	}

	/**
	 * Get the table subject of the worklfow from the data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext The process instance data context.
	 * @param pRepository  A repository
	 * @return The table subject of the workflow.
	 * @throws WorkflowConfigurationException If data context variable {@value WorkflowConstants#VAR_TABLE} is not found or not valued.
	 * @throws EBXResourceNotFoundException   If the dataspace, dataset or table is not found.
	 */
	public static AdaptationTable getTableFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository) throws WorkflowConfigurationException, EBXResourceNotFoundException {
		Adaptation dataset = getDatasetFromDataContext(pDataContext, pRepository);
		String tablePath = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_TABLE);
		return AdaptationUtils.getTable(dataset, Path.parse(tablePath));
	}

	/**
	 * Gets a variable from data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext  A process data context.
	 * @param pVariableName A variable name.
	 * @return The variable value from data context.
	 * @throws WorkflowConfigurationException If the variable has not been found.
	 */
	public static String getVariableFromDataContext(final DataContextReadOnly pDataContext, final String pVariableName) throws WorkflowConfigurationException {
		String value = pDataContext.getVariableString(pVariableName);
		if (value == null) {
			throw new WorkflowConfigurationException(EBXCommonsFunctionalReason.WORKFLOW_VARIABLE_NOT_FOUND, pVariableName);
		} else {
			return value;
		}
	}

	/**
	 * Get the workspace from data context.
	 *
	 * @since 1.0.0
	 * @param pDataContext A process instance data context.
	 * @param pRepository  A repository.
	 * @return A workspace.
	 * @throws EBXResourceNotFoundException   If the workspace has not been found.
	 * @throws WorkflowConfigurationException If the data context variable {@value WorkflowConstants#VAR_WORKSPACE} is not found.
	 */
	public static AdaptationHome getWorkspaceFromDataContext(final DataContextReadOnly pDataContext, final Repository pRepository) throws EBXResourceNotFoundException, WorkflowConfigurationException {
		String workspace = getVariableFromDataContext(pDataContext, WorkflowConstants.VAR_WORKSPACE);
		return AdaptationUtils.getDataspace(pRepository, workspace);
	}

	/**
	 * Get the workspace from the interaction in session.
	 *
	 * @since 1.0.0
	 * @param pRepository A repository.
	 * @param pSession    An EBX user session.
	 * @return The workspace.
	 * @throws WorkflowConfigurationException If interaction input parameter {@value WorkflowConstants#PARAM_WORKSPACE} is not found or not valued.
	 * @throws EBXResourceNotFoundException   If the workspace has not been found or if the session in not in workflow.
	 */
	public static AdaptationHome getWorkspaceFromInteraction(final Repository pRepository, final Session pSession) throws WorkflowConfigurationException, EBXResourceNotFoundException {
		String workspace = getInteractionInputParameter(pSession, WorkflowConstants.PARAM_WORKSPACE);
		return AdaptationUtils.getDataspace(pRepository, workspace);
	}

	/**
	 * Checks if a session is in workflow.
	 *
	 * @since 1.0.0
	 * @param pSession An EBX user session.
	 * @return true if the session is in workflow.
	 */
	public static boolean isInWorkflow(final Session pSession) {
		return pSession.getInteraction(true) != null;
	}

	/**
	 * Verify if a table occurrence is currently the subject of a workflow.
	 *
	 * @since 1.0.0
	 * @param pRecord  A table occurrence.
	 * @param pSession An EBX user session.
	 * @return true if the record is the subject of the current workflow.
	 * @throws EBXResourceNotFoundException      If the record it not found.
	 * @throws EBXResourceNotIdentifiedException If the XPath expression does not identify only one record.
	 * @throws WorkflowConfigurationException    If parameters or varibales are missing in the workflow configuration.
	 */
	public static boolean isRecordCurrentlySubjectOfWorkflow(final Adaptation pRecord, final Session pSession)
			throws EBXResourceNotFoundException, WorkflowConfigurationException, EBXResourceNotIdentifiedException {
		Adaptation record;
		if (isWorkflowCreatingOrDuplicatingRecord(pSession)) {
			record = WorkflowUtils.getCreatedRecordInWorkspaceFromInteraction(pRecord.getHome().getRepository(), pSession);
		} else {
			record = WorkflowUtils.getRecordInWorkspaceFromInteraction(pRecord.getHome().getRepository(), pSession);
		}
		return (record != null && record.toXPathExpression().equals(pRecord.toXPathExpression()));
	}

	/**
	 * Verify if the workflow is executing a task of creation or duplication.
	 *
	 * @since 1.0.0
	 * @param pSession An EBX user session.
	 * @return true If the workflow is executing a task of creation or duplication.
	 * @throws EBXResourceNotFoundException If the session in not in workflow.
	 */
	public static boolean isWorkflowCreatingOrDuplicatingRecord(final Session pSession) throws EBXResourceNotFoundException {
		Optional<ServiceKey> serviceKey = WorkflowUtils.getServiceKeyFromInteraction(pSession);
		return serviceKey.isPresent() && (serviceKey.get().equals(ServiceKey.CREATE) || serviceKey.get().equals(ServiceKey.DUPLICATE));
	}
}
