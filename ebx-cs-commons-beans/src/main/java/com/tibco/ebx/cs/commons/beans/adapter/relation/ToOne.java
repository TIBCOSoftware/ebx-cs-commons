
package com.tibco.ebx.cs.commons.beans.adapter.relation;

import com.onwbp.adaptation.Adaptation;
import com.tibco.ebx.cs.commons.beans.adapter.adaptation.AdaptationCollection;

/**
 * 
 * @author Gilles Mayer
 */
public interface ToOne extends Relation {
	public Adaptation get(Adaptation var1);

	public default ToOne and(final ToOne next) {
		return new ToOne() {

			@Override
			public Adaptation get(final Adaptation from) {
				return next.get(ToOne.this.get(from));
			}
		};
	}

	public default ToMany and(final ToMany next) {
		return new ToMany() {

			@Override
			public AdaptationCollection get(final Adaptation from) {
				return next.get(ToOne.this.get(from));
			}
		};
	}

}
