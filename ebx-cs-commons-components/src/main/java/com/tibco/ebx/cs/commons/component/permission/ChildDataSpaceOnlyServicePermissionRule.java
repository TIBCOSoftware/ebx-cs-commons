/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.ui.selection.DataspaceEntitySelection;

/**
 * @author Mickaël Chevalier
 */
public class ChildDataSpaceOnlyServicePermissionRule<S extends DataspaceEntitySelection> extends MasterOrChildDataSpaceOnlyServicePermissionRule<S> {
	public ChildDataSpaceOnlyServicePermissionRule() {
		allowInMaster = false;
	}
}