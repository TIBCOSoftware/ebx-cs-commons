/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;

/**
 * Represents an exception that is thrown when a resource cannot be identified, often due to insufficient or
 * ambiguous information.
 *
 * This exception is used when the application cannot determine which specific resource is being referenced.
 *
 *<p>Two constructors allow for flexibility in passing functional reasons and additional
 * arguments:</p>
 * <ul>
 *   <li>{@link #EBXResourceNotIdentifiedException(EBXCommonsFunctionalReason)}
 *       accepts a functional reason as a parameter, which describes the nature
 *       of the error.</li>
 *   <li>{@link #EBXResourceNotIdentifiedException(EBXCommonsFunctionalReason, Serializable...)}
 *       accepts a functional reason and a variable number of additional arguments,
 *       which allow detailed error descriptions.</li>
 * </ul>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class EBXResourceNotIdentifiedException extends EBXCommonsFunctionalException {

	/**
	 *
	 */
	private static final long serialVersionUID = -2155000502616847850L;

	/**
	 * Constructs a new {@code EBXResourceNotIdentifiedException} with the specified functional reason.
	 *
	 * @param functionalReason functional reason for this exception
	 */
	public EBXResourceNotIdentifiedException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructs a new {@code EBXResourceNotIdentifiedException} with the specified functional reason and additional
	 * arguments.
	 *
	 * @param functionalReason functional reason for this exception
	 * @param args one or more additional arguments
	 */
	public EBXResourceNotIdentifiedException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
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
