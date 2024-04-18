package com.tibco.ebx.cs.commons.component.trigger;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.schema.trigger.BeforeCreateOccurrenceContext;
import com.orchestranetworks.schema.trigger.BeforeModifyOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.schema.trigger.ValueChange;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;

/**
 * Trigger preventing from the creation of loops in a recursive relation. Loops can be prevented by constraints in EBX but this constraint is not always blocking updates which can âss via Web Services
 * of file import.
 *
 * It takes one parameter that is the path of the foreign to the parent record that must be of the same table. It works with multiple parents. <br>
 * {@code
 * <osd:trigger class="com.tibco.ebx.cs.commons.component.trigger.ForbidLoopTrigger">
 * 	<pathToParent>./parent</pathToParent>
 * </osd:trigger>}
 *
 * @author Mickaël Chevalier
 * @since 2.0.9
 */
public class ForbidLoopTrigger extends TableTrigger {

	private Path pathToParent;
	private SchemaNode nodeParent;

	@Override
	public void handleBeforeCreate(final BeforeCreateOccurrenceContext pContext) throws OperationException {
		List<Adaptation> parents = AdaptationUtils.getLinkedRecords(pContext.getOccurrenceContextForUpdate(), this.pathToParent, Optional.empty());
		HashSet<String> pks = new HashSet<>();
		this.checkLoop(parents, pks, pContext.getSession());
	}

	private void checkLoop(final List<Adaptation> pParents, final HashSet<String> pPKs, final Session pSession) throws OperationException {
		for (Adaptation parent : pParents) {
			HashSet<String> pks = (HashSet<String>) pPKs.clone();
			if (!pks.add(parent.getOccurrencePrimaryKey().format())) {
				String message = this.getErrorMessage(parent, pSession);
				throw OperationException.createError(message);
			} else {
				this.checkLoop(AdaptationUtils.getLinkedRecords(parent, this.pathToParent, null), pks, pSession);
			}
		}
	}

	private String getErrorMessage(final Adaptation pRecord, final Session pSession) {
		String message = "Cycle detected in table " + pRecord.getContainerTable().getTableNode().getLabel(pSession);
		message += " for record primary key '" + pRecord.getOccurrencePrimaryKey().format() + "'";
		return message;
	}

	@Override
	public void handleBeforeModify(final BeforeModifyOccurrenceContext pContext) throws OperationException {
		ValueChange change = pContext.getChanges().getChange(this.pathToParent);
		if (change != null) {
			Adaptation record = pContext.getAdaptationOccurrence();
			HashSet<String> pks = new HashSet<>();
			pks.add(record.getOccurrencePrimaryKey().format());
			List<Adaptation> parents = AdaptationUtils.getLinkedRecords(pContext.getOccurrenceContextForUpdate(), this.pathToParent, Optional.empty());
			this.checkLoop(parents, pks, pContext.getSession());
		}
		super.handleBeforeModify(pContext);
	}

	@Override
	public void setup(final TriggerSetupContext pContext) {
		this.nodeParent = pContext.getSchemaNode().getNode(this.pathToParent);
		SchemaFacetTableRef tableRef = this.nodeParent.getFacetOnTableReference();
		if (tableRef == null || tableRef.getContainerReference() != null || !tableRef.getTablePath().equals(pContext.getSchemaNode().getPathInSchema())) {
			pContext.addError("Path " + this.pathToParent + " is not a foreign key to the current table.");
		}
	}

	public Path getPathToParent() {
		return this.pathToParent;
	}

	public void setPathToParent(final Path pathToParent) {
		this.pathToParent = pathToParent;
	}
}
