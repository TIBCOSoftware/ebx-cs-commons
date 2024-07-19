/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.exception;

/**
 * Functional Reasons for managing functional exceptions
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 */
public enum EBXCommonsFunctionalReason {

	//ebx-commons-beans
	DEFAULT_ERROR("default.error"),
	BEANS_PK_MEMBER_NULL("beans.pk.member.null"),
	BEANS_NOT_BOUND("beans.not.bound"),
	BEANS_MANY_LINKED_RECORDS("beans.many.linked.records"),
	//ebx-commons-lib
	RESOURCE_DATASPACE_NOT_FOUND("resource.dataspace.not.found"),
	RESOURCE_DATASET_NOT_FOUND("resource.dataset.not.found"),
	RESOURCE_TABLE_NOT_FOUND("resource.table.not.found"),
	RESOURCE_RECORD_NOT_FOUND("resource.record.not.found"),
	RESOURCE_INTERACTION_NOT_FOUND("resource.interaction.not.found"),
	RESOURCE_XPATH_EXPRESSION_NOT_UNIQUE("resource.xpath.expression.not.discriminating"),
	WORKFLOW_INTERACTION_PARAM_NOT_FOUND("workflow.configuration.interaction.param.not.found"),
	WORKFLOW_VARIABLE_NOT_FOUND("workflow.configuration.variable.not.found");

	private final String key;

	private EBXCommonsFunctionalReason(final String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

}
