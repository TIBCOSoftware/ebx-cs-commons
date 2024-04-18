package com.tibco.ebx.cs.commons.beans.adapter.loader;

import java.util.HashMap;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.tibco.ebx.cs.commons.beans.adapter.loader.impl.EagerReadInvocationHandler;

/**
 * 
 * @author Gilles Mayer
 */
final class EagerReadBeanLoader implements BeanLoader {
	private final Map<Adaptation, Object> adaptationToProxy = new HashMap<>();

	@Override
	public <T> T load(final Class<T> beanType, final Adaptation adaptation) {
		return beanType.cast(EagerReadInvocationHandler.getProxy(this.adaptationToProxy, beanType, adaptation));
	}
}