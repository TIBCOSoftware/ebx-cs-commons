package com.tibco.ebx.cs.commons.component.permission;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Session;

/***
 * @author Mickaël Chevalier
 */
public class HideNullFieldAccessRule implements AccessRule {
	@Override
	public AccessPermission getPermission(final Adaptation adaptation, final Session session, final SchemaNode node) {
		if (adaptation.isHistory() || adaptation.isSchemaInstance()) {
			return AccessPermission.getReadWrite();
		}

		// Hide field if null
		if (adaptation.get(node.getPathInAdaptation()) == null) {
			return AccessPermission.getHidden();
		} else {
			return AccessPermission.getReadWrite();
		}
	}
}
