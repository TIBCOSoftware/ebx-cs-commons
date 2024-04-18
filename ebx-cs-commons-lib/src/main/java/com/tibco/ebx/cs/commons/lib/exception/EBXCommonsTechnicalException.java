package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Technical exception <br>
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
	 * Constructor
	 * 
	 * @param message message
	 * @param cause   throwable cause
	 */
	public EBXCommonsTechnicalException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * 
	 * @param message message
	 */
	public EBXCommonsTechnicalException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param cause throwable cause
	 */
	public EBXCommonsTechnicalException(final Throwable cause) {
		super(cause);
	}
}
