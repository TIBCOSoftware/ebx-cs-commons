/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;

/**
 * Represents an exception that is thrown when a requested resource (e.g., a file, database record, or service)
 * is not found.
 * <p>
 * This exception can signal that the resource does not exist or cannot be located.
 * </p>
 *
 *
 *<p>Two constructors allow for flexibility in passing functional reasons and additional
 * arguments:</p>
 * <ul>
 *   <li>{@link #EBXResourceNotFoundException(EBXCommonsFunctionalReason)}
 *       accepts a functional reason as a parameter, which describes the nature
 *       of the error.</li>
 *   <li>{@link #EBXResourceNotFoundException(EBXCommonsFunctionalReason, Serializable...)}
 *       accepts a functional reason and a variable number of additional arguments,
 *       which allow detailed error descriptions.</li>
 * </ul>
 *
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class EBXResourceNotFoundException extends EBXCommonsFunctionalException {

	/**
	 *
	 */
	private static final long serialVersionUID = 566645672978929997L;

	/**
	 * Constructs a new {@code EBXResourceNotFoundException} with the specified functional reason.
	 *
	 * @param functionalReason functional reason
	 */
	public EBXResourceNotFoundException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructs a new {@code EBXResourceNotFoundException} with the specified functional reason and additional
	 * arguments.
	 *
	 * @param functionalReason functional reason
	 * @param args             arguments
	 */
	public EBXResourceNotFoundException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super(functionalReason, args);
	}

	/**
	 * Returns the default error bundle.
	 * @return the error bundle.
	 */
	@Override
	public String getErrorBundle() {
		return EBXCommonsFunctionalException.DEFAULT_ERROR_BUNDLE;
	}

}
