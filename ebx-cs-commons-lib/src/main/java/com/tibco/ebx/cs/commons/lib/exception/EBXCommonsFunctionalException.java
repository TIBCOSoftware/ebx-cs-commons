package com.tibco.ebx.cs.commons.lib.exception;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Functional exception <br>
 * Specialized functional exceptions should extend this class
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public abstract class EBXCommonsFunctionalException extends EBXCommonsException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -789937155376130305L;
	private static final String MESSAGE_NOT_DEFINED_FOR_REASON = "Message not defined for reason : ";
	private static final String RESOURCE_BUNDLE_NOT_FOUND = "ResourceBundle not found : ";
	private static final String REASON_NOT_SET_ON_ERROR = "Reason not set on error, please check error construction.";

	protected static final String DEFAULT_ERROR_BUNDLE = "commonsErrorMessages";

	/** Logger */
	private static final Logger LOG = Logger.getLogger(EBXCommonsFunctionalException.class.getName());

	/**
	 *
	 */
	private final EBXCommonsFunctionalReason functionalReason;

	private Serializable[] args;

	/**
	 * Constructor
	 * 
	 * @param functionalReason the reason
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason) {
		super();
		this.functionalReason = functionalReason;
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason the reason
	 * @param args             arguments
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Serializable... args) {
		super();
		this.functionalReason = functionalReason;
		this.args = args;
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason the reason
	 * @param e                Exception
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Exception e) {
		super(e);
		this.functionalReason = functionalReason;
	}

	/**
	 * Constructor
	 * 
	 * @param functionalReason the reason
	 * @param e                Exception
	 * @param args             arguments
	 */
	public EBXCommonsFunctionalException(final EBXCommonsFunctionalReason functionalReason, final Exception e, final Serializable... args) {
		super(e);
		this.functionalReason = functionalReason;
		this.args = args;
	}

	/**
	 * Get internationalized message for this error
	 *
	 * @return internationalized message
	 */
	public String getInternationalizedMessage() {
		return this.getInternationalizedMessage(Locale.getDefault());
	}

	/**
	 * The name of the error bundle <br>
	 *
	 * @return name of the bundle to use
	 */
	public abstract String getErrorBundle();

	/**
	 * Get internationalized message for this error
	 *
	 * @param locale Locale
	 * @return internationalized message
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

	@Override
	public String getLocalizedMessage() {
		return this.getInternationalizedMessage();
	}

	@Override
	public String getMessage() {
		return this.getInternationalizedMessage();
	}

	/**
	 * Default Functional Exception
	 *
	 */
	private static class DefaultFunctionalException extends EBXCommonsFunctionalException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2914963646217649626L;
		private static final String FUNCTIONAL_ERROR = "Functional error";

		/**
		 *
		 */
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
