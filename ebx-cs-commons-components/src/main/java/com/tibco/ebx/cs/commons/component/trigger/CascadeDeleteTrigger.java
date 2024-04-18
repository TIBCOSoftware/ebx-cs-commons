package com.tibco.ebx.cs.commons.component.trigger;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.trigger.AfterDeleteOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.OperationException;
import com.tibco.ebx.cs.commons.lib.procedure.DeleteRecordsProcedure;
import com.tibco.ebx.cs.commons.lib.repository.RepositoryUtils;

/**
 * @author Mickaël Chevalier
 *
 *         Delete record in cascade.<br>
 *         {@code
 *
 *	<osd:trigger class="com.tibco.ebx.cs.commons.trigger.CascadeDeleteTrigger">
 *		<dataspace>a data space name or null</dataspace>
 *		<dataset>a data set name or null(only if dataspace is also null)</dataset>
 *		<table>a table path</table>
 *		<reference>a path to a foreign key</reference>
 *	</osd:trigger><}
 *
 * @throw {@link OperationException} if the dataspace, the dataset, the table or the reference (node) cannot be found
 * @throw {@link OperationException} if the reference is not a foreign key
 * @throw {@link OperationException} if the deletion fails
 */
public class CascadeDeleteTrigger extends TableTrigger {

	private String dataspace;
	private String dataset;
	private Path table;
	private Path reference;

	@Override
	public void setup(final TriggerSetupContext pContext) {
		// no implementation
	}

	@Override
	public void handleAfterDelete(final AfterDeleteOccurrenceContext pContext) throws OperationException {
		super.handleAfterDelete(pContext);

		Adaptation instance = RepositoryUtils.getDataSetFrom(pContext.getTable().getContainerAdaptation(), this.dataspace, this.dataset);
		if (instance == null) {
			throw OperationException.createError("Not able to retrieve dataset with dataspace '" + this.dataspace + "' and dataset '" + this.dataset + "'.");
		}

		AdaptationTable adaTable = pContext.getTable();
		if (this.table != null) {
			adaTable = instance.getTable(this.table);
			if (adaTable == null) {
				throw OperationException.createError("Not able to retrieve table '" + this.table + "' in dataset '" + instance.getAdaptationName().getStringName() + "'.");
			}
		}

		SchemaNode node = adaTable.getTableOccurrenceRootNode().getNode(this.reference);
		if (node == null) {
			throw OperationException.createError("Not able to retrieve node '" + this.reference + "' in table '" + adaTable.getTablePath().format() + "'.");
		}

		if (node.getFacetOnTableReference() != null) {
			if (!node.getFacetOnTableReference().getTableNode().equals(pContext.getTable().getTableNode())) {
				throw OperationException
						.createError("Node '" + this.reference + "' in table '" + adaTable.getTablePath().format() + "' is not referencing " + pContext.getTable().getTablePath().format() + ".");
			}
			PrimaryKey pk = pContext.getTable().computePrimaryKey(pContext.getOccurrenceContext());
			DeleteRecordsProcedure proc = new DeleteRecordsProcedure(adaTable.selectOccurrences(this.reference.format() + " = '" + pk.format() + "'"));
			try {
				proc.execute(pContext.getProcedureContext());
			} catch (Exception ex) {
				throw OperationException.createError(ex.getMessage());
			}
		} else {
			throw OperationException.createError("Node '" + this.reference + "' in table '" + adaTable.getTablePath().format() + "' is not a foreign key.");
		}
	}

	public String getDataspace() {
		return this.dataspace;
	}

	public void setDataspace(final String dataspace) {
		this.dataspace = dataspace;
	}

	public String getDataset() {
		return this.dataset;
	}

	public void setDataset(final String dataset) {
		this.dataset = dataset;
	}

	public Path getTable() {
		return this.table;
	}

	public void setTable(final Path table) {
		this.table = table;
	}

	public Path getReference() {
		return this.reference;
	}

	public void setReference(final Path reference) {
		this.reference = reference;
	}
}
