package com.tibco.ebx.cs.commons.beans.adapter.adaptation;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.onwbp.adaptation.Adaptation;

/**
 * 
 * @author Gilles Mayer
 */
public abstract interface AdaptationCollection extends Collection<Adaptation> {
	public abstract List<Adaptation> toList();

	public abstract Set<Adaptation> toSet();

	@Override
	public abstract Adaptation[] toArray();

}
