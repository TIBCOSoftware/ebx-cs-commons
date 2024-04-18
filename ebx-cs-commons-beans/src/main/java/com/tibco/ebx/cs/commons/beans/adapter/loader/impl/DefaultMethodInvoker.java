package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Implementation of the default methods invocation in proxies
 *
 * @author Gilles Mayer
 */
enum DefaultMethodInvoker {

	/**
	 * Compatible with java 1.8, 9, 11 but deprecated since 9. Private constructor
	 * takes three arguments in java 14. Very slow with the latest updates 1.8
	 */
	PRIVATE_CONSTRUCTOR {
		@Override
		Object invokeDefaultMethod(final Class<?> iface, final Object proxy, final Method method, final Object[] args)
				throws Throwable {
			Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
					.getDeclaredConstructor(Class.class, Integer.TYPE);
			if (constructor.trySetAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(iface, MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE)
					.unreflectSpecial(method, iface).bindTo(proxy).invokeWithArguments(args);
		}
	},

	/**
	 * Compatible with java 9, 11, 14
	 */
	FIND_SPECIAL {
		@Override
		Object invokeDefaultMethod(final Class<?> iface, final Object proxy, final Method method, final Object[] args)
				throws Throwable {
			return MethodHandles.lookup()
					.findSpecial(iface, method.getName(),
							MethodType.methodType(method.getReturnType(), method.getParameterTypes()), iface)
					.bindTo(proxy).invokeWithArguments(args);
		}
	};

	public static DefaultMethodInvoker getPrefered() {
		if (System.getProperty("java.version").startsWith("1.")) {
			return PRIVATE_CONSTRUCTOR;
		} else {
			return FIND_SPECIAL;
		}
	}

	abstract Object invokeDefaultMethod(Class<?> iface, Object proxy, Method method, Object[] args) throws Throwable;

}
