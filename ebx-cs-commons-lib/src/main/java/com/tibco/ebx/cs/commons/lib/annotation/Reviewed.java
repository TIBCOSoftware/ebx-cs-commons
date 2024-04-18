package com.tibco.ebx.cs.commons.lib.annotation;

import java.lang.annotation.Documented;

/**
 * Annotation for a review on a class
 * 
 * @author Mickaël Chevalier
 */
@Documented
public @interface Reviewed {

	/**
	 * Reviewer
	 * 
	 * @return reviewer
	 */
	abstract String value();
}
