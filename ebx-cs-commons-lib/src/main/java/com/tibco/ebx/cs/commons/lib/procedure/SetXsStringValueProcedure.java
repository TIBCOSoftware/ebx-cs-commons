/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * Set a string representation of a value (XsString) in a record's node.
 * 
 * @author Mickaël Chevalier
 */
public final class SetXsStringValueProcedure extends GenericProcedure {

	/** The record. */
	private final Adaptation record;

	/** The node. */
	private final SchemaNode node;

	/** The value. */
	private final String value;

	/**
	 * Set the value of a given node in a record
	 *
	 * @param aRecord the record
	 * @param aNode   the node
	 * @param aValue  the value as an XsString
	 */
	public SetXsStringValueProcedure(final Adaptation aRecord, final SchemaNode aNode, final String aValue) {
		this.record = aRecord;
		this.node = aNode;
		this.value = aValue;
	}

	@Override
	protected void doExecute(final ProcedureContext pContext) throws Exception {
		final ValueContextForUpdate vcfu = pContext.getContext(this.record.getAdaptationName());
		vcfu.setValue(this.node.parseXsString(this.value), this.node.getPathInAdaptation());
		pContext.doModifyContent(this.record, vcfu);
	}
}
