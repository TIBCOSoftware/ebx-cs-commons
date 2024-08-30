/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Represents a functional exception that occurs within the EBX CS Commons library.
 * <p>
 * Specialized functional exceptions should extend this class. It provides a
 * mechanism to handle exceptions that are tied to specific functional reasons,
 * enabling internationalized error messages and consistent exception management.
 * </p>
 * <p>
 * This class also manages the retrieval of localized error messages based on
 * the functional reason and arguments provided at runtime.
 * </p>
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public abstract class EBXCommonsFunctionalException extends EBXCommonsException {

	private static final long serialVersionUID = -789937155376130305L;
	private static final String MESSAGE_NOT_DEFINED_FOR_REASON = "Message not defined for the " +
			"following reason: ";
	private static final String RESOURCE_BUNDLE_NOT_FOUND = "ResourceBundle not found: ";
	private static final String REASON_NOT_SET_ON_ERROR = "Reason not set on error, please check error construction.";

	protected static final String DEFAULT_ERROR_BUNDLE = "commonsErrorMessages";

	private static final Logger LOG = Logger.getLogger(EBXCommonsFunctionalException.class.getName());

	private final EBXCommonsFunctionalReason functionalReason;

	private Serializable[] args;

	/**
	 * Constructs a new {@code EBXCommonsFunctionalException} with the specified
	 * functional reason.
	 *
	 * @param functionalReason the reason for the exception
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason) {
		super();
		this.functionalReason = functionalReason;
	}

	/**
	 * Constructs a new {@code EBXCommonsFunctionalException} with the specified
	 * functional reason and arguments.
	 *
	 * @param functionalReason the reason for the exception
	 * @param args arguments providing additional context for the exception
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super();
		this.functionalReason = functionalReason;
		this.args = args;
	}

	/**
	 * Constructs a new {@code EBXCommonsFunctionalException} with the specified
	 * functional reason and underlying exception.
	 *
	 * @param functionalReason the reason for the exception
	 * @param e the underlying exception that caused this functional exception
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Exception e) {
		super(e);
		this.functionalReason = functionalReason;
	}

	/**
	 * Constructs a new {@code EBXCommonsFunctionalException} with the specified
	 * functional reason, underlying exception, and additional arguments.
	 *
	 * @param functionalReason the reason for the exception
	 * @param e the underlying exception that caused this functional exception
	 * @param args arguments providing additional context for the exception
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Exception e, final Serializable... args) {
		super(e);
		this.functionalReason = functionalReason;
		this.args = args;
	}

	/**
	 * Returns the internationalized message for this error based on the default
	 * locale.
	 *
	 * @return the internationalized error message
	 */
	public String getInternationalizedMessage() {
		return this.getInternationalizedMessage(Locale.getDefault());
	}

	/**
	 * Returns the name of the error bundle associated with this exception.
	 *
	 * @return the name of the bundle to use for error messages
	 */
	public abstract String getErrorBundle();

	/**
	 * Returns the internationalized message for this error based on the specified
	 * locale.
	 *
	 * @param locale the locale to use for retrieving the error message
	 * @return the internationalized error message
	 */
	public String getInternationalizedMessage(final Locale locale) {
		DefaultFunctionalException def = new DefaultFunctionalException();
		String message = def.getInternationalizedMessage();
		String bundle = this.getErrorBundle();
		ResourceBundle errorBundle = ResourceBundle.getBundle(bundle, locale);
		if (errorBundle != null) {
			if (this.functionalReason != null) {
				String errorMessage = null;
				try {
					errorMessage = errorBundle.getString(this.functionalReason.getKey());
				} catch (MissingResourceException e) {
					LOG.info(MESSAGE_NOT_DEFINED_FOR_REASON + this.functionalReason.getKey());
				}
				if (errorMessage != null && !"".equals(errorMessage)) {
					message = errorMessage;
				}
			} else {
				LOG.severe(REASON_NOT_SET_ON_ERROR);
			}
		} else {
			LOG.severe(RESOURCE_BUNDLE_NOT_FOUND + bundle);
		}
		return MessageFormat.format(message, (Object[]) this.args);
	}

	/**
	 * Returns the localized message for this exception.
	 * @return localized exception message
	 */
	@Override
	public String getLocalizedMessage() {
		return this.getInternationalizedMessage();
	}

	/**
	 * Returns the message for this exception.
	 * @return the exception message
	 */
	@Override
	public String getMessage() {
		return this.getInternationalizedMessage();
	}

	/**
	 * Provides the efault implementation of a functional exception.
	 *
	 */
	private static class DefaultFunctionalException extends EBXCommonsFunctionalException {

		private static final long serialVersionUID = -2914963646217649626L;
		private static final String FUNCTIONAL_ERROR = "Functional error";


		private DefaultFunctionalException() {
			super(EBXCommonsFunctionalReason.DEFAULT_ERROR);
		}

		@Override
		public String getInternationalizedMessage() {
			return FUNCTIONAL_ERROR;
		}

		@Override
		public String getErrorBundle() {
			return DEFAULT_ERROR_BUNDLE;
		}
	}
}
