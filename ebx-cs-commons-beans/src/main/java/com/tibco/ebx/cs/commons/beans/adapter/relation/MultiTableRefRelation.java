package com.tibco.ebx.cs.commons.beans.adapter.relation;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollection;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollections;

/**
 * 
 * @author Gilles Mayer
 */
public final class MultiTableRefRelation implements ToMany {
	private final SchemaFacetTableRef ref;

	public MultiTableRefRelation(final SchemaFacetTableRef ref) {
		this.ref = ref;
	}

	@Override
	public AdaptationCollection get(final Adaptation from) {
		return AdaptationCollections.of(ref.getLinkedRecords(from));
	}
}