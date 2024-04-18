package com.tibco.ebx.cs.commons.component.workflow.condition;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.workflow.ConditionBean;
import com.orchestranetworks.workflow.ConditionBeanContext;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;

/**
 * @author Mickaël Chevalier
 *
 *         Verify an xpath condition on a record.
 *
 *         <pre>
 * {@code
 * 		<bean className="com.orchestranetworks.ps.workflow.condition.PredicateCondition">
 *           <documentation xml:lang="en-US">
 *               <label>Predicate is true?</label>
 *               <description>
 *                   Test if a XPath predicate is true
 *               </description>
 *           </documentation>
 *           <properties>
 *               <property name="branch" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Data space</label>
 *                       <description>
 *                          The data space where to apply the predicate
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="instance" input="true">
 *                   <documentation xml:lang="en-US">
 *                       <label>Data set</label>
 *                       <description>
 *                           The data where to apply the predicate
 *                       </description>
 *                   </documentation>
 *              </property>
 *              <property name="xpath" input="true">
 *                   <documentation xml:lang="en-US">
 *                        <label>Records XPath expression</label>
 *                       <description>
 *                          XPath to the record where to apply the predicate
 *                       </description>
 *                   </documentation>
 *               </property>
 *               <property name="predicate" input="true">
 *               	<documentation xml:lang="en-US">
 *                       <label>Predicate</label>
 *                       <description>
 *                       	Predicate to test
 *                       </description>
 *                   </documentation>
 *               </property>
 *           </properties>
 *       </bean>
 *
 * }
 * </pre>
 *
 */
public class RecordMatchesPredicateCondition extends ConditionBean {

	/** The data space. */
	private String branch;

	/** The dataset. */
	private String instance;

	/** The xpath to find the record. */
	private String xpath;

	/** The predicate to verify. */
	private String predicate;

	/*
	 * @see com.orchestranetworks.workflow.ConditionBean#evaluateCondition(com.orchestranetworks.workflow.ConditionBeanContext)
	 */
	@Override
	public final boolean evaluateCondition(final ConditionBeanContext aContext) throws OperationException {
		Adaptation record;
		try {
			record = AdaptationUtils.getRecord(aContext.getRepository(), this.branch, this.instance, this.xpath);
			return record.matches(this.predicate);
		} catch (EBXResourceNotFoundException | EBXResourceNotIdentifiedException ex) {
			throw OperationException.createError(ex);
		}
	}

	/**
	 * Gets the dataset.
	 *
	 * @return the dataset
	 */
	public final String getDataset() {
		return this.instance;
	}

	/**
	 * Gets the dataspace.
	 *
	 * @return the dataspace
	 */
	public final String getDataspace() {
		return this.branch;
	}

	/**
	 * Gets the predicate.
	 *
	 * @return the predicate
	 */
	public final String getPredicate() {
		return this.predicate;
	}

	/**
	 * Gets the xpath.
	 *
	 * @return the xpath
	 */
	public final String getXpath() {
		return this.xpath;
	}

	/**
	 * Sets the dataset.
	 *
	 * @param dataset the new dataset
	 */
	public final void setDataset(final String dataset) {
		this.instance = dataset;
	}

	/**
	 * Sets the dataspace.
	 *
	 * @param dataspace the new dataspace
	 */
	public final void setDataspace(final String dataspace) {
		this.branch = dataspace;
	}

	/**
	 * Sets the predicate.
	 *
	 * @param predicate the new predicate
	 */
	public final void setPredicate(final String predicate) {
		this.predicate = predicate;
	}

	/**
	 * Sets the xpath.
	 *
	 * @param xpath the new xpath
	 */
	public final void setXpath(final String xpath) {
		this.xpath = xpath;
	}
}
