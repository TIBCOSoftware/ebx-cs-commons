package com.tibco.ebx.cs.commons.lib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Utility to read commons.properties file carrying all properties of ebx-cs-commons.
 */
public final class CommonsProperties {

	private CommonsProperties() {
		super();
	}

	private static final String PROPERTIES_FILE_NAME = "com/tibco/ebx/cs/commons/utils/commons.properties";
	private static final String PROPERTY_PREFIX = "ebx.cs.commons.";
	private static final String VERSION_PROPERTY = PROPERTY_PREFIX + "version";

	private static Properties properties;

	public static Optional<String> getVersion() {
		return Optional.ofNullable(getCommonsProperties().getProperty(VERSION_PROPERTY));
	}

	private static Properties getCommonsProperties() {
		if (properties == null) {
			properties = new Properties();

			InputStream inputStream = CommonsProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME);

			if (inputStream != null) {
				try {
					properties.load(inputStream);
				} catch (IOException ex) {
					CommonsLogger.getLogger().error("Cannot load Commons properties file", ex);
				}
			} else {
				CommonsLogger.getLogger().error("Commons properties file not found");
			}
		}
		return properties;
	}
}
