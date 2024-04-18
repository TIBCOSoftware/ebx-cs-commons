package com.tibco.ebx.cs.commons.component.permission;

/**
 * @author Mickaël Chevalier
 */
public class ChildDataSpaceOnlyServicePermission extends MasterOrChildDataSpaceOnlyServicePermission {
	public ChildDataSpaceOnlyServicePermission() {
		allowInMaster = false;
	}
}