/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.utils;

import com.orchestranetworks.service.LoggingCategory;

/**
 * Commons Logger class
 * 
 * @author Mickaël Chevalier
 */
public final class CommonsLogger {
	private static LoggingCategory logger;

	private CommonsLogger() {
	}

	public static LoggingCategory getLogger() {
		if (logger == null) {
			// throw message and define default logger
			LoggingCategory.getKernel().info("Please set logger for this module");
			return LoggingCategory.getKernel();
		}
		return logger;
	}

	public static void setLogger(final LoggingCategory logger) {
		CommonsLogger.logger = logger;
	}

}
