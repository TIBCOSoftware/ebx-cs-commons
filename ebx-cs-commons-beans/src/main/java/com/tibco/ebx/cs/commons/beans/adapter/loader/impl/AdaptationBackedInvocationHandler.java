/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

import com.onwbp.adaptation.Adaptation;

/**
 * 
 * @author Gilles Mayer
 */
public final class AdaptationBackedInvocationHandler<T> extends AbstractProxyInvocationHandler<T> {

	public static <T> T getProxy(final Class<T> beanType, final Adaptation adaptation) {
		if (adaptation == null) {
			return null;
		}
		return beanType.cast(Proxy.newProxyInstance(beanType.getClassLoader(), new Class[] { beanType }, new AdaptationBackedInvocationHandler<>(beanType, adaptation)));
	}

	private final Adaptation adaptation;

	private AdaptationBackedInvocationHandler(final Class<T> beanType, final Adaptation record) {
		super(beanType);
		this.adaptation = Objects.requireNonNull(record);
	}

	@Override
	protected Object invokeDefault(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return DEFAULT_METHOD_HANDLE_FACTORY.findSpecial(this.beanType, method).bindTo(proxy).invokeWithArguments(args);
	}

	@Override
	protected Object invokeOther(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.getName().equals("toAdaptation") && method.getParameterTypes().length == 0 && method.getReturnType() == Adaptation.class) {
			return this.adaptation;
		}
		return ReflectionImpl.doInvocation(this.adaptation, method, AdaptationBackedInvocationHandler::getProxy);
	}

	@Override
	protected String paramString() {
		return this.adaptation.getOccurrencePrimaryKey().format();
	}

	@Override
	protected int invokeHashCode() {
		return this.adaptation.getOccurrencePrimaryKey().hashCode();
	}

	@Override
	protected boolean invokeEquals(final InvocationHandler otherHandler, final Object other) {
		if (otherHandler instanceof AdaptationBackedInvocationHandler) {
			return this.beanType == ((AdaptationBackedInvocationHandler<?>) otherHandler).beanType
					&& this.adaptation.getOccurrencePrimaryKey().equals(((AdaptationBackedInvocationHandler<?>) otherHandler).adaptation.getOccurrencePrimaryKey());
		}
		return false;
	}
}
