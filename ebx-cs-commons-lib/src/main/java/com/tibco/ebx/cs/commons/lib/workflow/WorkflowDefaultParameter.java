/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.workflow;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Enumeration of the commonly used workflow data context parameters.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public enum WorkflowDefaultParameter {
	/**
	 * Parameter "initialDataspace".
	 *
	 * @since 1.8.0
	 */
	INITIAL_DATASPACE("initialDataspace"),

	/**
	 * Parameter "dataspace".
	 *
	 * @since 1.8.0
	 */
	DATASPACE("dataspace"),

	/**
	 * Parameter "dataset".
	 *
	 * @since 1.8.0
	 */
	DATASET("dataset"),

	/**
	 * Parameter "tableXpath".
	 *
	 * @since 1.8.0
	 */
	TABLE_XPATH("tableXpath"),

	/**
	 * Parameter "recordXpath".
	 *
	 * @since 1.8.0
	 */
	RECORD_XPATH("recordXpath");

	/**
	 * Static method to get the enum item of the corresponding string value.
	 *
	 * @param pParameter the string value of the parameter.
	 * @return the corresponding enum item.
	 * @since 1.8.0
	 */
	public static Optional<WorkflowDefaultParameter> getParameter(final String pParameter) {
		if (pParameter == null || pParameter.trim().isEmpty()) {
			return Optional.empty();
		}

		List<WorkflowDefaultParameter> parameters = Arrays.asList(WorkflowDefaultParameter.values());
		for (WorkflowDefaultParameter parameter : parameters) {
			if (pParameter.equals(parameter.getStringValue())) {
				return Optional.of(parameter);
			}
		}
		return Optional.empty();
	}

	private final String value;

	private WorkflowDefaultParameter(final String pValue) {
		this.value = pValue;
	}

	/**
	 * Return the string value of the enum item.
	 *
	 * @return the string value of the enum item.
	 * @since 1.8.0
	 */
	public String getStringValue() {
		return this.value;
	}
}
