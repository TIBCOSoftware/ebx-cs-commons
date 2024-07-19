/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Main exception for EBX Commons
 *
 * @author Mickaël Chevalier
 * @since 1.1.0
 */
public class EBXCommonsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4007951756325585186L;
	private final boolean functional;

	/**
	 * Constructor
	 * 
	 * @param message message
	 * @param cause   throwable cause
	 */
	public EBXCommonsException(final String message, final Throwable cause) {
		super(message, cause);
		this.functional = EBXCommonsException.checkFunctionalException(cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message message
	 */
	public EBXCommonsException(final String message) {
		super(message);
		this.functional = this.checkFunctionalException();
	}

	/**
	 * Constructor
	 * 
	 */
	public EBXCommonsException() {
		super();
		this.functional = this.checkFunctionalException();
	}

	/**
	 * Constructor
	 * 
	 * @param cause throwable cause
	 */
	public EBXCommonsException(final Throwable cause) {
		super(cause);
		this.functional = checkFunctionalException(cause);
	}

	public boolean isFunctional() {
		return this.functional;
	}

	public boolean isTechnical() {
		return !this.functional;
	}

	private boolean checkFunctionalException() {
		return this instanceof EBXCommonsFunctionalException;

	}

	private static boolean checkFunctionalException(final Throwable cause) {
		return cause instanceof EBXCommonsFunctionalException;
	}

	@Override
	public String getMessage() {
		if (this.isFunctional()) {
			return ((EBXCommonsFunctionalException) this).getInternationalizedMessage();
		} else {
			return super.getMessage();
		}
	}

}
