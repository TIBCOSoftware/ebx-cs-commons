/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;

/**
 * Resource not identified exception <br>
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
	 * Constructor
	 * 
	 * @param functionalReason functional reason
	 */
	public EBXResourceNotIdentifiedException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason functional reason
	 * @param args             arguments
	 */
	public EBXResourceNotIdentifiedException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super(functionalReason, args);
	}

	@Override
	public String getErrorBundle() {
		return EBXCommonsFunctionalException.DEFAULT_ERROR_BUNDLE;
	}

}
