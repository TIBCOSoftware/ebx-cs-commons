/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
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