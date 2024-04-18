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
