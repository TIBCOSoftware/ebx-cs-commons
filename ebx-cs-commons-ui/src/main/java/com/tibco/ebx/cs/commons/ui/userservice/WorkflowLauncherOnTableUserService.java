/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceInitializeContext;
import com.orchestranetworks.userservice.UserServiceNext;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.ProcessLauncherResult;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.WorkItemKey;
import com.orchestranetworks.workflow.WorkflowEngine;
import com.tibco.ebx.cs.commons.lib.workflow.WorkflowDefaultParameter;

/**
 * UserService on TableViewEntitySelection allowing to launch a given workflow by setting default data context.<br>
 * You must implement the {@link com.orchestranetworks.userservice.declaration.UserServiceDeclaration.OnTableView UserServiceDeclaration.OnTableView} in your code base.<br>
 * <br>
 * This UserService is set on TableViewEntitySelection, but whatever the record selection it will launch one single workflow without any data context for the selected record. You can limit the record
 * selection in the defineActivation method of the Declaration.<br>
 * If you want to use the record selection, refer to {@link com.tibco.ebx.cs.commons.ui.userservice.WorkflowLauncherOnRecordUserService WorkflowLauncherOnRecordUserService} instead. <br>
 * <br>
 * The workflow model must have the same input parameters (data context) as the following:
 * <ul>
 * <li><strong>initialDataspace</strong>: the name of the data space from where the service has been launched</li>
 * <li><strong>dataset</strong>: the name of the data set from where the service has been launched</li>
 * <li><strong>tableXpath</strong>: the path in schema to the table from where the service has been launched</li>
 * </ul>
 *
 * @author Aurélien Ticot
 * @since 1.7.0
 */
public class WorkflowLauncherOnTableUserService extends WorkflowLauncherAbstractUserService<TableViewEntitySelection> {
	private boolean openWorkItem = false;

	/**
	 * Create a new instance of the class allowing to launch a given workflow by setting default data context.<br>
	 * <br>
	 * By default, the service is not opening the created work item. Set true on argument pOpenWorkItem or use {@link #setOpenWorkItem(boolean)}.<br>
	 * <br>
	 * This UserService is set on TableViewEntitySelection, but whatever the record selection it will launch one single workflow without any data context for the selected record. You can limit the
	 * record selection in the defineActivation method of the Declaration.<br>
	 * If you want to use the record selection, refer to {@link com.tibco.ebx.cs.commons.ui.userservice.WorkflowLauncherOnRecordUserService WorkflowLauncherOnRecordUserService} instead. <br>
	 * <br>
	 * The workflow model must have the same input parameters (data context) as the following:
	 * <ul>
	 * <li><strong>initialDataspace</strong>: the name of the data space from where the service has been launched</li>
	 * <li><strong>dataset</strong>: the name of the data set from where the service has been launched</li>
	 * <li><strong>tableXpath</strong>: the path in schema to the table from where the service has been launched</li>
	 * </ul>
	 *
	 * @param pWorkflowPublicationName the workflow published name
	 * @param pWorkflowLabel           the workflow label, optional
	 * @param pOpenWorkItem            set true to open the first work item, false to go back to the current screen. Optional, default is false.
	 * @since 1.7.0
	 */
	public WorkflowLauncherOnTableUserService(final String pWorkflowPublicationName, final UserMessage pWorkflowLabel, final boolean pOpenWorkItem) {
		super(pWorkflowPublicationName, pWorkflowLabel);
		this.openWorkItem = pOpenWorkItem;
	}

	@Override
	public UserServiceEventOutcome initialize(final UserServiceInitializeContext<TableViewEntitySelection> pContext) {
		Repository pRepository = pContext.getRepository();
		Session pSession = pContext.getSession();
		ProcessLauncher launcher = WorkflowEngine.getFromRepository(pRepository, pSession).getProcessLauncher(PublishedProcessKey.forName(this.getWorkflowPublicationName()));

		TableViewEntitySelection entitySelection = pContext.getEntitySelection();

		AdaptationHome dataspace = entitySelection.getDataspace();
		Adaptation dataset = entitySelection.getDataset();
		AdaptationTable table = entitySelection.getTable();
		SchemaNode tableNode = table.getTableNode();

		launcher.setInputParameter(WorkflowDefaultParameter.INITIAL_DATASPACE.getStringValue(), dataspace.getKey().getName());

		launcher.setInputParameter(WorkflowDefaultParameter.DATASET.getStringValue(), dataset.getAdaptationName().getStringName());

		launcher.setInputParameter(WorkflowDefaultParameter.TABLE_XPATH.getStringValue(), tableNode.getPathInSchema().format());

		launcher.setLabel(this.getWorkflowLabel());

		ProcessLauncherResult result;
		try {
			result = launcher.launchProcessWithResult();
		} catch (OperationException ex) {
			// TODO Manage exception
			throw new RuntimeException(ex);
		}

		if (this.openWorkItem && result != null) {
			WorkItemKey workItemKey = result.getWorkItemKey();
			if (workItemKey != null) {
				return UserServiceNext.nextWorkItem(workItemKey, true);
			}
		}

		return UserServiceNext.nextClose();
	}

	/**
	 * Getter for openWorkItem variable, defining whether or not the work item will be open. Default is false.
	 *
	 * @return true if the first work item will be open, false otherwise.
	 * @since 1.7.0
	 */
	public boolean isOpenWorkItem() {
		return this.openWorkItem;
	}

	/**
	 * Setter for openWorkItem variable, defining whether or not the work item will be open. Default is false.
	 *
	 * @param pOpenWorkItem set true to open the first work item.
	 * @since 1.7.0
	 */
	public void setOpenWorkItem(final boolean pOpenWorkItem) {
		this.openWorkItem = pOpenWorkItem;
	}
}
