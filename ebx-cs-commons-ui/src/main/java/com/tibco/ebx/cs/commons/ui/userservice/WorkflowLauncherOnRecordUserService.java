package com.tibco.ebx.cs.commons.ui.userservice;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.Repository;
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
 * This UserService is set on TableViewEntitySelection, but will launch one workflow instance by selected record.The record Xpath will be set in the data context "recordXpath". <strong>By default, no
 * work item will be open.</strong> If you want such behavior set true to the argument pOpenFirstWorkItem, the work item of the first worklflow will be open, no other workflow will be launched for
 * remaining selected records.<br>
 * <br>
 * If no record is selected, no workflow will be launched. You can limit the record selection in the defineActivation method of the Declaration.<br>
 * If you want to use the table level without record selection, refer to {@link com.tibco.ebx.cs.commons.ui.userservice.WorkflowLauncherOnTableUserService WorkflowLauncherOnTableUserService} instead.
 * <br>
 * <br>
 * The workflow model must have the same input parameters (data context) as the following:
 * <ul>
 * <li><strong>initialDataspace</strong>: the name of the data space from where the service has been launched</li>
 * <li><strong>dataset</strong>: the name of the data set from where the service has been launched</li>
 * <li><strong>recordXpath</strong>: the path in schema (so, with the predicate) to the selected record</li>
 * </ul>
 *
 * @author Aurélien Ticot
 * @since 1.7.0
 */
public class WorkflowLauncherOnRecordUserService extends WorkflowLauncherAbstractUserService<TableViewEntitySelection> {
	private boolean openFirstWorkItem = false;

	/**
	 * Create a new instance of the class allowing to launch a given workflow by setting default data context.<br>
	 * <br>
	 * By default, the service is not opening the created work item. Set true on argument pOpenWorkItem or use {@link #setOpenFirstWorkItem(boolean)}.<br>
	 * <br>
	 * This UserService is set on TableViewEntitySelection, but will launch one workflow instance by selected record.The record Xpath will be set in the data context "recordXpath". <strong>By default,
	 * no work item will be open.</strong> If you want such behavior set true to the argument pOpenFirstWorkItem, the work item of the first worklflow will be open, no other workflow will be launched
	 * for remaining selected records.<br>
	 * <br>
	 * If no record is selected, no workflow will be launched. You can limit the record selection in the defineActivation method of the Declaration.<br>
	 * If you want to use the table level without record selection, refer to {@link com.tibco.ebx.cs.commons.ui.userservice.WorkflowLauncherOnTableUserService WorkflowLauncherOnTableUserService}
	 * instead. <br>
	 * <br>
	 * The workflow model must have the same input parameters (data context) as the following:
	 * <ul>
	 * <li><strong>initialDataspace</strong>: the name of the data space from where the service has been launched</li>
	 * <li><strong>dataset</strong>: the name of the data set from where the service has been launched</li>
	 * <li><strong>recordXpath</strong>: the path in schema (so, with the predicate) to the selected record</li>
	 * </ul>
	 *
	 * @param pWorkflowPublicationName the workflow published name
	 * @param pWorkflowLabel           the workflow label, optional
	 * @param pOpenFirstWorkItem       set true to open the first work item, false to go back to the current screen. Optional, default is false.
	 * @since 1.7.0
	 */
	public WorkflowLauncherOnRecordUserService(final String pWorkflowPublicationName, final UserMessage pWorkflowLabel, final boolean pOpenFirstWorkItem) {
		super(pWorkflowPublicationName, pWorkflowLabel);
		this.openFirstWorkItem = pOpenFirstWorkItem;
	}

	@Override
	public UserServiceEventOutcome initialize(final UserServiceInitializeContext<TableViewEntitySelection> pContext) {
		Repository pRepository = pContext.getRepository();
		Session pSession = pContext.getSession();
		ProcessLauncher launcher = WorkflowEngine.getFromRepository(pRepository, pSession).getProcessLauncher(PublishedProcessKey.forName(this.getWorkflowPublicationName()));

		TableViewEntitySelection entitySelection = pContext.getEntitySelection();

		AdaptationHome dataspace = entitySelection.getDataspace();
		Adaptation dataset = entitySelection.getDataset();

		Request selectedRecordsRequest = entitySelection.getSelectedRecords();
		RequestResult selectedRecordsRequestResult = selectedRecordsRequest.execute();

		UserServiceEventOutcome outcome = null;
		try {
			Adaptation record = null;
			while ((record = selectedRecordsRequestResult.nextAdaptation()) != null) {
				launcher.setInputParameter(WorkflowDefaultParameter.INITIAL_DATASPACE.getStringValue(), dataspace.getKey().getName());
				launcher.setInputParameter(WorkflowDefaultParameter.DATASET.getStringValue(), dataset.getAdaptationName().getStringName());
				launcher.setInputParameter(WorkflowDefaultParameter.RECORD_XPATH.getStringValue(), record.toXPathExpression());
				launcher.setLabel(this.getWorkflowLabel());
				ProcessLauncherResult result = launcher.launchProcessWithResult();

				// TODO Open first work item not compatible with multiple selection

				if (this.openFirstWorkItem && result != null) {
					WorkItemKey workItemKey = result.getWorkItemKey();
					if (workItemKey != null) {
						outcome = UserServiceNext.nextWorkItem(workItemKey, true);
					}
					break;
				}
			}
		} catch (OperationException ex) {
			// TODO Manage exception
			throw new RuntimeException(ex);
		} finally {
			selectedRecordsRequestResult.close();
		}

		if (outcome == null) {
			outcome = UserServiceNext.nextClose();
		}

		return outcome;
	}

	/**
	 * Getter for openFirstWorkItem variable.
	 *
	 * @return true if the first work item will be open, false otherwise.
	 * @since 1.7.0
	 */
	public boolean isOpenFirstWorkItem() {
		return this.openFirstWorkItem;
	}

	/**
	 * Setter for openFirstWorkItem variable. Default is false.
	 *
	 * @param pOpenFirstWorkItem set true to open the first work item.
	 * @since 1.7.0
	 */
	public void setOpenFirstWorkItem(final boolean pOpenFirstWorkItem) {
		this.openFirstWorkItem = pOpenFirstWorkItem;
	}
}
