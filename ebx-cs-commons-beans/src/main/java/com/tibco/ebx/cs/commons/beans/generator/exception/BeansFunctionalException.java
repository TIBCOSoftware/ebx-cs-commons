/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.exception;

import java.io.Serializable;

import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsFunctionalException;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsFunctionalReason;

/**
 * Functional exception
 * 
 * @author Mickaël Chevalier
 * @since 1.1.5
 */
public class BeansFunctionalException extends EBXCommonsFunctionalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4132708329000452230L;
	private static final String ERROR_BUNDLE = "EBXCommonsBeansErrorMessages";

	/**
	 * Constructor
	 * 
	 * @param functionalReason functional Reason
	 */
	public BeansFunctionalException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason functional Reason
	 * @param args             args
	 */
	public BeansFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super(functionalReason, args);
	}

	@Override
	public String getErrorBundle() {
		return ERROR_BUNDLE;
	}

}
