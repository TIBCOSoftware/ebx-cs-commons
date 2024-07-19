/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.annotation;

import java.lang.annotation.Documented;

/**
 * Annotation defining EBX minimal version required on an usage
 * 
 * @author Mickaël Chevalier
 */
@Documented
public @interface FromEBXVersion {

	/**
	 * Version
	 * 
	 * @return value
	 */
	abstract String value();
}
