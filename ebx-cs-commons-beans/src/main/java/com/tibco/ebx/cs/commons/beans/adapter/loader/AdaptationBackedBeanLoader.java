/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
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