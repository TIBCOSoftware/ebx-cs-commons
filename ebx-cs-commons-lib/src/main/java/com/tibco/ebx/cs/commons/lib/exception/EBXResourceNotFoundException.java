package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;

/**
 * Resource not found exception <br>
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
	 * Constructor
	 * 
	 * @param functionalReason functional reason
	 */
	public EBXResourceNotFoundException(final EBXCommonsFunctionalReason functionalReason) {
		super(functionalReason);
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason functional reason
	 * @param args             arguments
	 */
	public EBXResourceNotFoundException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super(functionalReason, args);
	}

	@Override
	public String getErrorBundle() {
		return EBXCommonsFunctionalException.DEFAULT_ERROR_BUNDLE;
	}

}
