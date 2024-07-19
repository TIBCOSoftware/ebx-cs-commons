/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.exception;

import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsTechnicalException;

/**
 * Technical exception <br>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class BeansTechnicalException extends EBXCommonsTechnicalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6443507730841844600L;

	/**
	 * Constructor
	 * 
	 * @param message message
	 * @param cause   throwable cause
	 */
	public BeansTechnicalException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message message
	 */
	public BeansTechnicalException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause throwable cause
	 */
	public BeansTechnicalException(final Throwable cause) {
		super(cause);
	}
}
