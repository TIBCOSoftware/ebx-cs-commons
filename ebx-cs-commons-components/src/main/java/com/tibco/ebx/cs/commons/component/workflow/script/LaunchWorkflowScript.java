/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.workflow.script;

import java.util.Iterator;

import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.DataContextReadOnly;
import com.orchestranetworks.workflow.ProcessInstance;
import com.orchestranetworks.workflow.ProcessLauncher;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.ScriptTaskBean;
import com.orchestranetworks.workflow.ScriptTaskBeanContext;
import com.orchestranetworks.workflow.WorkflowEngine;

/**
 * @author Mickaël Chevalier
 *
 *         Launches a Workflow
 *
 *         <pre>
 * {@code
 *
 * 		<bean className="com.orchestranetworks.ps.workflow.scripttask.LaunchWorkflowScript">
 *           <documentation xml:lang="en-US">
 *               <label>Launch a Workflow</label>
 *               <description>
 *                   Create a process instance of a given publication
 *               </description>
 *           </documentation>
 *           <properties>
 *               <property name="publicationKey" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Workflow publication</label>
 *                       <description>
 *                          Workflow publication to launch
 *                       </description>
 *                   </documentation>
 *               </property>
 *           </properties>
 *       </bean>
 *
 * }
 * </pre>
 *
 *
 */
public class LaunchWorkflowScript extends ScriptTaskBean {

	/** The publication key. */
	private String publicationKey;

	/*
	 * @see com.orchestranetworks.workflow.ScriptTaskBean#executeScript(com.orchestranetworks.workflow.ScriptTaskBeanContext)
	 */
	@Override
	public void executeScript(final ScriptTaskBeanContext pContext) throws OperationException {
		final Repository repository = pContext.getRepository();
		final Session session = pContext.getSession();
		final WorkflowEngine engine = WorkflowEngine.getFromRepository(repository, session);
		final ProcessLauncher launcher = engine.getProcessLauncher(PublishedProcessKey.forName(this.publicationKey));

		final ProcessInstance process = engine.getProcessInstance(pContext.getProcessInstanceKey());
		final DataContextReadOnly dataContext = process.getDataContext();
		final Iterator<String> iterator = dataContext.getVariableNames();
		while (iterator.hasNext()) {
			String variable = iterator.next();
			launcher.setInputParameter(variable, dataContext.getVariableString(variable));
		}
		launcher.launchProcess();
	}

	/**
	 * Gets the publication key.
	 *
	 * @return the publication key
	 */
	public String getPublicationKey() {
		return this.publicationKey;
	}

	/**
	 * Sets the publication key.
	 *
	 * @param publicationKey the new publication key
	 */
	public void setPublicationKey(final String publicationKey) {
		this.publicationKey = publicationKey;
	}

}
