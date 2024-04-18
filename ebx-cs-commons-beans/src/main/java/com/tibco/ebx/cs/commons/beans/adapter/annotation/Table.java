package com.tibco.ebx.cs.commons.beans.adapter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an interface type to an EBX table. Required when the path of the table cannot be inferred from the interface name.
 * 
 * @author Gilles Mayer
 */
@Documented
@Target(value = { ElementType.TYPE })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Table {
	/**
	 * Alias for {@link #path()}
	 */
	String value() default "";

	/**
	 * Path of the table without the usual {@code /root} prefix. Exclusive from {@link #pathInSchema()}.
	 */
	String path() default "";

	/**
	 * Full path of the table in the dataset. Exclusive from {@link #path()}.
	 */
	String pathInSchema() default "";
}
