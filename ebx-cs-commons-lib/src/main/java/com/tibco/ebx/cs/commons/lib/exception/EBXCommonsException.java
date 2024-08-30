/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Serves as the base class for all exceptions within the EBX CS Commons library.
 *
 * @author Mickaël Chevalier
 * @since 1.1.0
 */
public class EBXCommonsException extends Exception {

	private static final long serialVersionUID = 4007951756325585186L;
	private final boolean functional;

	/**
	 * Constructs a new {@code EBXCommonsException} with the specified detail
	 * message and cause.
	 *
	 * @param message the detailed message
	 * @param cause   the cause of the exception
	 */
	public EBXCommonsException(final String message, final Throwable cause) {
		super(message, cause);
		this.functional = EBXCommonsException.checkFunctionalException(cause);
	}

	/**
	 * Constructs a new {@code EBXCommonsException} with the specified
	 * message.
	 *
	 * @param message the detailed message
	 */
	public EBXCommonsException(final String message) {
		super(message);
		this.functional = this.checkFunctionalException();
	}

	/**
	 * Constructs a new {@code EBXCommonsException} with no detail message or cause.
	 * 
	 */
	public EBXCommonsException() {
		super();
		this.functional = this.checkFunctionalException();
	}

	/**
	 * Constructs a new {@code EBXCommonsException} with the specified cause.
	 *
	 * @param cause the cause of the exception
	 *
	 */
	public EBXCommonsException(final Throwable cause) {
		super(cause);
		this.functional = checkFunctionalException(cause);
	}

	/**
	 * Returns {@code true} if this exception is considered functional.
	 *
	 * @return {@code true} if this exception is functional; {@code false} otherwise
	 */
	public boolean isFunctional() {
		return this.functional;
	}

	/**
	 * Returns {@code true} if this exception is considered technical.
	 *
	 * @return {@code true} if this exception is technical; {@code false} otherwise
	 */
	public boolean isTechnical() {
		return !this.functional;
	}

	private boolean checkFunctionalException() {
		return this instanceof EBXCommonsFunctionalException;

	}

	private static boolean checkFunctionalException(final Throwable cause) {
		return cause instanceof EBXCommonsFunctionalException;
	}

	/**
	 * Returns the message string of this exception.
	 * <p>
	 * If this exception is functional, the internationalized message is returned.
	 * Otherwise, the standard message is returned.
	 * </p>
	 *
	 * @return the message string
	 */
	@Override
	public String getMessage() {
		if (this.isFunctional()) {
			return ((EBXCommonsFunctionalException) this).getInternationalizedMessage();
		} else {
			return super.getMessage();
		}
	}

}
