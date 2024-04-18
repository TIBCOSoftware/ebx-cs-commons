package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;

/**
 * 
 * @author Gilles Mayer
 */
public final class EagerReadInvocationHandler<T> extends AbstractProxyInvocationHandler<T> {

	public static <T> T getProxy(final Map<Adaptation, Object> adaptationToProxy, final Class<T> beanType, final Adaptation adaptation) {
		if (adaptation == null) {
			return null;
		}
		T proxy = beanType.cast(adaptationToProxy.get(adaptation));
		if (proxy == null) {
			proxy = newProxy(adaptationToProxy, beanType, adaptation);
		}
		return proxy;
	}

	private static <T> T newProxy(final Map<Adaptation, Object> adaptationToProxy, final Class<T> beanType, final Adaptation adaptation) {
		try {
			EagerReadInvocationHandler<T> handler = new EagerReadInvocationHandler<>(beanType, adaptation);
			final T proxy = beanType.cast(Proxy.newProxyInstance(beanType.getClassLoader(), new Class[] { beanType }, handler));
			// proxy instance must be stored in the map
			adaptationToProxy.put(adaptation, proxy);
			// then values (in handler) can be updated
			handler.values = resolveValues(adaptationToProxy, beanType, adaptation);
			return proxy;
		} catch (ReflectiveOperationException ex) {
			throw new UnsupportedOperationException(ex);
		}
	}

	/** Identity delegate (equals/hashCode) */
	private final Object id;

	/** Getter return values */
	private Map<Method, Object> values;

	/** Cache of unbound MethodHandle for default methods */
	private final Map<Method, MethodHandle> specialHandles;

	/**
	 * Constructs a new handler for the specified bean type with the values of the specified adaptation.
	 * 
	 * @param beanType   class of the bean interface type
	 * @param adaptation record matching the bean instance
	 */
	private EagerReadInvocationHandler(final Class<T> beanType, final Adaptation adaptation) throws ReflectiveOperationException {
		super(beanType);
		this.id = adaptation.getOccurrencePrimaryKey().format();
		this.specialHandles = findSpecialHandles(beanType);
	}

	private static Map<Method, MethodHandle> findSpecialHandles(final Class<?> beanType) throws ReflectiveOperationException {
		Map<Method, MethodHandle> handles = new HashMap<>(2);
		for (Method method : beanType.getMethods()) {
			if (method.isDefault()) {
				handles.put(method, DEFAULT_METHOD_HANDLE_FACTORY.findSpecial(beanType, method));
			}
		}
		if (handles.isEmpty()) {
			return null;
		}
		return handles;
	}

	private static Map<Method, Object> resolveValues(final Map<Adaptation, Object> adaptationToProxy, final Class<?> beanType, final Adaptation adaptation)
			throws InstantiationException, IllegalAccessException {
		Map<Method, Object> values = new HashMap<>(2);
		for (Method method : ReflectionImpl.listAbstractGetters(beanType)) {
			values.put(method, ReflectionImpl.doInvocation(adaptation, method, (t, a) -> getProxy(adaptationToProxy, t, a)));
		}
		return values;
	}

	@Override
	protected Object invokeDefault(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return specialHandles.get(method).bindTo(proxy).invokeWithArguments(args);
	}

	@Override
	protected Object invokeOther(final Object proxy, final Method method, final Object[] args) throws Throwable {
		Object val = values.get(method);
		if (val == null && !values.containsKey(method)) {
			throw new UnsupportedOperationException(method.toGenericString());
		}
		return val;
	}

	@Override
	protected String paramString() {
		return id.toString();
	}

	@Override
	protected int invokeHashCode() {
		return id.hashCode();
	}

	@Override
	protected boolean invokeEquals(final InvocationHandler otherHandler, final Object other) {
		if (otherHandler instanceof EagerReadInvocationHandler) {
			return this.beanType == ((EagerReadInvocationHandler<?>) otherHandler).beanType && this.id.equals(((EagerReadInvocationHandler<?>) otherHandler).id);
		}
		return false;
	}

}
