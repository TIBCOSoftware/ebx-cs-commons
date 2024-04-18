
package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.onwbp.adaptation.AdaptationName;

/**
 * 
 * @author Gilles Mayer
 */
public class AdaptationNameTransformer implements Function<String, AdaptationName> {
	@Override
	public AdaptationName apply(final String t) {
		return t == null ? null : AdaptationName.forName(t);
	}
}
