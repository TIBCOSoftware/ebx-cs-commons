/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.workflow;

/**
 * Workflow constants
 * 
 * @author Mickaël Chevalier
 */
public final class WorkflowConstants {
	private WorkflowConstants() {
		super();
	}

	public static final String VAR_DATASPACE = "dataspace";
	public static final String VAR_DATASET = "dataset";
	public static final String VAR_RECORD = "record";
	public static final String VAR_TABLE = "table";
	public static final String VAR_WORKSPACE = "workspace";

	public static final String PARAM_BRANCH = "branch";
	public static final String PARAM_INSTANCE = "instance";
	public static final String PARAM_XPATH = "xpath";
	public static final String PARAM_WORKSPACE = "workspace";

	public static final String PARAM_CREATED = "created";
}