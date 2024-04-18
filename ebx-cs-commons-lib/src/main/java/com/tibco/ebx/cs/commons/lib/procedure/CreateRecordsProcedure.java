package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * The Class CreateRecordsProcedure is a generic procedure for creating records.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class CreateRecordsProcedure implements Procedure {
	private boolean contextFormExisting = false;
	private List<RecordValuesBean> recordDefinitions;
	private final List<Adaptation> createdRecords = new ArrayList<>();
	private boolean allPrivileges = true;

	/**
	 * Instantiates a new record creation procedure.<br>
	 * For a creation, the table is required from the record definition(s) (RecordValueBean). A value map is optional (if null, the new record will be empty, except the PK).<br>
	 * For a duplication, the table and a record are required from the record definition(s) (RecordValueBean). ContextFromExisting must be set to true. A value map is also optional.<br>
	 * <br>
	 * If the primary key is not computed/auto-incremented or define in the value map, an exception will be raised.
	 *
	 * @since 1.0.0
	 */
	public CreateRecordsProcedure() {
		super();
	}

	/**
	 * Instantiates a new record creation procedure.<br>
	 * For a creation, the table is required from the record definition(s) (RecordValueBean). A value map is optional (if null, the new record will be empty, except the PK).<br>
	 * For a duplication, the table and a record are required from the record definition(s) (RecordValueBean). ContextFromExisting must be set to true. A value map is also optional.<br>
	 * <br>
	 * If the primary key is not computed/auto-incremented or define in the value map, an exception will be raised.
	 *
	 * @param recordDefinitions the record definitions
	 * @since 1.0.0
	 */
	public CreateRecordsProcedure(final List<RecordValuesBean> recordDefinitions) {
		super();
		this.recordDefinitions = recordDefinitions;
	}

	/**
	 * Instantiates a new record creation procedure.<br>
	 * For a creation, the table is required from the record definition(s) (RecordValueBean). A value map is optional (if null, the new record will be empty, except the PK).<br>
	 * For a duplication, the table and a record are required from the record definition(s) (RecordValueBean). ContextFromExisting must be set to true. A value map is also optional.<br>
	 * <br>
	 * If the primary key is not computed/auto-incremented or define in the value map, an exception will be raised.
	 *
	 * @param recordDefinitions   the record definitions
	 * @param contextFormExisting define if the value context is defined from an existing record (duplication) but can be overriden after by the value map.
	 * @since 1.0.0
	 */
	public CreateRecordsProcedure(final List<RecordValuesBean> recordDefinitions, final boolean contextFormExisting) {
		super();
		this.contextFormExisting = contextFormExisting;
		this.recordDefinitions = recordDefinitions;
	}

	/**
	 * Instantiates a new record creation procedure.<br>
	 * For a creation, the table is required from the record definition(s) (RecordValueBean). A value map is optional (if null, the new record will be empty, except the PK).<br>
	 * For a duplication, the table and a record are required from the record definition(s) (RecordValueBean). ContextFromExisting must be set to true. A value map is also optional.<br>
	 * <br>
	 * If the primary key is not computed/auto-incremented or define in the value map, an exception will be raised.
	 *
	 * @param recordDefinition the record definition
	 * @since 1.0.0
	 */
	public CreateRecordsProcedure(final RecordValuesBean recordDefinition) {
		super();
		this.recordDefinitions = new ArrayList<>();
		this.recordDefinitions.add(recordDefinition);
	}

	/**
	 * Instantiates a new record creation procedure.<br>
	 * For a creation, the table is required from the record definition(s) (RecordValueBean). A value map is optional (if null, the new record will be empty, except the PK).<br>
	 * For a duplication, the table and a record are required from the record definition(s) (RecordValueBean). ContextFromExisting must be set to true. A value map is also optional.<br>
	 * <br>
	 * If the primary key is not computed/auto-incremented or define in the value map, an exception will be raised.
	 *
	 * @param recordDefinition    the record definition
	 * @param contextFormExisting define if the value context is defined from an existing record (duplication) but can be overriden after by the value map.
	 * @since 1.0.0
	 */
	public CreateRecordsProcedure(final RecordValuesBean recordDefinition, final boolean contextFormExisting) {
		super();
		this.contextFormExisting = contextFormExisting;
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
			AdaptationTable table = recordDefinition.getTable();
			if (table == null) {
				continue;
			}

			ValueContextForUpdate newOccurrenceContext = pContext.getContextForNewOccurrence(table);
			Adaptation recordToCopy = recordDefinition.getRecord();
			if (this.contextFormExisting && recordToCopy != null) {
				newOccurrenceContext = pContext.getContextForNewOccurrence(recordToCopy, table);
			}

			HashMap<Path, Object> values = recordDefinition.getValues();
			CreateRecordsProcedure.setValues(newOccurrenceContext, values);
			Adaptation createdRecord = pContext.doCreateOccurrence(newOccurrenceContext, table);
			this.createdRecords.add(createdRecord);
		}
	}

	/**
	 * Gets the created records.
	 *
	 * @return the created records
	 * @since 1.0.0
	 */
	public List<Adaptation> getCreatedRecords() {
		return this.createdRecords;
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
	 * Checks whether the context is defined form an existing (duplication).
	 *
	 * @return true, if is context form existing
	 * @since 1.0.0
	 */
	public boolean isContextFormExisting() {
		return this.contextFormExisting;
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
	 * Sets true to get the context from an existing record (duplication). The existing record must be define in a RecordValueBean.
	 *
	 * @param contextFormExisting the new context form existing
	 * @since 1.0.0
	 */
	public void setContextFormExisting(final boolean contextFormExisting) {
		this.contextFormExisting = contextFormExisting;
	}

	/**
	 * Sets the record definition.
	 *
	 * @param recordDefinition the new record definition
	 * @since 1.0.0
	 */
	public void setRecordDefinition(final RecordValuesBean recordDefinition) {
		this.recordDefinitions = new ArrayList<>();
		this.recordDefinitions.add(recordDefinition);
	}

	/**
	 * Sets the record definitions.
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
