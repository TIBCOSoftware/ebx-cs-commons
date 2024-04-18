package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 
 * @author Gilles Mayer
 */
abstract class AbstractProxyInvocationHandler<T> implements InvocationHandler {

	protected static final DefaultMethodHandleFactory DEFAULT_METHOD_HANDLE_FACTORY = DefaultMethodHandleFactory.getPrefered();

	private static final Method OBJECT_EQUALS;
	private static final Method OBJECT_HASHCODE;
	private static final Method OBJECT_TOSTRING;

	static {
		try {
			OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
			OBJECT_HASHCODE = Object.class.getMethod("hashCode");
			OBJECT_TOSTRING = Object.class.getMethod("toString");
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

	protected final Class<T> beanType;

	protected AbstractProxyInvocationHandler(final Class<T> beanType) {
		if (!beanType.isInterface()) {
			throw new IllegalArgumentException(beanType.toGenericString());
		}
		this.beanType = beanType;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		if (method.isDefault()) {
			return invokeDefault(proxy, method, args);
		}
		if (method.equals(OBJECT_TOSTRING)) {
			return this.beanType.getSimpleName() + "[" + paramString() + "]";
		}
		if (method.equals(OBJECT_HASHCODE)) {
			return invokeHashCode();
		}
		if (method.equals(OBJECT_EQUALS)) {
			final Object other = args[0];
			if (other == proxy) {
				return true;
			}
			if (other == null || !Proxy.isProxyClass(other.getClass())) {
				return false;
			}
			return invokeEquals(Proxy.getInvocationHandler(other), other);
		}
		return invokeOther(proxy, method, args);
	}

	protected abstract Object invokeDefault(Object proxy, Method method, Object[] args) throws Throwable;

	protected abstract Object invokeOther(Object proxy, Method method, Object[] args) throws Throwable;

	protected abstract String paramString() throws Throwable;

	protected abstract int invokeHashCode() throws Throwable;

	protected abstract boolean invokeEquals(InvocationHandler otherHandler, Object other) throws Throwable;
}
