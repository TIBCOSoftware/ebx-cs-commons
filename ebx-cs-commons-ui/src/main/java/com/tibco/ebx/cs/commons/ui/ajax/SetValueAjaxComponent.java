package com.tibco.ebx.cs.commons.ui.ajax;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.info.SchemaFacetTableRef;
import com.orchestranetworks.ui.UIAjaxComponent;
import com.orchestranetworks.ui.UIAjaxContext;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;
import com.tibco.ebx.cs.commons.ui.util.UIUtils;

/**
 * This ajax component is called by SetValueEditor. It gets the value from a
 * referenced records (FK) and returns it.
 *
 * @author Mickaël Chevalier
 *
 * @see SetValueEditor
 */
public class SetValueAjaxComponent extends UIAjaxComponent {
	public static final String AJAX_COMP_NAME = "SetValueAjaxComponent";
	public static final String VALUE_PARAM = "value";
	public static final String NODE_ID_PARAM = "node";
	public static final String PATH_SOURCE_PARAM = "pathToSource";

	@Override
	public void doAjaxResponse(final UIAjaxContext pContext) {
		String value = pContext.getOptionalRequestParameterValue(SetValueAjaxComponent.VALUE_PARAM).trim();
		String nodeId = pContext.getOptionalRequestParameterValue(SetValueAjaxComponent.NODE_ID_PARAM).trim();

		Path path = UIUtils.getNodePathFromUniqueWebIdentier(nodeId);
		String pathToSource = pContext.getOptionalRequestParameterValue(SetValueAjaxComponent.PATH_SOURCE_PARAM);

		SchemaNode node = SchemaUtils.getNode(pContext.getCurrentRecord(), Path.SELF.add(path));
		SchemaFacetTableRef tableRefFacet = node.getFacetOnTableReference();
		if (tableRefFacet == null) {
			return;
		}

		AdaptationTable table = pContext.getCurrentAdaptation().getContainer().getTable(tableRefFacet.getTablePath());

		Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(value));
		if (record == null) {
			return;
		}

		pContext.add(record.get(Path.SELF.add(Path.parse(pathToSource))) + "");
	}
}