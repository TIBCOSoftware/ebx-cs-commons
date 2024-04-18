package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.reflect.Method;

/**
 * 
 * @author Gilles Mayer
 */
public class ModelException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private Method method;

	public ModelException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ModelException(final String message) {
		super(message);
	}

	public ModelException(final Throwable cause) {
		super(cause);
	}

	public ModelException(final Method method, final String message) {
		super(method.toGenericString() + ": " + message);
		this.method = method;
	}

	public ModelException(final Method method, final String message, final Throwable cause) {
		super(method.toGenericString() + ": " + message, cause);
		this.method = method;
	}

	public ModelException(final Method method, final Throwable cause) {
		super(method.toGenericString() + ": " + cause.getMessage(), cause);
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

}
