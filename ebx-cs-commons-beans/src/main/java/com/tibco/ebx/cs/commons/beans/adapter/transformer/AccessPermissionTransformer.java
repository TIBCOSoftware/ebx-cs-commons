
package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.service.AccessPermission;

/**
 * 
 * @author Gilles Mayer
 */
public class AccessPermissionTransformer implements Function<String, AccessPermission> {
	@Override
	public AccessPermission apply(final String t) {
		return t == null ? null : AccessPermission.parseFlag(t);
	}
}
