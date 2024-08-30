/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;

/**
 * Indicates an error in the workflow configuration.
 *
 * <p>Two constructors allow for flexibility in passing functional reasons and additional
 * arguments:</p>
 * <ul>
 *   <li>{@link #WorkflowConfigurationException(EBXCommonsFunctionalReason)}
 *       accepts a functional reason as a parameter, which describes the nature
 *       of the error.</li>
 *   <li>{@link #WorkflowConfigurationException(EBXCommonsFunctionalReason, Serializable...)}
 *       accepts a functional reason and a variable number of additional arguments,
 *       which allow detailed error descriptions.</li>
 * </ul>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public class WorkflowConfigurationException extends EBXCommonsFunctionalException {

	/**
	 *
	 */
	private static final long serialVersionUID = 8282394265015525982L;

	/**
	 * Constructs a new {@code WorkflowConfigurationException} with the specified functional reason.
	 *
	 * @param functionalReason functional reason for this exception
	 */
	public WorkflowConfigurationException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructs a new {@code WorkflowConfigurationException} with the specified functional reason
	 * and additional arguments.
	 *
	 * @param functionalReason functional reason for this exception
	 * @param args one or more arguments
	 */
	public WorkflowConfigurationException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
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
