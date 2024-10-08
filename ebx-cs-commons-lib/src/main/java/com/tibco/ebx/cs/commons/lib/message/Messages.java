/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.message;

import java.util.Locale;
import java.util.MissingResourceException;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.Severity;
import com.onwbp.base.text.UserMessage;
import com.onwbp.base.text.UserMessageRef;
import com.onwbp.base.text.UserMessageString;

/**
 * The Messages class handles getting localized String or UserMessage from a properties file.<br>
 * <br>
 * The properties file shall be located in the same package of the class passed in argument.<br>
 * The properties file shall be named 'Messages_xx.properties' with xx the locale.<br>
 * <br>
 * Example:
 *
 * <pre>
 * String title = Messages.get(this.getClass(), Locale.getDefault(), "title");
 * UserMessage label = Messages.getInfo(this.getClass(), "label");
 * </pre>
 *
 * @author Aurélien Ticot
 * @since 1.1.0
 */
@SuppressWarnings("rawtypes")
public final class Messages {
	private static final String PROPERTIES_FILE_NAME = "Messages";

	/**
	 * Returns a localized message from the properties file.<br>
	 * <br>
	 * The properties file shall be located in the same package of the class passed in argument.<br>
	 * The properties file shall be named 'Messages_xx.properties' with xx the locale.
	 *
	 * @param pBundleClass a class in the same package as the properties file
	 * @param pLocale      the locale
	 * @param pKey         the key in the properties file
	 * @param pValues      the values to insert in the message
	 * @return the localized message as String
	 * @since 1.1.0
	 */
	public static String get(final Class pBundleClass, final Locale pLocale, final String pKey, final Object... pValues) {
		try {
			UserMessage message = Messages.getInfo(pBundleClass, pKey, pValues);
			return message.formatMessage(pLocale);
		} catch (final MissingResourceException exception) {
			return pKey;
		}
	}

	/**
	 * Returns a localized message from the properties file.<br>
	 * <br>
	 * The properties file shall be located in the same package of the class passed in argument.<br>
	 * The properties file shall be named 'Messages_xx.properties' with xx the locale.
	 *
	 * @param pBundleClass a class in the same package as the properties file
	 * @param pSeverity    the severity
	 * @param pKey         the key in the properties file
	 * @param pValues      the values to insert in the message
	 * @return the localized message as UserMessage
	 * @since 1.1.0
	 */
	public static UserMessage get(final Class pBundleClass, final Severity pSeverity, final String pKey, final Object... pValues) {
		final String bundleName = pBundleClass.getPackage().getName() + "." + Messages.PROPERTIES_FILE_NAME;
		final ClassLoader classLoader = pBundleClass.getClassLoader();
		return new UserMessageRef(pSeverity, pKey, bundleName, pValues, classLoader);
	}

	/**
	 * Returns an Error localized message (set with an error severty) from the properties file.<br>
	 * <br>
	 * The properties file shall be located in the same package of the class passed in argument.<br>
	 * The properties file shall be named 'Messages_xx.properties' with xx the locale.
	 *
	 * @param pBundleClass a class in the same package as the properties file
	 * @param pKey         the key in the properties file
	 * @param pValues      the values to insert in the message
	 * @return the localized Error message as UserMessage
	 * @since 1.1.0
	 */
	public static UserMessage getError(final Class pBundleClass, final String pKey, final Object... pValues) {
		return Messages.get(pBundleClass, Severity.ERROR, pKey, pValues);
	}

	/**
	 * Returns an Information localized message (set with an info severty) from the properties file. <br>
	 * <br>
	 * The properties file shall be located in the same package of the class passed in argument.<br>
	 * The properties file shall be named 'Messages_xx.properties' with xx the locale.
	 *
	 * @param pBundleClass a class in the same package as the properties file
	 * @param pKey         the key in the properties file
	 * @param pValues      the values to insert in the message
	 * @return the localized Information message as UserMessage
	 * @since 1.1.0
	 */
	public static UserMessage getInfo(final Class pBundleClass, final String pKey, final Object... pValues) {
		return Messages.get(pBundleClass, Severity.INFO, pKey, pValues);
	}

	/**
	 * Returns a Warning localized message (set with an warning severty) from the properties file. <br>
	 * <br>
	 * The properties file shall be located in the same package of the class passed in argument.<br>
	 * The properties file shall be named 'Messages_xx.properties' with xx the locale.
	 *
	 * @param pBundleClass a class in the same package as the properties file
	 * @param pKey         the key in the properties file
	 * @param pValues      the values to insert in the message
	 * @return the localized Warning message as UserMessage
	 * @since 1.1.0
	 */
	public static UserMessage getWarning(final Class pBundleClass, final String pKey, final Object... pValues) {
		return Messages.get(pBundleClass, Severity.WARNING, pKey, pValues);
	}

	/**
	 * Return a user message with the specified message and severity where the message is prefixed by information about the record.
	 *
	 * @param record   the record
	 * @param msg      the simple message
	 * @param severity the desired severity
	 * @return a UserMessage
	 */
	public static UserMessageString createUserMessage(final Adaptation record, final String msg, final Severity severity) {
		String msgTxt = "Record " + record.getOccurrencePrimaryKey().format() + " in the " + record.getContainerTable().getTableNode().getLabel(Locale.getDefault()) + " Table, "
				+ record.getLabel(Locale.getDefault()) + ": " + msg;

		return createUserMessage(msgTxt, severity);
	}

	/**
	 * Create and return a UserMessage with the specified message and severity
	 * 
	 * @param msg      the simple message
	 * @param severity the desired severity
	 * @return new UserMessage
	 */
	public static UserMessageString createUserMessage(final String msg, final Severity severity) {
		if (Severity.FATAL == severity) {
			return UserMessage.createFatal(msg);
		}
		if (Severity.ERROR == severity) {
			return UserMessage.createError(msg);
		}
		if (Severity.WARNING == severity) {
			return UserMessage.createWarning(msg);
		}
		return UserMessage.createInfo(msg);
	}

	private Messages() {
	}
}
