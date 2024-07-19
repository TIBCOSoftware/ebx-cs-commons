/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps a getter method to an EBX field with a table reference (a.k.a. foreign key). <br>
 * Required when the field path or cannot be inferred from the getter method name.
 * 
 * @author Gilles Mayer
 */
@Documented
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface TableRef {
	/** Alias for {@link #path()} */
	String value() default "";

	/**
	 * Path of the field in adaptation. Required when the field path cannot be inferred from the getter name.
	 */
	String path() default "";

	/**
	 * Path of the group of the field in adaptation. Required when the last step of the field path can be inferred from the getter name but not the first (group) part of the path.
	 */
	String group() default "";
}
