/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.workflow.script;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.workflow.ScriptTaskBean;
import com.orchestranetworks.workflow.ScriptTaskBeanContext;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;
import com.tibco.ebx.cs.commons.lib.procedure.SetXsStringValueProcedure;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * @author Mickaël Chevalier
 *
 *         Set the value of a node in a record.
 *
 *         <pre>
 * {@code
 *  		<bean className="com.orchestranetworks.ps.workflow.scripttask.SetValueScript">
 *           <documentation xml:lang="en-US">
 *               <label>Set value</label>
 *               <description>
 *                   Set a value to a field
 *               </description>
 *           </documentation>
 *           <properties>
 *               <property name="branch" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Data space</label>
 *                       <description>
 *                          The data space where the value to set is located
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="instance" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Data set</label>
 *                       <description>
 *                           The data set where the value to set is located
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="xpath" input="true">
 *                   <documentation xml:lang="en-US">
 *                        <label>XPath</label>
 *                       <description>
 *                          XPath to the record where the value to set is located
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="path" input="true">
 *               	<documentation xml:lang="en-US">
 *                       <label>Path</label>
 *                       <description>
 *                          Path to the field to set
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="value" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Value</label>
 *                       <description>
 *                          Value to set in the selected field
 *                       </description>
 *                   </documentation>
 *               </property>
 *           </properties>
 *       </bean>
 * }
 * </pre>
 */
public class SetValueScript extends ScriptTaskBean {

	/** The branch. */
	private String branch;

	/** The instance. */
	private String instance;

	/** The xpath. */
	private String xpath;

	/** The path. */
	private String path;

	/** The value. */
	private String value;

	/*
	 * @see com.orchestranetworks.workflow.ScriptTaskBean#executeScript(com.orchestranetworks.workflow.ScriptTaskBeanContext)
	 */
	@Override
	public void executeScript(final ScriptTaskBeanContext pContext) throws OperationException {
		Adaptation record;
		try {
			record = AdaptationUtils.getRecord(pContext.getRepository(), this.branch, this.instance, this.xpath);
		} catch (EBXResourceNotFoundException | EBXResourceNotIdentifiedException ex) {
			throw OperationException.createError(ex);
		}
		SchemaNode node = SchemaUtils.getNode(record, Path.parse(this.path));
		final ProgrammaticService srv = ProgrammaticService.createForSession(pContext.getSession(), record.getHome());
		final ProcedureResult result = srv.execute(new SetXsStringValueProcedure(record, node, this.value));
		if (result.hasFailed()) {
			throw result.getException();
		}
	}

	/**
	 * Gets the branch.
	 *
	 * @return the branch
	 */
	public String getBranch() {
		return this.branch;
	}

	/**
	 * Gets the single instance of SetValueScript.
	 *
	 * @return single instance of SetValueScript
	 */
	public String getInstance() {
		return this.instance;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Gets the xpath.
	 *
	 * @return the xpath
	 */
	public String getXpath() {
		return this.xpath;
	}

	/**
	 * Sets the branch.
	 *
	 * @param branch the new branch
	 */
	public void setBranch(final String branch) {
		this.branch = branch;
	}

	/**
	 * Sets the instance.
	 *
	 * @param instance the new instance
	 */
	public void setInstance(final String instance) {
		this.instance = instance;
	}

	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Sets the xpath.
	 *
	 * @param xpath the new xpath
	 */
	public void setXpath(final String xpath) {
		this.xpath = xpath;
	}
}
