
package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.onwbp.adaptation.AdaptationReference;

/**
 * 
 * @author Gilles Mayer
 */
public class AdaptationReferenceTransformer implements Function<String, AdaptationReference> {
	@Override
	public AdaptationReference apply(final String t) {
		return t == null ? null : AdaptationReference.forPersistentName(t);
	}
}
