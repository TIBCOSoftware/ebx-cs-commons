/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.schema.Path;

/**
 * Specifies the paths for the MasterAccessRule
 * 
 * @author Mickaël Chevalier
 */
public abstract class MasterAccessRulePathConfig {
	protected abstract Path getAccessPermissionPath();

	protected abstract Path getAccessPermissionDataSpacePath();

	protected abstract Path getAccessPermissionDataSetPath();

	protected abstract Path getAccessPermissionTablePath();

	protected abstract Path getAccessPermissionConditionPath();

	protected abstract Path getAccessPermissionRolePath();

	protected abstract Path getAccessPermissionPermissionPath();
}
