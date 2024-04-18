package com.tibco.ebx.cs.commons.component.scheduledtask;

import java.util.HashMap;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.workflow.WorkflowLauncher;

/**
 * Scheduled task to launch a workflow.<br>
 * <br>
 * Use the following parameters:
 * <ul>
 * <li><strong>workflowPublicationName</strong>: the publication name of the workflow to launch (Mandatory)</li>
 * <li><strong>workflowLabel</strong>: label of the instance (Optional)</li>
 * <li><strong>dataContextParamName</strong>: name of one data context parameter to set (Optional)</li>
 * <li><strong>dataContextParamValue</strong>: value of the one data context parameter (Optional)</li>
 * </ul>
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class WorkflowLauncherScheduledTask extends ScheduledTask {
	private String workflowPublicationName;
	private String workflowLabel;
	private String dataContextParamName;
	private String dataContextParamValue;

	@Override
	public void execute(final ScheduledExecutionContext pContext) throws OperationException, ScheduledTaskInterruption {
		// Check parameters
		if (this.workflowPublicationName == null || this.workflowPublicationName.trim().isEmpty()) {
			pContext.addExecutionInformation("The workflowPublicationName parameter shall not be null or empty");
			throw OperationException.createError("The workflowPublicationName parameter shall not be null or empty");
		}

		Repository repository = pContext.getRepository();
		Session session = pContext.getSession();

		WorkflowLauncher launcher = new WorkflowLauncher(repository, session);

		// Define workflow input parameters
		HashMap<String, String> inputParameters = new HashMap<>();

		if (this.dataContextParamName != null && !this.dataContextParamName.trim().isEmpty()) {
			inputParameters.put(this.dataContextParamName, this.dataContextParamValue);
		}

		// Define workflow label
		UserMessage workflowLabelUserMessage = null;
		if (this.workflowLabel != null && !this.workflowLabel.trim().isEmpty()) {
			workflowLabelUserMessage = UserMessage.createInfo(this.workflowLabel);
		}

		// Launch the workflow instance
		try {
			pContext.addExecutionInformation("Launching an instance of the workflow " + this.workflowPublicationName);
			launcher.launch(this.workflowPublicationName, inputParameters, workflowLabelUserMessage);
		} catch (Exception ex) {
			pContext.addExecutionInformation("The workflowPublicationName parameter shall not be null or empty");
			throw OperationException.createError("The workflowPublicationName parameter shall not be null or empty", ex);
		}
	}

	/**
	 * Getter for the data context parameter name.
	 *
	 * @return the name.
	 * @since 1.8.0
	 */
	public String getDataContextParamName() {
		return this.dataContextParamName;
	}

	/**
	 * Getter for the data context parameter value.
	 *
	 * @return the value.
	 * @since 1.8.0
	 */
	public String getDataContextParamValue() {
		return this.dataContextParamValue;
	}

	/**
	 * Getter for the workflow label.
	 *
	 * @return the label.
	 * @since 1.8.0
	 */
	public String getWorkflowLabel() {
		return this.workflowLabel;
	}

	/**
	 * Getter for the workflow publication name.
	 *
	 * @return the publication name.
	 * @since 1.8.0
	 */
	public String getWorkflowPublicationName() {
		return this.workflowPublicationName;
	}

	/**
	 * Setter for the data context parameter name.
	 *
	 * @param pDataContextParamName the name to set.
	 * @since 1.8.0
	 */
	public void setDataContextParamName(final String pDataContextParamName) {
		this.dataContextParamName = pDataContextParamName;
	}

	/**
	 * Setter for the data context parameter value.
	 *
	 * @param pDataContextParamValue the value to set.
	 * @since 1.8.0
	 */
	public void setDataContextParamValue(final String pDataContextParamValue) {
		this.dataContextParamValue = pDataContextParamValue;
	}

	/**
	 * Setter for the workflow label.
	 *
	 * @param pWorkflowLabel the label to set.
	 * @since 1.8.0
	 */
	public void setWorkflowLabel(final String pWorkflowLabel) {
		this.workflowLabel = pWorkflowLabel;
	}

	/**
	 * Setter for the workflow publication name.
	 *
	 * @param pWorkflowPublicationName the publication name to set.
	 * @since 1.8.0
	 */
	public void setWorkflowPublicationName(final String pWorkflowPublicationName) {
		this.workflowPublicationName = pWorkflowPublicationName;
	}
}
