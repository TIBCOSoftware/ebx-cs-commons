/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.annotation;

import java.lang.annotation.Documented;

/**
 * Annotation for EBX Private API Usage
 * 
 * @author Mickaël Chevalier
 */
@Documented
public @interface EBXPrivateAPI {

	/**
	 * Justification of the usage
	 * 
	 * @return justification of the usage
	 */
	abstract String justification();
}
