/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.procedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.schema.ConstraintViolationException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * This procedure allows to cleanse foreign key fields by looking for a value in the target table. If the target table is composed of an id field as PK (./id) and a label field (./field). We want to
 * look for the label to return the PK.
 *
 * @author Aurélien Ticot
 * @since 1.4.0
 */
public class FixForeignKeyProcedure implements Procedure {
	private final HashMap<SchemaNode, List<Path>> foreignKeyNodes;
	private final RequestResult recordsToCleanseAsRequest;
	private final List<Adaptation> recordsToCleanseAsList;

	/**
	 * Instantiates a new FixForeignKey procedure.
	 *
	 * @param pForeignKeyNodes        the list of foreign key nodes and for each one the list of path to look for the value
	 * @param pRecordsToCleanseAsList the list of records to cleanse
	 * @throws IllegalArgumentException if one of the parameters is null.
	 * @since 1.4.0
	 */
	public FixForeignKeyProcedure(final HashMap<SchemaNode, List<Path>> pForeignKeyNodes, final List<Adaptation> pRecordsToCleanseAsList) throws IllegalArgumentException {
		if (pForeignKeyNodes == null) {
			throw new IllegalArgumentException("The list of nodes to cleanse shall not be null");
		}
		if (pRecordsToCleanseAsList == null) {
			throw new IllegalArgumentException("The list of records shall not be null");
		}
		this.foreignKeyNodes = pForeignKeyNodes;
		this.recordsToCleanseAsRequest = null;
		this.recordsToCleanseAsList = pRecordsToCleanseAsList;
	}

	/**
	 * Instantiates a new FixForeignKey procedure.
	 *
	 * @param pForeignKeyNodes           the list of foreign key nodes and for each one the list of path to look for the value
	 * @param pRecordsToCleanseAsRequest the request to get the records
	 * @throws IllegalArgumentException if one of the parameters is null.
	 * @since 1.4.0
	 */
	public FixForeignKeyProcedure(final HashMap<SchemaNode, List<Path>> pForeignKeyNodes, final RequestResult pRecordsToCleanseAsRequest) throws IllegalArgumentException {
		if (pForeignKeyNodes == null) {
			throw new IllegalArgumentException("The list of nodes to cleanse shall not be null");
		}
		if (pRecordsToCleanseAsRequest == null) {
			throw new IllegalArgumentException("The request result shall not be null");
		}
		this.foreignKeyNodes = pForeignKeyNodes;
		this.recordsToCleanseAsRequest = pRecordsToCleanseAsRequest;
		this.recordsToCleanseAsList = null;
	}

	/**
	 * Instantiates a new FixForeignKey procedure.
	 *
	 * @param pForeignKeyNode         the node of the foreign key
	 * @param pPathToLookFor          the path, in the targeted table, to look for the value
	 * @param pRecordsToCleanseAsList the list of records to cleanse
	 * @throws IllegalArgumentException if one of the parameters is null.
	 * @since 1.4.0
	 */
	public FixForeignKeyProcedure(final SchemaNode pForeignKeyNode, final Path pPathToLookFor, final List<Adaptation> pRecordsToCleanseAsList) throws IllegalArgumentException {
		if (pForeignKeyNode == null) {
			throw new IllegalArgumentException("The node to cleanse shall not be null");
		}
		if (pPathToLookFor == null) {
			throw new IllegalArgumentException("The path to look for shall not be null");
		}
		if (pRecordsToCleanseAsList == null) {
			throw new IllegalArgumentException("The list of records shall not be null");
		}
		this.foreignKeyNodes = new HashMap<>();
		List<Path> paths = new ArrayList<>();
		paths.add(pPathToLookFor);
		this.foreignKeyNodes.put(pForeignKeyNode, paths);
		this.recordsToCleanseAsRequest = null;
		this.recordsToCleanseAsList = pRecordsToCleanseAsList;
	}

	/**
	 * Instantiates a new FixForeignKey procedure.
	 *
	 * @param pForeignKeyNode            the node of the foreign key
	 * @param pPathToLookFor             the path, in the targeted table, to look for the value
	 * @param pRecordsToCleanseAsRequest the request to get the records
	 * @throws IllegalArgumentException if one of the parameters is null.
	 * @since 1.4.0
	 */
	public FixForeignKeyProcedure(final SchemaNode pForeignKeyNode, final Path pPathToLookFor, final RequestResult pRecordsToCleanseAsRequest) throws IllegalArgumentException {
		if (pForeignKeyNode == null) {
			throw new IllegalArgumentException("The node to cleanse shall not be null");
		}
		if (pPathToLookFor == null) {
			throw new IllegalArgumentException("The path to look for shall not be null");
		}
		if (pRecordsToCleanseAsRequest == null) {
			throw new IllegalArgumentException("The request result shall not be null");
		}
		this.foreignKeyNodes = new HashMap<>();
		List<Path> paths = new ArrayList<>();
		paths.add(pPathToLookFor);
		this.foreignKeyNodes.put(pForeignKeyNode, paths);
		this.recordsToCleanseAsRequest = pRecordsToCleanseAsRequest;
		this.recordsToCleanseAsList = null;
	}

	// TODO add fuzzy search

	@Override
	public void execute(final ProcedureContext pProcedureContext) throws Exception {
		// Check there's at least a record to cleanse
		if (this.recordsToCleanseAsList != null && this.recordsToCleanseAsRequest == null) {
			if (this.recordsToCleanseAsList.isEmpty()) {
				return;
			}
		} else if (this.recordsToCleanseAsRequest != null && this.recordsToCleanseAsList == null) {
			if (this.recordsToCleanseAsRequest.isEmpty()) {
				return;
			}
		} else if (this.recordsToCleanseAsRequest == null && this.recordsToCleanseAsList == null) {
			return;
		}

		// Iterate over the list or the request result
		if (this.recordsToCleanseAsList != null) {
			for (Adaptation record : this.recordsToCleanseAsList) {
				this.cleanseRecord(pProcedureContext, record);
			}
		} else {
			try {
				Adaptation record = null;
				while ((record = this.recordsToCleanseAsRequest.nextAdaptation()) != null) {
					this.cleanseRecord(pProcedureContext, record);
				}
			} finally {
				this.recordsToCleanseAsRequest.close();
			}
		}
	}

	/**
	 * Cleanse a give record
	 *
	 * @param pProcedureContext the procedure context
	 * @param pRecord           the record to cleanse
	 * @throws ConstraintViolationException if a record to cleanse contains values that do not comply with constraints in blocking mode.
	 * @throws OperationException           if a record to cleanse is null or is in occulting mode.
	 * @since 1.4.0
	 */
	private void cleanseRecord(final ProcedureContext pProcedureContext, final Adaptation pRecord) throws ConstraintViolationException, OperationException {
		// Iterate on the foreign key nodes
		Iterator<Entry<SchemaNode, List<Path>>> iterator = this.foreignKeyNodes.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<SchemaNode, List<Path>> foreignKey = iterator.next();
			SchemaNode foreignKeyNode = foreignKey.getKey();

			// Get the value for that FK node
			String currentValue = pRecord.getString(foreignKeyNode.getPathInAdaptation());
			if (currentValue == null) {
				continue;
			}

			// Check the facet tab ref
			SchemaFacetTableRef facet = foreignKeyNode.getFacetOnTableReference();
			if (facet == null) {
				continue;
			}

			// Get the target table
			AdaptationTable targetTable = facet.getTable(pRecord.createValueContext());
			if (targetTable == null) {
				continue;
			}

			List<Path> paths = foreignKey.getValue();
			if (paths == null || paths.isEmpty()) {
				continue;
			}

			// build a predicate to look for the value in the different paths of the target table
			StringBuilder predicate = new StringBuilder("");
			boolean first = true;
			for (Path path : paths) {
				if (!first) {
					predicate.append(" or ");
				}
				predicate.append(path.format() + "='" + currentValue + "'");
			}

			// Get the first record matching
			Adaptation foundRecord = targetTable.lookupFirstRecordMatchingPredicate(predicate.toString());
			if (foundRecord == null) {
				continue;
			}

			String newValue = foundRecord.getOccurrencePrimaryKey().format();

			if (currentValue.equals(newValue)) {
				continue;
			}

			ValueContextForUpdate valueContext = pProcedureContext.getContext(pRecord.getAdaptationName());
			valueContext.setValue(newValue, foreignKeyNode.getPathInAdaptation());
			pProcedureContext.doModifyContent(pRecord, valueContext);
		}
	}
}
