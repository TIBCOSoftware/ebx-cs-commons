package com.tibco.ebx.cs.commons.beans.generator.template;

import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.SessionPermissions;
import com.orchestranetworks.service.ValueContextForUpdate;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

/**
 * Object to access data of a dataset.
 *
 * @author Mickaël Chevalier
 * @since 2.0.0
 *
 * @param <T> DatasetBean type
 */
public abstract class DatasetDAO<T extends DatasetBean> {

	protected final Adaptation ebxDataset;

	/**
	 * Create a DAO from an {@link Adaptation} to access the underlying dataset.
	 * 
	 * @param pDataset underlying dataset
	 */
	public DatasetDAO(final Adaptation pDataset) {
		this.ebxDataset = pDataset;
	}

	/**
	 * Get instance of a bean
	 * 
	 * @param pDataset dataset
	 * @return instance of a bean
	 * @throws OperationException OperationException
	 */
	public abstract T getInstanceOfBean(Adaptation pDataset) throws OperationException;

	public abstract T getInstanceOfBean() throws OperationException;

	protected abstract void setValuesForUpdate(final ValueContextForUpdate pContext, final T pBean) throws EBXCommonsException;

	protected abstract void getValuesFromAdaptation(final T pBean, final Adaptation pRecord, Optional<SessionPermissions> pPermissions);

	protected abstract void getValuesFromValueContext(final T pBean, final ValueContext pContext, Optional<SessionPermissions> pPermissions);
}
