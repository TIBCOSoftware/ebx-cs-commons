/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Represents exceptions that occur due to technical issues such as system errors,
 * or unexpected conditions in the application's runtime environment.
 * <p>These exceptions typically indicate problems that are not related to the business logic but
 * to the underlying
 * system.</p>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class EBXCommonsTechnicalException extends EBXCommonsException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5435259904191166783L;

	/**
	 * Constructs a new {@code EBXCommonsTechnicalException} with the specified message and cause.
	 *
	 * @param message message
	 * @param cause throwable cause of this exception
	 */
	public EBXCommonsTechnicalException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@code EBXCommonsTechnicalException} with the specified message.
	 *
	 * @param message message describing the exception
	 */
	public EBXCommonsTechnicalException(final String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code EBXCommonsTechnicalException} with the specified cause.
	 *
	 * @param cause throwable cause of this exception
	 */
	public EBXCommonsTechnicalException(final Throwable cause) {
		super(cause);
	}
}
