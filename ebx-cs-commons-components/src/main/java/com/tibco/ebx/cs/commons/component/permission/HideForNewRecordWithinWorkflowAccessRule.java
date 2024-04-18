package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;

//
//TODO: This will also hide columns in a table, which would be an issue if you're showing a table view in a workflow.
//    We currently don't have a way to detect when it's a table view vs. a new record context.
//
/**
 * As the name suggests, use this rule when you need to hide a field when creating a new record within a workflow.
 * 
 * @author Mickaël Chevalier
 */
public class HideForNewRecordWithinWorkflowAccessRule implements AccessRule {
	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (adaptation.isHistory()) {
			return AccessPermission.getReadWrite();
		}
		// Hide field for New context within a Workflow
		if (adaptation.isSchemaInstance() && session.getInteraction(true) != null) {
			return AccessPermission.getHidden();
		} else {
			return AccessPermission.getReadWrite();
		}
	}
}
