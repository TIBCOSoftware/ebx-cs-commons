/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.relation;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;

/**
 * 
 * @author Gilles Mayer
 */
public final class TableRefRelation implements ToOne {
	private final SchemaFacetTableRef ref;

	public TableRefRelation(final SchemaFacetTableRef ref) {
		this.ref = ref;
	}

	@Override
	public Adaptation get(final Adaptation from) {
		return ref.getLinkedRecord(from);
	}
}