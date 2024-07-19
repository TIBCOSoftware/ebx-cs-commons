/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

/**
 * @author Mickaël Chevalier
 */
public class ChildDataSpaceOnlyServicePermission extends MasterOrChildDataSpaceOnlyServicePermission {
	public ChildDataSpaceOnlyServicePermission() {
		allowInMaster = false;
	}
}