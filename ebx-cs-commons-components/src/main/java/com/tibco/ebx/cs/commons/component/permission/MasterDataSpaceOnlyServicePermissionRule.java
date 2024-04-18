package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.ui.selection.DataspaceEntitySelection;

/**
 * @author Mickaël Chevalier
 */
public class MasterDataSpaceOnlyServicePermissionRule<S extends DataspaceEntitySelection> extends MasterOrChildDataSpaceOnlyServicePermissionRule<S> {
	public MasterDataSpaceOnlyServicePermissionRule() {
		allowInChild = false;
	}
}