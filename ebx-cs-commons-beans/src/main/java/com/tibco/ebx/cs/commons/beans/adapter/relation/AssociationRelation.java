package com.tibco.ebx.cs.commons.beans.adapter.relation;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.Request;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.RequestSortCriteria;
import com.orchestranetworks.schema.info.AssociationLink;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollection;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollections;

/**
 * 
 * @author Gilles Mayer
 */
public final class AssociationRelation implements ToMany {
	private final AssociationLink associationLink;
	private final RequestSortCriteria sortCriteria;

	public AssociationRelation(final AssociationLink associationLink) {
		this.associationLink = associationLink;
		this.sortCriteria = null;
	}

	public AssociationRelation(final AssociationLink associationLink, final RequestSortCriteria sortCriteria) {
		this.associationLink = associationLink;
		this.sortCriteria = sortCriteria;
	}

	@Override
	public AdaptationCollection get(final Adaptation from) {
		RequestResult results;
		if (sortCriteria == null) {
			results = this.associationLink.getAssociationResult(from);
		} else {
			Request request = associationLink.getResult(from, null).getRequest();
			request.setSortCriteria(sortCriteria);
			results = request.execute();
		}
		try {
			return AdaptationCollections.of(results);
		} finally {
			results.close();
		}
	}
}