/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */

package com.tibco.ebx.cs.commons.beans.adapter.relation;

import java.util.LinkedHashSet;

import com.onwbp.adaptation.Adaptation;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollection;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollections;

/**
 * 
 * @author Gilles Mayer
 */
public interface ToMany extends Relation {
	public AdaptationCollection get(Adaptation adaptation);

	public default ToMany and(final ToOne next) {
		return new ToMany() {

			@Override
			public AdaptationCollection get(final Adaptation from) {
				LinkedHashSet<Adaptation> result = new LinkedHashSet<>();
				AdaptationCollection col = ToMany.this.get(from);
				for (Adaptation a : col) {
					result.add(next.get(a));
				}
				return AdaptationCollections.of(result);
			}
		};
	}

	public default ToMany and(final ToMany next) {
		return new ToMany() {

			@Override
			public AdaptationCollection get(final Adaptation from) {
				LinkedHashSet<Adaptation> result = new LinkedHashSet<>();
				AdaptationCollection col = ToMany.this.get(from);
				for (Adaptation a : col) {
					result.addAll(next.get(a));
				}
				return AdaptationCollections.of(result);
			}
		};
	}

}
