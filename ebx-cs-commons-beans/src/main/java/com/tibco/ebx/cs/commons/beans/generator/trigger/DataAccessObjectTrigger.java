/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.trigger;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.ConstraintViolationException;
import com.orchestranetworks.schema.trigger.AfterCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.AfterModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.ProcedureContext;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.DataAccessObject;
import com.tibco.ebx.cs.commons.beans.generator.generated.bean.Table;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.DataAccessObjectDAO;
import com.tibco.ebx.cs.commons.beans.generator.generated.dao.ModelTableDAO;
import com.tibco.ebx.cs.commons.lib.exception.EBXCommonsException;

/**
 * Data Access Object Trigger
 *
 * @author Mickaël Chevalier
 * @since 1.1.6
 */
public class DataAccessObjectTrigger extends TableTrigger {

	@Override
	public void setup(final TriggerSetupContext aContext) {
		// nothing to setup
	}

	@Override
	public void handleAfterCreate(final AfterCreateOccurrenceContext pContext) throws OperationException {
		super.handleAfterCreate(pContext);
		try {
			checkTableDAO(pContext.getAdaptationOccurrence(), pContext.getProcedureContext());
		} catch (ConstraintViolationException | EBXCommonsException ex) {
			throw OperationException.createError(ex);
		}
	}

	@Override
	public void handleAfterModify(final AfterModifyOccurrenceContext pContext) throws OperationException {
		super.handleAfterModify(pContext);
		try {
			checkTableDAO(pContext.getAdaptationOccurrence(), pContext.getProcedureContext());
		} catch (ConstraintViolationException | EBXCommonsException ex) {
			throw OperationException.createError(ex);
		}
	}

	private static void checkTableDAO(final Adaptation pRecord, final ProcedureContext pContext)
			throws ConstraintViolationException, EBXCommonsException {
		DataAccessObject dao = DataAccessObjectDAO.getInstance().read(pRecord);
		Table table = dao.getTable();
		if (table != null && table.getDaobject() == null) {
			table.setDaobject(dao);
			ModelTableDAO.getInstance().update(pContext, table.getEbxRecord(), table);
		}
	}
}
