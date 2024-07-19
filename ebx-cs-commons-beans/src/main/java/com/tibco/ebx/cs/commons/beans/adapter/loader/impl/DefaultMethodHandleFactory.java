/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Implementation of the finding of default methods' handles used by proxies
 * invocation handler
 *
 * @author Gilles Mayer
 */
enum DefaultMethodHandleFactory {

	/**
	 * Compatible with java 1.8, 9, 11 but deprecated since 9. Private constructor
	 * takes three arguments in java 14. Very slow with the latest updates 1.8
	 */
	PRIVATE_CONSTRUCTOR {
		@Override
		MethodHandle findSpecial(final Class<?> iface, final Method method) throws ReflectiveOperationException {
			Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
					.getDeclaredConstructor(Class.class, Integer.TYPE);
			if (constructor.trySetAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(iface, MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE)
					.unreflectSpecial(method, iface);
		}
	},

	/**
	 * Compatible with java 9, 11, 14
	 */
	FIND_SPECIAL {
		@Override
		MethodHandle findSpecial(final Class<?> iface, final Method method) throws ReflectiveOperationException {
			return MethodHandles.lookup().findSpecial(iface, method.getName(),
					MethodType.methodType(method.getReturnType(), method.getParameterTypes()), iface);
		}
	};

	public static DefaultMethodHandleFactory getPrefered() {
		if (System.getProperty("java.version").startsWith("1.")) {
			return PRIVATE_CONSTRUCTOR;
		} else {
			return FIND_SPECIAL;
		}
	}

	abstract MethodHandle findSpecial(Class<?> iface, Method method) throws ReflectiveOperationException;

}
