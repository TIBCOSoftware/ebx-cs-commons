/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.onwbp.adaptation.Adaptation;

/**
 * Maps a getter method to an EBX association. Required when the association path or records value conversion cannot be inferred from the getter method name and return type.
 * 
 * @author Gilles Mayer
 */
@Documented
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Association {
	/**
	 * Alias for {@link #path()}
	 */
	String value() default "";

	/**
	 * Path of the association in adaptation. Required when the association path cannot be inferred from the getter name.
	 */
	String path() default "";

	/**
	 * Path of a field in the association records used as the {@link Map.Entry#getKey() key } of a map entry. Required when the return type of the getter return value is a {@link Map} and
	 * {@link #keyExtractor()} is not specified.
	 */
	String keyPath() default "";

	/**
	 * Path of the group of the association in adaptation. Required when the last step of the association path can be inferred from the getter name but not the first (group) part of the path.
	 */
	String group() default "";

	/**
	 * Paths of the fields of the association records used to sort these records. Required when the getter return type is a {@link List} and the desired order of elements in the list does not match
	 * the default association record order.
	 * 
	 * @see #sortOrders()
	 */
	String[] sortCriteria() default {};

	/**
	 * Order of the {@link #sortCriteria() criteria}. Required when {@link #sortCriteria()} is specified and the default order must be changed.
	 */
	Order[] sortOrders() default {};

	/**
	 * Class of a key extractor {@link Function} used to compute the {@link Map.Entry#getKey() key } of a map entry from the {@link Map.Entry#getValue() value }. Required when the return type of the
	 * getter return value is a {@link Map} and {@link #keyPath()} is not specified.
	 * <p>
	 * The function should be thread safe and its returned value should be immutable.
	 * </p>
	 */
	Class<? extends Function<Adaptation, ?>> keyExtractor() default DEFAULT.class;

	static final class DEFAULT implements Function<Adaptation, Object> {
		@Override
		public Object apply(final Adaptation t) {
			throw new UnsupportedOperationException();
		}
	}

}
