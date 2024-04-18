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