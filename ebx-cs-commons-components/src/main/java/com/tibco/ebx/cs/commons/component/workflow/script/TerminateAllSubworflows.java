package com.tibco.ebx.cs.commons.component.workflow.script;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ProcessInstance;
import com.orchestranetworks.workflow.ProcessInstanceKey;
import com.orchestranetworks.workflow.ScriptTaskBean;
import com.orchestranetworks.workflow.ScriptTaskBeanContext;
import com.orchestranetworks.workflow.WorkflowEngine;

/**
 * @author Mickaël Chevalier
 */
public class TerminateAllSubworflows extends ScriptTaskBean {

	@Override
	public void executeScript(final ScriptTaskBeanContext pContext) throws OperationException {
		ProcessInstanceKey parentKey = pContext.getProcessInstance().getParentKey();
		WorkflowEngine engine = WorkflowEngine.getFromRepository(pContext.getRepository(), pContext.getSession());
		ProcessInstance parent = engine.getProcessInstance(parentKey);
		for (ProcessInstance sub : parent.getCurrentSubWorkflows()) {
			if (!sub.isCompleted() && !sub.getProcessInstanceKey().equals(pContext.getProcessInstanceKey())) {
				engine.forceTerminationWithoutCleaning(sub.getProcessInstanceKey());
			}
		}
	}

}
