package com.tibco.ebx.cs.commons.beans.adapter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.function.Function;

import com.onwbp.adaptation.Adaptation;

/**
 * Maps a getter getter method to an EBX field. Required when the field path or value transformation cannot be inferred from the getter method name and return type.
 * 
 * @author Gilles Mayer
 */
@Documented
@Target(value = { ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
@Inherited
public @interface Field {
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

	/**
	 * Class of a transformer {@link Function} used to convert the value in the {@link Adaptation} to the getter return value. Required when the getter return type does not match the value in the
	 * Adaptation and when a default transformer for these types does not exist.
	 * <p>
	 * The function should be thread safe, its returned value should be immutable and it should support {@code null} parameter value (since a null input value can be mapped to a non-null output
	 * value).
	 * </p>
	 */
	Class<? extends Function<?, ?>> transformer() default DEFAULT.class;

	/**
	 * Class of a key extractor {@link Function} used to compute the {@link Map.Entry#getKey() key } of a map entry from the {@link Map.Entry#getValue() value }. Required when the return type of the
	 * getter return value is a {@link Map}.
	 * <p>
	 * The function should be thread safe and its returned value should be immutable.
	 * </p>
	 */
	Class<? extends Function<?, ?>> keyExtractor() default DEFAULT.class;

	static final class DEFAULT implements Function<Object, Object> {
		@Override
		public Object apply(final Object t) {
			throw new UnsupportedOperationException();
		}
	}

}
