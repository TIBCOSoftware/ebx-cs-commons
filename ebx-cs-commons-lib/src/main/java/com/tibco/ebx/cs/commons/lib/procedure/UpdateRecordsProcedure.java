/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * The Class UpdateRecordsProcedure.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class UpdateRecordsProcedure implements Procedure {
	private List<RecordValuesBean> recordDefinitions;
	private boolean allPrivileges = true;

	/**
	 * Instantiates a new update records procedure. A record definition (RecordValueBean) must be set with the appropriate setter or the other constructors. The record and the values of the
	 * RecordValueBean are required for the update procedure.
	 *
	 * @since 1.0.0
	 */
	public UpdateRecordsProcedure() {
		super();
	}

	/**
	 * Instantiates a new update records procedure. The record and the values of the RecordValueBean are required for the update procedure.
	 *
	 * @param recordDefinitions the record definitions
	 * @since 1.0.0
	 */
	public UpdateRecordsProcedure(final List<RecordValuesBean> recordDefinitions) {
		super();
		this.recordDefinitions = recordDefinitions;
	}

	/**
	 * Instantiates a new update records procedure. The record and the values of the RecordValueBean are required for the update procedure.
	 *
	 * @param recordDefinition the record definition
	 * @since 1.0.0
	 */
	public UpdateRecordsProcedure(final RecordValuesBean recordDefinition) {
		super();
		this.recordDefinitions = new ArrayList<>();
		this.recordDefinitions.add(recordDefinition);
	}

	/**
	 * Execute the procedure.
	 *
	 * @param pContext the context
	 * @throws Exception the exception
	 * @since 1.0.0
	 */
	@Override
	public void execute(final ProcedureContext pContext) throws Exception {
		pContext.setAllPrivileges(this.allPrivileges);

		if (this.recordDefinitions == null) {
			return;
		}

		for (RecordValuesBean recordDefinition : this.recordDefinitions) {
			Adaptation record = recordDefinition.getRecord();
			if (record == null || record.isDeleted()) {
				continue;
			}
			HashMap<Path, Object> values = recordDefinition.getValues();
			ValueContextForUpdate valueContext = pContext.getContext(record.getAdaptationName());
			UpdateRecordsProcedure.setValues(valueContext, values);
			pContext.doModifyContent(record, valueContext);
		}
	}

	/**
	 * Gets the record definitions.
	 *
	 * @return the record definitions
	 * @since 1.0.0
	 */
	public List<RecordValuesBean> getRecordDefinitions() {
		return this.recordDefinitions;
	}

	/**
	 * Sets the all privileges. If true execute the procedure without any permission restriction. Default is true;
	 *
	 * @param pAllPrivileges the new all privileges
	 * @since 1.2.0
	 */
	public void setAllPrivileges(final boolean pAllPrivileges) {
		this.allPrivileges = pAllPrivileges;
	}

	/**
	 * Sets the record definition. The record and the values of the RecordValueBean are required for the update procedure.
	 *
	 * @param recordDefinition the new record definitions
	 * @since 1.0.0
	 */
	public void setRecordDefinition(final RecordValuesBean recordDefinition) {
		this.recordDefinitions = new ArrayList<>();
		this.recordDefinitions.add(recordDefinition);
	}

	/**
	 * Sets the record definitions. The record and the values of the RecordValueBean are required for the update procedure.
	 *
	 * @param recordDefinitions the new record definitions
	 * @since 1.0.0
	 */
	public void setRecordDefinitions(final List<RecordValuesBean> recordDefinitions) {
		this.recordDefinitions = recordDefinitions;
	}

	/**
	 * Sets the values.
	 *
	 * @param pContext the context
	 * @param pValues  the values
	 * @since 1.0.0
	 */
	private static void setValues(final ValueContextForUpdate pContext, final HashMap<Path, Object> pValues) {
		if (pValues != null) {
			Iterator<Entry<Path, Object>> iterator = pValues.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Path, Object> valueDef = iterator.next();
				Path path = valueDef.getKey();
				Object value = valueDef.getValue();
				pContext.setValue(value, path);
			}
		}
	}

}
