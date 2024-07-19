/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
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