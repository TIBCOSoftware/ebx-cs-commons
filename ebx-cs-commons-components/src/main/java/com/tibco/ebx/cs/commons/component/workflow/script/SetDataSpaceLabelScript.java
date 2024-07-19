/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.workflow.script;

import java.util.Locale;

import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ScriptTaskBean;
import com.orchestranetworks.workflow.ScriptTaskBeanContext;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;

/**
 * @author Mickaël Chevalier
 *
 *         Set the label of a data space. <br>
 *         {@code
 *  <bean className="com.orchestranetworks.ps.workflow.scripttask.SetDataSpaceLabelScript">
 *          <documentation xml:lang="en-US">
 *               <label>Set the label of a data space</label>
 *               <description>
 *                 Set the label of a data space
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
 *                 <property name="label" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Label</label>
 *                       <description>
 *                          The new label to set
 *                       </description>
 *                   </documentation>
 *               </property>
 *           </properties>
 *       </bean><}
 */
public class SetDataSpaceLabelScript extends ScriptTaskBean {

	/** The label. */
	private String label;

	/** The data space. */
	private String branch;

	/*
	 * @see com.orchestranetworks.workflow.ScriptTaskBean#executeScript(com.orchestranetworks.workflow.ScriptTaskBeanContext)
	 */
	@Override
	public void executeScript(final ScriptTaskBeanContext pContext) throws OperationException {
		AdaptationHome home;
		try {
			home = AdaptationUtils.getDataspace(pContext.getRepository(), this.branch);
		} catch (EBXResourceNotFoundException ex) {
			throw OperationException.createError(ex);
		}
		pContext.getRepository().setDocumentationLabel(home, this.label, Locale.getDefault(), pContext.getSession());
	}

	/**
	 * Gets the data space.
	 *
	 * @return the data space
	 */
	public String getDataSpace() {
		return this.branch;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Sets the data space.
	 *
	 * @param dataSpace the new data space
	 */
	public void setDataSpace(final String dataSpace) {
		this.branch = dataSpace;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(final String label) {
		this.label = label;
	}
}
