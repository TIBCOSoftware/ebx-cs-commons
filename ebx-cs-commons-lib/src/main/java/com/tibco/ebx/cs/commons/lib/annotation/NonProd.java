package com.tibco.ebx.cs.commons.lib.annotation;

import java.lang.annotation.Documented;

/**
 * Annotation for non-production usage
 * 
 * @author Mickaël Chevalier
 */
@Documented
public @interface NonProd {

	/**
	 * Reason for non-production flag
	 * 
	 * @return the reason
	 */
	abstract String reason();

	/**
	 * Required action
	 * 
	 * @return action
	 */
	abstract String todo();
}
