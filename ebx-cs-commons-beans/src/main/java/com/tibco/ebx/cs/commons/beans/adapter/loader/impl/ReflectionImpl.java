package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.tibco.ebx.cs.commons.beans.adapter.annotation.Field;
import com.tibco.ebx.cs.commons.beans.adapter.loader.BeanLoader;
import com.tibco.ebx.cs.commons.beans.adapter.relation.Relation;
import com.tibco.ebx.cs.commons.beans.adapter.relation.ToMany;
import com.tibco.ebx.cs.commons.beans.adapter.relation.ToOne;

/**
 *
 * @author Gilles Mayer
 */
final class ReflectionImpl {

	private static final Method OBJECT_GET_CLASS;

	static {
		try {
			OBJECT_GET_CLASS = Object.class.getMethod("getClass");
		} catch (NoSuchMethodException e) {
			throw new AssertionError(e);
		}
	}

	/**
	 * Implementation of the getter methods in interfaces managed by the
	 * {@link BeanLoader}
	 * <p>
	 * Method name, return type (including type parameters) and annotations are used
	 * to infer the expected method behavior which includes:
	 * <ul>
	 * <li>getting the value from the adaptation (record),
	 * <li>optionally transforming the value (or values),
	 * <li>optionally mapping to target enum type,
	 * <li>optionally mapping to target bean type,
	 * <li>optionally transforming collections to the right collection type (or
	 * map).
	 * </ul>
	 *
	 * @param adaptation  the adaptation backing the bean
	 * @param method      the invoked method
	 * @param beanFactory function used to retrieve other bean instances
	 * @return the result of the method invocation
	 *
	 * @throws InstantiationException if thrown by a reflection operation
	 * @throws IllegalAccessException if thrown by a reflection operation
	 * @throws ModelException         if no operation could be inferred for the
	 *                                method
	 */
	static <T> Object doInvocation(final Adaptation adaptation, final Method method,
			final BiFunction<Class<T>, Adaptation, T> beanFactory)
			throws InstantiationException, IllegalAccessException {
		if (Utils.isGetter(method)) {
			return reflectionGet(adaptation, method, beanFactory);
		}
		// It's the responsibility of the caller to check that 'method' has no
		// implementation
		throw new ModelException(method, "Method is not a getter and has no implementation");
	}

	@SuppressWarnings("rawtypes")
	private static <T> Object reflectionGet(final Adaptation adaptation, final Method method,
			final BiFunction<Class<T>, Adaptation, T> beanFactory)
			throws InstantiationException, IllegalAccessException {
		Class targetType = Utils.getTargetType(method);
		if (Utils.isField(method)) {
			return reflectionGetField(adaptation, method, targetType);
		} else {
			Relation relation = Utils.getRelation(method, adaptation.getSchemaNode());
			if (relation != null) {
				return reflectionGetRelation(adaptation, method, beanFactory, targetType, relation);
			}
		}
		throw new ModelException(method, "Method is a getter but does not match any field or relation");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object reflectionGetField(final Adaptation adaptation, final Method method, final Class targetType)
			throws InstantiationException, IllegalAccessException {
		// ================================
		// || Field 'get' implementation ||
		// ================================

		// Determine the key extractor when returning a map
		Class<? extends Function> transformerClass = null;
		Function<Object, ?> keyExtractor = null;
		Field field = method.getAnnotation(Field.class);
		if (field != null) {
			if (field.transformer() != Field.DEFAULT.class) {
				transformerClass = field.transformer();
			}
			if (field.keyExtractor() != Field.DEFAULT.class) {
				if (!Map.class.isAssignableFrom(method.getReturnType())) {
					throw new ModelException("keyExtractor is specified but " + method + " return type is not a Map");
				}
				;

				try {
					keyExtractor = (Function<Object, ?>) field.keyExtractor().getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new ModelException("keyExtractor is specified but cannot be instantiated");
				}

			}
		}

		// Get the value from the adaptation
		Object value = adaptation.get(Utils.getFieldPath(method));

		if (value instanceof List) {
			// -----------------------------------
			// Value is a list (multi-occurrenced)
			// -----------------------------------
			if (((List<?>) value).isEmpty()) {
				value = Utils.getEmptyInstance(method);
			} else {
				// Transform the values
				Stream stream = ((List<Object>) value).stream();
				if (transformerClass != null) {
					// Explicit transformer
					stream = stream.map(transformerClass.newInstance());
				} else {
					Function transformer = Utils.getImplicitTransformer(targetType);
					if (transformer != null) {
						// Implicit transformer
						stream = stream.map(transformer);
					}
				}

				if (targetType.isEnum()) {
					// Map to enum values
					stream = stream.map(e -> e == null || targetType.isAssignableFrom(e.getClass()) ? e
							: Enum.valueOf(targetType, e.toString()));
				}

				if (keyExtractor != null && method.getGenericReturnType() instanceof ParameterizedType) {
					// Build and return the map
					final Class<?> valueType = Utils.getMapValueType(method);
					if (Utils.isCollection(valueType)) {
						return stream
								.collect(
										Collectors.collectingAndThen(
												Collectors.groupingBy(keyExtractor,
														Collectors.mapping(Function.identity(),
																Utils.getCollector(valueType))),
												Collections::unmodifiableMap));
					}
				}

				// Build and return the collection
				value = stream.collect(Utils.getCollector(method.getReturnType()));
			}
		} else {
			// ------------------------
			// Value is a single value
			// ------------------------
			if (transformerClass != null) {
				// Transform the values with explicit transformer
				value = transformerClass.newInstance().apply(value);
			} else {
				// Transform the values with implicit transformer
				Function transformer = Utils.getImplicitTransformer(targetType);
				if (transformer != null) {
					value = transformer.apply(value);
				}
			}
			if (value != null && !targetType.isAssignableFrom(value.getClass()) && targetType.isEnum()) {
				// Map to enum values
				value = Enum.valueOf(targetType, value.toString());
			}
		}
		// Return the value
		return value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static <T> Object reflectionGetRelation(final Adaptation adaptation, final Method method,
			final BiFunction<Class<T>, Adaptation, T> beanFactory, final Class targetType, final Relation relation)
			throws InstantiationException, IllegalAccessException {
		// ===================================
		// || Relation 'get' implementation ||
		// ===================================

		if (relation instanceof ToMany) {
			Path keyPath = Utils.getKeyPath(method);
			Class<? extends Function<Adaptation, ?>> keyExtractorClass = Utils.getKeyExtractorClass(method);
			if (keyPath != null || keyExtractorClass != null) {
				final Stream<Adaptation> adaptationStream = ((ToMany) relation).get(adaptation).stream();
				Function<Adaptation, ?> keyMapper;
				if (keyPath != null) {
					if (keyExtractorClass != null) {
						throw new ModelException(method, "Define only one of 'keyPath' and 'keyExtractor'");
					}
					keyMapper = record -> record.get(keyPath);
				} else {
					keyMapper = keyExtractorClass.newInstance();
				}
				if (method.getGenericReturnType() instanceof ParameterizedType) {
					Class<?> keyType = Utils.getMapKeyType(method);
					Function keyTransformer = Utils.getImplicitTransformer(keyType);
					if (keyTransformer != null) {
						keyMapper = keyMapper.andThen(keyTransformer::apply);
					}

					final Class<?> valueType = Utils.getMapValueType(method);
					if (Utils.isCollection(valueType)) {
						return adaptationStream
								.collect(
										Collectors.collectingAndThen(
												Collectors.groupingBy(keyMapper,
														Collectors.mapping(
																record -> beanFactory.apply(targetType, record),
																Utils.getCollector(valueType))),
												Collections::unmodifiableMap));
					}
				}
				return adaptationStream.collect(Collectors.collectingAndThen(
						Collectors.toMap(keyMapper, record -> beanFactory.apply(targetType, record)),
						Collections::unmodifiableMap));
			}
			return ((ToMany) relation).get(adaptation).stream().map(record -> beanFactory.apply(targetType, record))
					.collect(Utils.getCollector(method.getReturnType()));
		} else if (relation instanceof ToOne) {
			return beanFactory.apply(targetType, ((ToOne) relation).get(adaptation));
		} else {
			throw new ModelException(method, "Unsupported relation for the method");
		}
	}

	static Iterable<Method> listAbstractGetters(final Class<?> beanType) {
		ArrayList<Method> getters = new ArrayList<>();
		for (Method method : beanType.getMethods()) {
			if (Modifier.isAbstract(method.getModifiers()) && !method.equals(OBJECT_GET_CLASS)
					&& Utils.isGetter(method)) {
				getters.add(method);
			}
		}
		return getters;
	}

	private ReflectionImpl() {
		throw new AssertionError();
	}
}
