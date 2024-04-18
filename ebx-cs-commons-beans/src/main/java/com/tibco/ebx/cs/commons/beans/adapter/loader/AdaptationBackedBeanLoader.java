package com.tibco.ebx.cs.commons.beans.adapter.loader;

import com.onwbp.adaptation.Adaptation;
import com.tibco.ebx.cs.commons.beans.adapter.loader.impl.AdaptationBackedInvocationHandler;

/**
 * 
 * @author Gilles Mayer
 */
final class AdaptationBackedBeanLoader implements BeanLoader {
	@Override
	public <T> T load(final Class<T> beanType, final Adaptation adaptation) {
		if (adaptation == null) {
			return null;
		}
		return AdaptationBackedInvocationHandler.getProxy(beanType, adaptation);
	}
}