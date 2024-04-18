package com.tibco.ebx.cs.commons.lib.workflow;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.ProcessLauncherResult;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.WorkflowEngine;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;

/**
 * Class allowing to launch a workflow.<br>
 * <br>
 * Usage:<br>
 *
 * <pre>
 * HashMap&lt;String, String&gt; inputParameters = new HashMap&lt;String, String&gt;();
 * inputParameters.put("initialDataSpace", context.getCurrentHome().getKey().getName());
 * inputParameters.put("recordXpath", context.getCurrentAdaptation().toXPathExpression());
 *
 * WorkflowLauncher launcher = new WorkflowLauncher(repository, session);
 * ProcessLauncherResult result = launcher.launch("MyWorkflowPublicationName", inputParameters);
 * </pre>
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class WorkflowLauncher {
	private final Repository repository;
	private final Session session;

	/**
	 * Instantiates a new workflow launcher.
	 *
	 * @param pRepository the respository
	 * @param pSession    the session, optional
	 * @since 1.7.0
	 */
	public WorkflowLauncher(final Repository pRepository, final Session pSession) {
		this.repository = pRepository;
		this.session = pSession;
	}

	/**
	 * Launch a workflow instance with its data context (input parameters).
	 *
	 * @param pWorkflowPublicationName the workflow name
	 * @param pInputParameters         the input parameters
	 * @return the process launcher result
	 * @since 1.7.0
	 */
	public ProcessLauncherResult launch(final String pWorkflowPublicationName, final HashMap<String, String> pInputParameters) {
		return this.launch(pWorkflowPublicationName, pInputParameters, null);
	}

	/**
	 * Launch a workflow instance with its data context (input parameters).
	 *
	 * @param pWorkflowPublicationName the workflow name
	 * @param pInputParameters         the input parameters
	 * @param pWorkflowLabel           the workflow label
	 * @return the process launcher result
	 * @since 1.7.0
	 */
	public ProcessLauncherResult launch(final String pWorkflowPublicationName, final HashMap<String, String> pInputParameters, final UserMessage pWorkflowLabel) {
		return this.launchWorkflowInstance(pWorkflowPublicationName, pInputParameters, pWorkflowLabel);
	}

	/**
	 * Checks if the input parameter exists for the specified process launcher.
	 *
	 * @param pLauncher      the process launcher
	 * @param pParameterName the parameter name
	 * @return true, if the parameter exists
	 * @since 1.7.0
	 */
	private static boolean isParameterExists(final ProcessLauncher pLauncher, final String pParameterName) {
		if (pParameterName == null) {
			return false;
		}

		// There is no method in the current API (5.7.0) to check if a parameter exists.
		// Using the getInputParameter...(parameterName) will raise an exception if the parameter does not exists.
		try {
			pLauncher.getInputParameterDefaultString(pParameterName);
		} catch (Exception ex) {
			return false;
		}

		return true;
	}

	/**
	 * Launch workflow instance.
	 *
	 * @param pWorkflowPublicationName the workflow publication name
	 * @param pInputParameters         the input parameters
	 * @param pWorkflowLabel           the workflow label
	 * @return the process launcher result
	 * @since 1.7.0
	 */
	private ProcessLauncherResult launchWorkflowInstance(final String pWorkflowPublicationName, final HashMap<String, String> pInputParameters, final UserMessage pWorkflowLabel) {
		ProcessLauncherResult processLauncherResult = null;

		if (pWorkflowPublicationName == null) {
			return processLauncherResult;
		}

		WorkflowEngine workflowEngine = WorkflowEngine.getFromRepository(this.repository, this.session);
		PublishedProcessKey processKey = PublishedProcessKey.forName(pWorkflowPublicationName);
		if (processKey == null) {
			return null;
		}

		ProcessLauncher launcher = workflowEngine.getProcessLauncher(processKey);
		if (launcher == null) {
			return null;
		}

		WorkflowLauncher.setWorkflowLabel(launcher, pWorkflowLabel);
		WorkflowLauncher.setInputParameters(launcher, pInputParameters);
		this.setWorkflowCreator(launcher);

		try {
			processLauncherResult = launcher.launchProcessWithResult();
		} catch (OperationException e) {
			CommonsLogger.getLogger().error(e.getMessage(), e);
		}

		return processLauncherResult;
	}

	/**
	 * Sets the input parameters.
	 *
	 * @param launcher        the process launcher
	 * @param inputParameters the input parameters
	 * @since 1.7.0
	 */
	private static void setInputParameters(final ProcessLauncher launcher, final HashMap<String, String> inputParameters) {
		if (inputParameters == null || launcher == null) {
			return;
		}

		Iterator<Entry<String, String>> iterator = inputParameters.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, String> inputParameter = iterator.next();
			String parameterName = inputParameter.getKey();
			if (WorkflowLauncher.isParameterExists(launcher, parameterName)) {
				String value = inputParameter.getValue();
				launcher.setInputParameter(parameterName, value);
			}
		}
	}

	/**
	 * Sets the workflow creator.
	 *
	 * @param pLauncher the new workflow creator
	 * @since 1.7.0
	 */
	private void setWorkflowCreator(final ProcessLauncher pLauncher) {
		if (this.session == null) {
			return;
		}

		UserReference user = this.session.getUserReference();
		if (pLauncher != null && user != null) {
			pLauncher.setCreator(user);
		}
	}

	/**
	 * Sets the workflow label.
	 *
	 * @param pLauncher      the launcher
	 * @param pWorkflowLabel the workflow label
	 * @since 1.7.0
	 */
	private static void setWorkflowLabel(final ProcessLauncher pLauncher, final UserMessage pWorkflowLabel) {
		if (pWorkflowLabel != null && pLauncher != null) {
			pLauncher.setLabel(pWorkflowLabel);
		}
	}
}
