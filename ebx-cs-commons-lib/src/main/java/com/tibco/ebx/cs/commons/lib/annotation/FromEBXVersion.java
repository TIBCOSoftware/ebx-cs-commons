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
