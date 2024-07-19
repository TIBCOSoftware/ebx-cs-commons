/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.template;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.RequestResult;
import com.onwbp.adaptation.RequestSortCriteria;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.ConstraintViolationException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.SessionPermissions;
import com.orchestranetworks.service.ValueContextForUpdate;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansFunctionalException;
import com.tibco.ebx.cs.commons.beans.generator.exception.BeansTechnicalException;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsFunctionalReason;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * Data Access Object corresponding to a given EBX Table.
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 *
 * @param <T> Table Bean
 */
public abstract class TableDAO<T extends TableBean> {

	/**
	 * Build an EBX representation of the primary key from a Java Bean
	 *
	 * @param pBean A Java Bean representing a table occurrence.
	 *
	 * @return The string representation of a primary key or null if the PK cannot be constituted.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public String getRecordPrimaryKey(final T pBean) throws EBXCommonsException {
		if (pBean == null) {
			throw new IllegalArgumentException("The Java Bean cannot be null");
		}
		StringBuilder str = new StringBuilder();
		for (String primaryKey : this.getPrimaryKeysGetters()) {
			try {
				Object pk = pBean.getClass().getMethod(primaryKey).invoke(pBean);
				if (pk == null) {
					// TODO Study backward compatibility to get reactivate this exception.
					// throw new BeansFunctionalException(EBXCommonsFunctionalReason.BEANS_PK_MEMBER_NULL);
					return null;
				}
				if (TableBean.class.isInstance(pk)) {
					str.append(((TableBean) pk).getDAO().getRecordPrimaryKey(pk) + "|");
				} else {
					str.append(pk + "|");
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
				throw new BeansTechnicalException(ex);
			}
		}
		str.deleteCharAt(str.length() - 1);
		return str.toString();
	}

	/**
	 * Build an EBX representation of the primary key from a Java Bean
	 *
	 * @param pBean A Java Bean representing a table occurrence.
	 *
	 * @return The string representation of a primary key or null if the PK cannot be constituted.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public String getRecordPrimaryKey(final Object pBean) throws EBXCommonsException {
		return this.getRecordPrimaryKey((T) pBean);
	}

	/**
	 * Build a list of EBX primary keys from a list of Java Beans.
	 *
	 * @param pBeanList A list of Java Beans representing table occurrences.
	 *
	 * @return A list of string representation of a primary keys.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public List<String> getRecordPrimaryKeys(final List<T> pBeanList) throws EBXCommonsException {
		List<String> primaryKeys = new ArrayList<>();
		if (pBeanList != null) {
			for (T bean : pBeanList) {
				primaryKeys.add(this.getRecordPrimaryKey(bean));
			}
		}
		return primaryKeys;
	}

	protected abstract String[] getPrimaryKeysGetters();

	protected abstract List<PrimaryKeySetter> getPrimaryKeysSetters() throws BeansTechnicalException;

	protected abstract T getInstanceOfBean(Adaptation pDataset);

	protected abstract T getInstanceOfBean();

	protected abstract DatasetDAO<?> getDatasetDAO(final Adaptation pDataset);

	protected abstract AdaptationTable getAdaptationTable(final Adaptation pDataset);

	/**
	 * Create a table occurrence from a Java Bean
	 *
	 * @param pContext A procedure context.
	 * @param pDataset The dataset in which to create the table occurrence
	 * @param pBean    The Java Bean
	 *
	 * @return A Java Bean representing the created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T create(final ProcedureContext pContext, final Adaptation pDataset, final T pBean) throws EBXCommonsException {
		return this.create(pContext, this.getAdaptationTable(pDataset), pBean);
	}

	/**
	 * Create a table occurrence from a Java Bean
	 *
	 * @param pContext     A procedure context.
	 * @param pDataset     The dataset in which to create the table occurrence
	 * @param pBean        The Java Bean
	 * @param pPermissions Permissions to be applied to the creation and the read of the created record returned as java bean.
	 *
	 * @return A Java Bean representing the created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T create(final ProcedureContext pContext, final Adaptation pDataset, final T pBean, final Optional<SessionPermissions> pPermissions) throws EBXCommonsException {
		return this.create(pContext, this.getAdaptationTable(pDataset), pBean, pPermissions);
	}

	/**
	 * Create a table occurrence from a Java Bean
	 *
	 * @param pContext A procedure context.
	 * @param pTable   The table in which to create the table occurrence
	 * @param pBean    The Java Bean
	 *
	 * @return A Java Bean representing the created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T create(final ProcedureContext pContext, final AdaptationTable pTable, final T pBean) throws EBXCommonsException {
		return this.create(pContext, pTable, pBean, Optional.empty());
	}

	/**
	 * Create a table occurrence from a Java Bean
	 *
	 * @param pContext     A procedure context.
	 * @param pTable       The table in which to create the table occurrence
	 * @param pBean        The Java Bean
	 * @param pPermissions Permissions to be applied to the creation and the read of the created record returned as java bean.
	 *
	 * @return A Java Bean representing the created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T create(final ProcedureContext pContext, final AdaptationTable pTable, final T pBean, final Optional<SessionPermissions> pPermissions) throws EBXCommonsException {
		ValueContextForUpdate vcfu = pContext.getContextForNewOccurrence(pTable);
		try {
			this.setValuesForUpdate(vcfu, pBean);
			Adaptation record = pContext.doCreateOccurrence(vcfu, pTable);
			T bean = (T) pBean.getDAO().read(record, pPermissions);
			return bean;
		} catch (OperationException ex) {
			throw new BeansTechnicalException(ex);
		}
	}

	/**
	 * Build a Java Bean from an EBX table occurrence
	 *
	 * @param pRecord An EBX table occurrence
	 *
	 * @return A Java Bean representing the record.
	 */
	public T read(final Adaptation pRecord) {
		return this.read(pRecord, Optional.empty());
	}

	/**
	 * Build a Java Bean from an EBX table occurrence
	 *
	 * @param pRecord      An EBX table occurrence
	 * @param pPermissions Permissions to be applied to the read of the record returned as java bean.
	 *
	 * @return A Java Bean representing the record.
	 */
	public T read(final Adaptation pRecord, final Optional<SessionPermissions> pPermissions) {
		if (pRecord == null || pPermissions.isPresent() && pPermissions.get().getAdaptationAccessPermission(pRecord).isHidden()) {
			return null;
		}
		T t = this.getInstanceOfBean(null);
		this.getValuesFromAdaptation(t, pRecord, pPermissions);
		t.setEbxRecord(pRecord);
		return t;
	}

	/**
	 * Build a Java Bean from an EBX primary key
	 *
	 * @param pPrimaryKey the primary key of an EBX table occurrence.
	 * @param pDataset    The dataset in which to search for a record based on the primary key.
	 *
	 * @return A Java Bean representing the record.
	 */
	public Optional<T> read(final String pPrimaryKey, final Adaptation pDataset) {
		return this.read(pPrimaryKey, pDataset, Optional.empty());
	}

	/**
	 * Build a Java Bean from an EBX primary key
	 *
	 * @param pPrimaryKey  the primary key of an EBX table occurrence.
	 * @param pDataset     The dataset in which to search for a record based on the primary key.
	 * @param pPermissions Permissions to be applied to the read of the record returned as java bean.
	 *
	 * @return A Java Bean representing the record.
	 */
	public Optional<T> read(final String pPrimaryKey, final Adaptation pDataset, final Optional<SessionPermissions> pPermissions) {
		if (pPrimaryKey == null) {
			return Optional.empty();
		}
		PrimaryKey pk = PrimaryKey.parseString(pPrimaryKey);
		Adaptation record = this.getAdaptationTable(pDataset).lookupAdaptationByPrimaryKey(pk);
		if (record == null) {
			return Optional.empty();
		}
		return Optional.of(this.read(record, pPermissions));
	}

	/**
	 * Build a list of Java Bean from a list of EBX table occurrences
	 *
	 * @param pRecords A list of EBX table occurrences.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final List<Adaptation> pRecords) {
		return this.readAll(pRecords, Optional.empty());
	}

	/**
	 * Build a list of Java Bean from a list of EBX table occurrences
	 *
	 * @param pRecords     A list of EBX table occurrences.
	 * @param pPermissions Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final List<Adaptation> pRecords, final Optional<SessionPermissions> pPermissions) {
		List<T> list = new ArrayList<>();
		for (final Adaptation record : pRecords) {
			T bean = this.read(record, pPermissions);
			if (bean != null) {
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * Build a list of Java Bean from a list of primary keys
	 *
	 * @param pPrimaryKeys A list of primary keys of EBX table occurrences.
	 * @param pDataset     The dataset in which to search for records based on the primary keys.
	 *
	 * @return A list of Java Bean representing records.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> readAll(final List<String> pPrimaryKeys, final Adaptation pDataset) throws BeansTechnicalException {
		return this.readAll(pPrimaryKeys, pDataset, Optional.empty());
	}

	/**
	 * Build a list of Java Bean from a list of primary keys
	 *
	 * @param pPrimaryKeys A list of primary keys of EBX table occurrences.
	 * @param pDataset     The dataset in which to search for records based on the primary keys.
	 * @param pPermissions Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Bean representing records.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public List<T> readAll(final List<String> pPrimaryKeys, final Adaptation pDataset, final Optional<SessionPermissions> pPermissions) throws BeansTechnicalException {
		List<Adaptation> records = new ArrayList<>();
		List<T> beans = new ArrayList<>();
		if (pPrimaryKeys != null) {
			for (String primaryKey : pPrimaryKeys) {
				PrimaryKey pk = PrimaryKey.parseString(primaryKey);
				Adaptation record = this.getAdaptationTable(pDataset).lookupAdaptationByPrimaryKey(pk);
				if (record != null) {
					records.add(record);
				} else {
					T bean = this.getInstanceOfBeanFromPK(pDataset, pk);
					beans.add(bean);
				}
			}
		}
		beans.addAll(this.readAll(records, pPermissions));
		return beans;
	}

	/**
	 * Build a Java Bean only based on a primary key in valuing the attributes which compose it.
	 *
	 * @param pDataset The dataset in which to find an occurrence of the corresponding AdaptationTable.
	 * @param pPK      The primary key of an EBX table occurrence.
	 *
	 * @return A Java Bean only valued with the provided primary key, not bound to an EBX record nor value context.
	 * @throws BeansTechnicalException BeansTechnicalException
	 */
	public T getInstanceOfBeanFromPK(final Adaptation pDataset, final PrimaryKey pPK) throws BeansTechnicalException {
		T bean = this.getInstanceOfBean();
		Object[] tablePrimaryKeyValues = this.getAdaptationTable(pDataset).getTableNode().getTablePrimaryKeyValues(pPK);
		for (int i = 0; i < this.getPrimaryKeysSetters().size(); i++) {
			try {
				PrimaryKeySetter setter = this.getPrimaryKeysSetters().get(i);
				if (setter.getBean().isPresent()) {
					SchemaFacetTableRef facetOnTableReference = this.getAdaptationTable(pDataset).getTableNode().getTablePrimaryKeyNodes()[i].getFacetOnTableReference();
					Class<? extends TableBean> linkedBean = setter.getBean().get();
					Method getDaoMethod = linkedBean.getDeclaredMethod("getDAO");
					Class<?> returnType = getDaoMethod.getReturnType();
					Method getInstanceMethod = returnType.getDeclaredMethod("getInstance");
					TableDAO<?> dao = (TableDAO<?>) getInstanceMethod.invoke(null);
					setter.getSetter().invoke(bean,
							dao.getInstanceOfBeanFromPK(SchemaUtils.getRelatedDataset(facetOnTableReference, pDataset), PrimaryKey.parseString((String) tablePrimaryKeyValues[i])));
				} else {
					setter.getSetter().invoke(bean, tablePrimaryKeyValues[i]);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException | EBXResourceNotFoundException | NoSuchMethodException ex) {
				throw new BeansTechnicalException(ex);
			}
		}
		return bean;
	}

	/**
	 * Build a Java Bean from an EBX table occurrence
	 *
	 * @param pContext An EBX value context
	 *
	 * @return A Java Bean representing the record.
	 */
	public T read(final ValueContext pContext) {
		return this.read(pContext, Optional.empty());
	}

	/**
	 * Build a Java Bean from an EBX table occurrence
	 *
	 * @param pContext     An EBX value context
	 * @param pPermissions Permissions to be applied to the read of the record returned as java bean.
	 *
	 * @return A Java Bean representing the record.
	 */
	public T read(final ValueContext pContext, final Optional<SessionPermissions> pPermissions) {
		Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext);
		if (record.isPresent() && pPermissions.isPresent() && pPermissions.get().getAdaptationAccessPermission(record.get()).isHidden()) {
			return null;
		}
		T t = this.getInstanceOfBean(null);
		this.getValuesFromValueContext(t, pContext, pPermissions);
		t.setEbxContext(pContext);
		return t;
	}

	/**
	 * Build a Java Bean from an EBX referenced table occurrence
	 *
	 * @param pBean A Java Bean representing a table occurrence
	 * @param pPath The path of the reference in the table occurrence.
	 *
	 * @return A Java Bean representing the referenced record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public Optional<T> read(final TableBean pBean, final Path pPath) throws EBXCommonsException {
		return this.read(pBean, pPath, Optional.empty());
	}

	/**
	 * Build a Java Bean from an EBX referenced table occurrence
	 *
	 * @param pBean        A Java Bean representing a table occurrence
	 * @param pPath        The path of the reference in the table occurrence.
	 * @param pPermissions Permissions to be applied to the read of the record returned as java bean.
	 *
	 * @return A Java Bean representing the referenced record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public Optional<T> read(final TableBean pBean, final Path pPath, final Optional<SessionPermissions> pPermissions) throws EBXCommonsException {
		List<Adaptation> linkedRecords = new ArrayList<>();
		if (pBean.getEbxRecord() != null) {
			linkedRecords = AdaptationUtils.getLinkedRecords(pBean.getEbxRecord(), pPath, Optional.empty());
			if (linkedRecords.isEmpty()) {
				String value = pBean.getEbxRecord().getString(pPath);
				if (StringUtils.isBlank(value)) {
					return Optional.empty();
				} else {
					SchemaNode fkNode = pBean.getEbxRecord().getSchemaNode().getNode(pPath);
					Adaptation dataset = pBean.getEbxRecord().getContainer();
					if (fkNode.getFacetOnTableReference() != null && fkNode.getFacetOnTableReference().getContainerReference() != null) {
						dataset = SchemaUtils.getRelatedDataset(fkNode.getFacetOnTableReference(), dataset);
					}
					return Optional.of(this.getInstanceOfBeanFromPK(dataset, PrimaryKey.parseString(value)));
				}
			}
		} else if (pBean.getEbxContext() != null) {
			Path pathToRecordRoot = SchemaUtils.getPathToRecordRoot(pBean.getEbxContext().getNode());
			linkedRecords = AdaptationUtils.getLinkedRecords(pBean.getEbxContext(), pathToRecordRoot.add(pPath), Optional.empty());
			if (linkedRecords.isEmpty()) {
				String value = (String) pBean.getEbxContext().getValue(pathToRecordRoot.add(pPath));
				if (StringUtils.isBlank(value)) {
					return Optional.empty();
				} else {
					SchemaNode fkNode = pBean.getEbxContext().getNode(pathToRecordRoot.add(pPath));
					Adaptation dataset = pBean.getEbxContext().getAdaptationInstance();
					if (fkNode.getFacetOnTableReference() != null && fkNode.getFacetOnTableReference().getContainerReference() != null) {
						dataset = SchemaUtils.getRelatedDataset(fkNode.getFacetOnTableReference(), dataset);
					}
					return Optional.of(this.getInstanceOfBeanFromPK(dataset, PrimaryKey.parseString(value)));
				}
			}
		} else {
			throw new BeansFunctionalException(EBXCommonsFunctionalReason.BEANS_NOT_BOUND, pPath.format());
		}
		if (linkedRecords.size() > 1) {
			throw new BeansFunctionalException(EBXCommonsFunctionalReason.BEANS_MANY_LINKED_RECORDS, pPath.format());
		} else {
			return Optional.of(this.read(linkedRecords.get(0), pPermissions));
		}
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset   A dataset in which to apply the filter.
	 * @param pPredicate An XPath predicate to use as filter.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate) {
		return this.readAll(pDataset, pPredicate, Optional.empty());
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset     A dataset in which to apply the filter.
	 * @param pPredicate   An XPath predicate to use as filter.
	 * @param pPermissions Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate, final Optional<SessionPermissions> pPermissions) {
		List<T> list = new ArrayList<>();
		for (Adaptation record : this.getAdaptationTable(pDataset).selectOccurrences(pPredicate)) {
			T bean = this.read(record, pPermissions);
			if (bean != null) {
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset      A dataset in which to apply the filter.
	 * @param pPredicate    An XPath predicate to use as filter.
	 * @param pSortCriteria Object defining the ordering of beans within the returned list.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate, final RequestSortCriteria pSortCriteria) {
		return this.readAll(pDataset, pPredicate, pSortCriteria, Optional.empty());
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset      A dataset in which to apply the filter.
	 * @param pPredicate    An XPath predicate to use as filter.
	 * @param pSortCriteria Object defining the ordering of beans within the returned list.
	 * @param pPermissions  Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate, final RequestSortCriteria pSortCriteria, final Optional<SessionPermissions> pPermissions) {
		List<T> list = new ArrayList<>();
		for (Adaptation record : this.getAdaptationTable(pDataset).selectOccurrences(pPredicate, pSortCriteria)) {
			T bean = this.read(record, pPermissions);
			if (bean != null) {
				list.add(bean);
			}
		}
		return list;
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset   A dataset in which to apply the filter.
	 * @param pPredicate An XPath predicate to use as filter.
	 * @param pLimit     The maximum number of Java Beans to be returned.
	 * @param pOffset    The number of first Java Beans to be ignored.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate, final Integer pLimit, final Integer pOffset) {
		return this.readAll(pDataset, pPredicate, pLimit, pOffset, Optional.empty());
	}

	/**
	 * Build a list of Java Bean from a filter on an EBX table.
	 *
	 * @param pDataset     A dataset in which to apply the filter.
	 * @param pPredicate   An XPath predicate to use as filter.
	 * @param pLimit       The maximum number of Java Beans to be returned.
	 * @param pOffset      The number of first Java Beans to be ignored.
	 * @param pPermissions Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Bean representing records.
	 */
	public List<T> readAll(final Adaptation pDataset, final String pPredicate, final Integer pLimit, Integer pOffset, final Optional<SessionPermissions> pPermissions) {
		List<T> list = new ArrayList<>();
		if (pLimit == null && pOffset == null) {
			return this.readAll(pDataset, pPredicate, pPermissions);
		}
		RequestResult result = this.getAdaptationTable(pDataset).createRequestResult(pPredicate);
		try {
			Adaptation record = result.nextAdaptation();
			if (pOffset == null) {
				pOffset = 0;
			}
			if (pLimit == null) {
				for (int i = 0; record != null; i++) {
					if (i >= pOffset) {
						T bean = this.read(record, pPermissions);
						if (bean != null) {
							list.add(bean);
						}
					}
					record = result.nextAdaptation();
				}
			} else {
				for (int i = 0; i < pLimit + pOffset && record != null; i++) {
					if (i >= pOffset) {
						T bean = this.read(record, pPermissions);
						if (bean != null) {
							list.add(bean);
						}
					}
					record = result.nextAdaptation();
				}
			}
		} finally {
			result.close();
		}
		return list;
	}

	/**
	 * Build a list of Java Beans from filtered EBX referenced table occurrences.
	 *
	 * @param pBean A Java Bean representing a table occurrence.
	 * @param pPath The path of the references in the table occurrence.
	 *
	 * @return A list of Java Beans representing referenced records.
	 */
	public List<T> readAll(final TableBean pBean, final Path pPath) {
		return this.readAll(pBean, pPath, Optional.empty(), Optional.empty());
	}

	/**
	 * Build a list of Java Beans from filtered EBX referenced table occurrences.
	 *
	 * @param pBean      A Java Bean representing a table occurrence.
	 * @param pPath      The path of the references in the table occurrence.
	 * @param pPredicate An XPath predicate to use as filter.
	 *
	 * @return A list of Java Beans representing referenced records.
	 */
	public List<T> readAll(final TableBean pBean, final Path pPath, final Optional<String> pPredicate) {
		return this.readAll(pBean, pPath, pPredicate, Optional.empty());
	}

	/**
	 * Build a list of Java Beans from filtered EBX referenced table occurrences.
	 *
	 * @param pBean        A Java Bean representing a table occurrence.
	 * @param pPath        The path of the references in the table occurrence.
	 * @param pPredicate   An XPath predicate to use as filter.
	 * @param pPermissions Permissions to be applied to the read of the records returned as java beans.
	 *
	 * @return A list of Java Beans representing referenced records.
	 */
	public List<T> readAll(final TableBean pBean, final Path pPath, final Optional<String> pPredicate, final Optional<SessionPermissions> pPermissions) {

		List<T> list = new ArrayList<>();
		Adaptation record = pBean.getEbxRecord();
		if (record != null) {
			list = this.readAll(AdaptationUtils.getLinkedRecords(record, pPath, pPredicate), pPermissions);
		} else {
			ValueContext context = pBean.getEbxContext();
			if (context != null) {
				Path pathToRecordRoot = SchemaUtils.getPathToRecordRoot(context.getNode());
				list = this.readAll(AdaptationUtils.getLinkedRecords(context, pathToRecordRoot.add(pPath), pPredicate), pPermissions);
			}
		}
		return list;
	}

	/**
	 * Update a table occurrence from a Java Bean
	 *
	 * @param pContext     A procedure context.
	 * @param pDataset     The dataset in which to update the table occurrence
	 * @param pBean        The Java Bean
	 * @param pPermissions Permissions to be applied to the update and the read of the table occurrence.
	 *
	 * @return A Java Bean representing the updated record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T update(final ProcedureContext pContext, final Adaptation pDataset, final T pBean) throws EBXCommonsException {
		return this.update(pContext, pDataset, pBean, Optional.empty());
	}

	/**
	 * Update a table occurrence from a Java Bean
	 *
	 * @param pContext     A procedure context.
	 * @param pDataset     The dataset in which to update the table occurrence
	 * @param pBean        The Java Bean
	 * @param pPermissions Permissions to be applied to the update and the read of the table occurrence.
	 *
	 * @return A Java Bean representing the updated record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T update(final ProcedureContext pContext, final Adaptation pDataset, final T pBean, final Optional<SessionPermissions> pPermissions) throws EBXCommonsException {
		Optional<Adaptation> record = this.getRecordFromJavaBean(pDataset, pBean);
		if (!record.isPresent()) {
			throw new BeansFunctionalException(EBXCommonsFunctionalReason.BEANS_NOT_BOUND);
		}
		ValueContextForUpdate vcfu = pContext.getContext(record.get().getAdaptationName());
		this.setValuesForUpdate(vcfu, pBean);
		try {
			record = Optional.of(pContext.doModifyContent(record.get(), vcfu));
		} catch (ConstraintViolationException | OperationException ex) {
			throw new BeansTechnicalException(ex);
		}
		T bean = (T) pBean.getDAO().read(record.get(), pPermissions);
		return bean;
	}

	/**
	 * Create a table occurrence from a Java Bean, or update it if it already exists.
	 *
	 * @param pContext A procedure context.
	 * @param pDataset The dataset in which to update the table occurrence
	 * @param pBean    The Java Bean
	 *
	 * @return A Java Bean representing the updated or created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T createOrUpdate(final ProcedureContext pContext, final Adaptation pDataset, final T pBean) throws EBXCommonsException {
		return this.createOrUpdate(pContext, pDataset, pBean, Optional.empty());
	}

	/**
	 * Create a table occurrence from a Java Bean, or update it if it already exists.
	 *
	 * @param pContext     A procedure context.
	 * @param pDataset     The dataset in which to update the table occurrence
	 * @param pBean        The Java Bean
	 * @param pPermissions Permissions to be applied to the update or creation and the read of the table occurrence.
	 *
	 * @return A Java Bean representing the updated or created record.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public T createOrUpdate(final ProcedureContext pContext, final Adaptation pDataset, final T pBean, final Optional<SessionPermissions> pPermissions) throws EBXCommonsException {
		Optional<Adaptation> record = this.getRecordFromJavaBean(pDataset, pBean);
		if (record.isPresent()) {
			return this.update(pContext, pDataset, pBean, pPermissions);
		} else {
			return this.create(pContext, pDataset, pBean, pPermissions);
		}
	}

	private Optional<Adaptation> getRecordFromJavaBean(final Adaptation pDataset, final T pBean) throws EBXCommonsException {
		Optional<Adaptation> record = Optional.ofNullable(pBean.getEbxRecord());
		if (!record.isPresent()) {
			ValueContext context = pBean.getEbxContext();
			if (context != null) {
				record = AdaptationUtils.getRecordForValueContext(context);
			}
			if (!record.isPresent()) {
				String pk = this.getRecordPrimaryKey(pBean);
				if (pk != null) {
					record = Optional.ofNullable(this.getAdaptationTable(pDataset).lookupAdaptationByPrimaryKey(PrimaryKey.parseString(pk)));
				}
			}
		}
		return record;
	}

	/**
	 * Delete a table occurrence from a Java Bean
	 *
	 * @param pContext A procedure context.
	 * @param pDataset The dataset in which to update the table occurrence
	 * @param pBean    The Java Bean
	 *
	 * @return true if the record has been deleted, false if no record found.
	 * @throws EBXCommonsException EBXCommonsException
	 */
	public boolean delete(final ProcedureContext pContext, final Adaptation pDataset, final T pBean) throws EBXCommonsException {
		Optional<Adaptation> record = this.getRecordFromJavaBean(pDataset, pBean);
		if (record.isPresent()) {
			try {
				pContext.doDelete(record.get().getAdaptationName(), false);
			} catch (ConstraintViolationException | OperationException ex) {
				throw new BeansTechnicalException(ex);
			}
			return true;
		}
		return false;
	}

	protected abstract void getValuesFromAdaptation(final T pBean, final Adaptation pRecord, Optional<SessionPermissions> pPermissions);

	protected abstract void getValuesFromValueContext(final T pBean, final ValueContext pContext, Optional<SessionPermissions> pPermissions);

	protected abstract void setValuesForUpdate(final ValueContextForUpdate pContext, final T pBean) throws EBXCommonsException;

}
