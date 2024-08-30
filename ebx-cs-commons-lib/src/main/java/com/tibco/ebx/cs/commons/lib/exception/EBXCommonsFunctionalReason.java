/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Represents specific functional reasons for managing exceptions within
 * the EBX CS Commons library.
 *
 * <p>This enum categorizes and identifies different types of functional
 * errors that can occur during the EBX operation. Each constant
 * in this enum corresponds to a specific error scenario, with an associated key
 * that can be used for error handling, logging, or user messaging.</p>
 *
 * <p>The keys associated with each functional reason typically map to messages
 * in a resource bundle, enabling localization and standardized error reporting.</p>
 *
 * <p>This enum is primarily used in conjunction with
 * {@link EBXCommonsFunctionalException} to provide a clear and consistent way to
 * handle and report functional exceptions across the EBX CS Commons library.</p>
 *
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public enum EBXCommonsFunctionalReason {

	/**
	 * Represents a default error scenario, typically used as a fallback for
	 * unknown errors.
	 */
	DEFAULT_ERROR("default.error"),

	/**
	 * Indicates that a primary key member in a bean is null when it should not be.
	 */
	BEANS_PK_MEMBER_NULL("beans.pk.member.null"),

	/**
	 * Indicates that a required bean is not bound in the context.
	 */
	BEANS_NOT_BOUND("beans.not.bound"),

	/**
	 * Indicates that there are multiple linked records when only one was expected.
	 */
	BEANS_MANY_LINKED_RECORDS("beans.many.linked.records"),

	// ebx-commons-lib
	/**
	 * Indicates that the specified dataspace resource could not be found.
	 */
	RESOURCE_DATASPACE_NOT_FOUND("resource.dataspace.not.found"),

	/**
	 * Indicates that the specified dataset resource could not be found.
	 */
	RESOURCE_DATASET_NOT_FOUND("resource.dataset.not.found"),

	/**
	 * Indicates that the specified table resource could not be found.
	 */
	RESOURCE_TABLE_NOT_FOUND("resource.table.not.found"),

	/**
	 * Indicates that the specified record resource could not be found.
	 */
	RESOURCE_RECORD_NOT_FOUND("resource.record.not.found"),

	/**
	 * Indicates that the specified workflow interaction could not be found.
	 */
	RESOURCE_INTERACTION_NOT_FOUND("resource.interaction.not.found"),

	/**
	 * Indicates that the provided XPath expression is not unique or does not
	 * uniquely identify the expected element.
	 */
	RESOURCE_XPATH_EXPRESSION_NOT_UNIQUE(
			"resource.xpath.expression.not.discriminating"
	),

	/**
	 * Indicates that a required workflow interaction parameter could not be
	 * found in the configuration.
	 */
	WORKFLOW_INTERACTION_PARAM_NOT_FOUND(
			"workflow.configuration.interaction.param.not.found"
	),

	/**
	 * Indicates that a required workflow variable could not be found in the
	 * configuration.
	 */
	WORKFLOW_VARIABLE_NOT_FOUND(
			"workflow.configuration.variable.not.found"
	);


	private final String key;

	private EBXCommonsFunctionalReason(final String key) {
		this.key = key;
	}

	/**
	 * Returns the key associated with this functional reason.
	 *
	 * @return the key string, which you can use for error messages and logging
	 */

	public String getKey() {
		return this.key;
	}

}
