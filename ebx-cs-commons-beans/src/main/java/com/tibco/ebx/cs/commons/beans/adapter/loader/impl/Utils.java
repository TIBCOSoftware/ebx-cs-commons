package com.tibco.ebx.cs.commons.beans.adapter.loader.impl;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationReference;
import com.onwbp.adaptation.RequestSortCriteria;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaLocation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.AssociationLink;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.service.AccessPermission;
import com.tibco.ebx.cs.commons.beans.adapter.annotation.Association;
import com.tibco.ebx.cs.commons.beans.adapter.annotation.Field;
import com.tibco.ebx.cs.commons.beans.adapter.annotation.Table;
import com.tibco.ebx.cs.commons.beans.adapter.annotation.TableRef;
import com.tibco.ebx.cs.commons.beans.adapter.relation.AssociationRelation;
import com.tibco.ebx.cs.commons.beans.adapter.relation.MultiTableRefRelation;
import com.tibco.ebx.cs.commons.beans.adapter.relation.Relation;
import com.tibco.ebx.cs.commons.beans.adapter.relation.TableRefRelation;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.AccessPermissionTransformer;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.AdaptationNameTransformer;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.AdaptationReferenceTransformer;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.HomeKeyTransformer;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.PathTransformer;
import com.tibco.ebx.cs.commons.beans.adapter.transformer.SchemaLocationTransformer;

/**
 * 
 * @author Gilles Mayer
 */
final class Utils {
	private static final Path _Root = Path.parse("/root");

	@SuppressWarnings("rawtypes")
	private static final Map<Class<?>, Function> IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE = new HashMap<>();
	static {
		// Keep this in sync with the 'Transformers' subsection in the
		// 'com.tibco.ebx.cs.commons.model' package Javadoc
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(AdaptationName.class, new AdaptationNameTransformer());
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(AdaptationReference.class, new AdaptationReferenceTransformer());
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(SchemaLocation.class, new SchemaLocationTransformer());
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(Path.class, new PathTransformer());
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(HomeKey.class, new HomeKeyTransformer());
		IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.put(AccessPermission.class, new AccessPermissionTransformer());
	}

	private Utils() {
		throw new AssertionError();
	}

	static Path getKeyPath(final Method method) {
		Association association = method.getAnnotation(Association.class);
		if (association != null && !association.keyPath().isEmpty()) {
			return Path.SELF.add(Path.parse(association.keyPath()));
		}
		return null;
	}

	static Class<? extends Function<Adaptation, ?>> getKeyExtractorClass(final Method method) {
		Association association = method.getAnnotation(Association.class);
		if (association != null && association.keyExtractor() != Association.DEFAULT.class) {
			return association.keyExtractor();
		}
		return null;
	}

	static Relation getRelation(final Method method, final SchemaNode recordNode) {
		// Annotated with @Association
		Association association = method.getAnnotation(Association.class);
		if (association != null) {
			SchemaNode assoNode = recordNode.getNode(getPropertyPath(method, association.path(), association.value(), association.group()));
			AssociationLink associationLink = assoNode.getAssociationLink();
			int sortCriteriaLength = association.sortCriteria().length;
			if (sortCriteriaLength > 0) {
				RequestSortCriteria sort = new RequestSortCriteria();
				int sortOrderLength = association.sortOrders().length;
				for (int i = 0; i < sortCriteriaLength; i++) {
					String strPath = association.sortCriteria()[i];
					Path sortCrit = Path.parse(strPath);
					if (strPath != null && !strPath.startsWith("\\.")) {
						sortCrit = Path.SELF.add(sortCrit);
					}
					if (i < sortOrderLength) {
						sort.add(sortCrit, association.sortOrders()[i].isAscendant);
					} else {
						sort.add(sortCrit);
					}
				}
				return new AssociationRelation(associationLink, sort);
			}
			return new AssociationRelation(associationLink);
		}

		// Annotated with @TableRef
		TableRef tableRef = method.getAnnotation(TableRef.class);
		if (tableRef != null) {
			SchemaNode fieldNode = recordNode.getNode(getPropertyPath(method, tableRef.path(), tableRef.value(), tableRef.group()));
			SchemaFacetTableRef ref = fieldNode.getFacetOnTableReference();
			if (fieldNode.getMaxOccurs() > 1) {
				return new MultiTableRefRelation(ref);
			}
			return new TableRefRelation(ref);
		}

		Path inferredPath = Path.SELF.add(getPropertyName(method));
		SchemaNode fieldNode = recordNode.getNode(inferredPath);

		if (fieldNode == null) {
			throw new ModelException(method, "There is no node for the inferred path " + inferredPath.format() + " in " + recordNode);
		}

		// Inferred node is an association
		AssociationLink associationLink = fieldNode.getAssociationLink();
		if (associationLink != null) {
			return new AssociationRelation(associationLink);
		}

		// Inferred node is a table reference
		final SchemaFacetTableRef ref = fieldNode.getFacetOnTableReference();
		if (ref != null) {
			if (fieldNode.getMaxOccurs() > 1) {
				return new MultiTableRefRelation(ref);
			}
			return new TableRefRelation(ref);
		}

		// Not a relation
		return null;
	}

	static Path getPropertyPath(final Method method, final String path, final String value, final String group) {
		String result;
		if (!value.isEmpty()) {
			if (!path.isEmpty()) {
				throw new UnsupportedOperationException("Define at most one of 'value' and 'path'");
			}
			result = value;
		} else if (!path.isEmpty()) {
			result = path;
		} else {
			result = getPropertyName(method);
		}
		if (!group.isEmpty()) {
			return Path.SELF.add(group).add(result);
		}
		return Path.SELF.add(result);
	}

	static Path getTargetTablePath(final Method method, final String relTable) {
		if (!relTable.isEmpty()) {
			return Path.parse(relTable);
		}
		return getTablePath(getTargetType(method));
	}

	static Path getTablePath(final Class<?> type) {
		Table table = type.getAnnotation(Table.class);
		if (table != null) {
			if (!table.value().isEmpty()) {
				if (!table.path().isEmpty() || !table.pathInSchema().isEmpty()) {
					throw new UnsupportedOperationException("Define at most one of 'value', 'path' and 'pathInSchema'");
				}
				return _Root.add(table.value());
			} else if (!table.path().isEmpty()) {
				if (!table.pathInSchema().isEmpty()) {
					throw new UnsupportedOperationException("Define at most one of 'value', 'path' and 'pathInSchema'");
				}
				return _Root.add(table.path());
			} else if (!table.pathInSchema().isEmpty()) {
				return Path.parse(table.pathInSchema());
			}
		}
		return _Root.add(type.getSimpleName());
	}

	static Adaptation getDataSet(AdaptationHome home, final String relHome, final String relDataset) {
		if (relDataset.isEmpty()) {
			return null;
		}
		if (!relHome.isEmpty()) {
			home = home.getRepository().lookupHome(HomeKey.parse(relHome));
		}
		return home.findAdaptationOrNull(AdaptationName.forName(relDataset));
	}

	static boolean isCollection(final Type type) {
		return List.class == type || Set.class == type || SortedSet.class == type || NavigableSet.class == type;
	}

	static Class<?> getTargetType(final Method method) {
		Class<?> returnType = method.getReturnType();
		if (method.getGenericReturnType() instanceof ParameterizedType) {
			if (isCollection(returnType)) {
				return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
			} else if (Map.class == returnType) {
				Type valueType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[1];
				if (valueType instanceof ParameterizedType && isCollection(((ParameterizedType) valueType).getRawType())) {
					return (Class<?>) ((ParameterizedType) valueType).getActualTypeArguments()[0];
				}
				return (Class<?>) valueType;
			}
		}
		return returnType;
	}

	static Class<?> getMapKeyType(final Method method) {
		return (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
	}

	static Class<?> getMapValueType(final Method method) {
		Type type = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[1];
		if (type instanceof ParameterizedType) {
			type = ((ParameterizedType) type).getRawType();
		}
		return (Class<?>) type;
	}

	static String getPropertyName(final Method getter) {
		String substr = getter.getName().startsWith("is") ? getter.getName().substring(2) : getter.getName().substring(3);
		return substr.substring(0, 1).toLowerCase(Locale.ENGLISH) + substr.substring(1);
	}

	@SuppressWarnings("unchecked")
	static <T, A, R> Collector<T, A, R> getCollector(final Class<?> type) throws InstantiationException, IllegalAccessException {
		if (type == List.class) {
			return (Collector<T, A, R>) Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList);
		}
		if (type == Set.class) {
			return (Collector<T, A, R>) Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet);
		}
		if (type == SortedSet.class || type == NavigableSet.class) {
			return (Collector<T, A, R>) Collectors.collectingAndThen(Collectors.toCollection(TreeSet::new), Collections::unmodifiableNavigableSet);
		}
		throw new UnsupportedOperationException(type.toGenericString());
	}

	static Object getEmptyInstance(final Method method) {
		if (method.getReturnType() == List.class) {
			return Collections.emptyList();
		}
		if (method.getReturnType() == Set.class) {
			return Collections.emptySet();
		}
		if (method.getReturnType() == Map.class) {
			return Collections.emptyMap();
		}
		throw new UnsupportedOperationException(method.toGenericString());
	}

	static Path getFieldPath(final Method method) {
		Field field = method.getAnnotation(Field.class);
		if (field != null) {
			return getPropertyPath(method, field.path(), field.value(), field.group());
		}
		return Path.SELF.add(getPropertyName(method));
	}

	static boolean isField(final Method method) {
		Class<?> targetType;
		return method.getAnnotation(Field.class) != null || isFieldType(targetType = getTargetType(method)) || isImplicitTransformerType(targetType) || !targetType.isInterface();
	}

	static boolean isImplicitTransformerType(final Class<?> targetType) {
		return IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.containsKey(targetType);
	}

	@SuppressWarnings("unchecked")
	static <T, R> Function<T, R> getImplicitTransformer(final Class<R> targetType) {
		return IMPLICIT_TRANSFORMERS_BY_TARGET_TYPE.get(targetType);
	}

	static boolean isFieldType(final Class<?> t) {
		return t.isPrimitive() || t == String.class || t == Integer.class || t == Boolean.class || t == BigDecimal.class || t == Date.class || t == URI.class || t == Locale.class || t.isEnum()
				|| isImplicitTransformerType(t);
	}

	static boolean isGetter(final Method method) {
		if (method.getParameterCount() != 0) {
			return false;
		}
		if (method.getName().startsWith("get") && method.getName().length() > 3 && method.getReturnType() != Void.TYPE) {
			return true;
		}
		if (method.getName().startsWith("is") && method.getName().length() > 2 && (method.getReturnType() == Boolean.TYPE || method.getReturnType() == Boolean.class)) {
			return true;
		}
		return false;
	}
}
