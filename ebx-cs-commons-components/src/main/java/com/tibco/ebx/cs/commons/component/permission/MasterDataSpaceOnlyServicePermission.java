package com.tibco.ebx.cs.commons.component.permission;

/***
 * @author Mickaël Chevalier
 */
public class MasterDataSpaceOnlyServicePermission extends MasterOrChildDataSpaceOnlyServicePermission {
	public MasterDataSpaceOnlyServicePermission() {
		allowInChild = false;
	}
}