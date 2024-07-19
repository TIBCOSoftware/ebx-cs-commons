/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.schema.info.SchemaNodeInformation;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.UIFormLabelSpec;
import com.orchestranetworks.ui.UIFormLabelSpec.DocumentationPane;
import com.orchestranetworks.ui.UIFormRequestContext;
import com.orchestranetworks.ui.form.UIForm;
import com.orchestranetworks.ui.form.UIFormBody;
import com.orchestranetworks.ui.form.UIFormBottomBar;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWithTabs;
import com.orchestranetworks.ui.form.UIFormPaneWithTabs.Tab;
import com.orchestranetworks.ui.form.UIFormPaneWriter;
import com.orchestranetworks.ui.form.UIFormRow;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;
import com.tibco.ebx.cs.commons.ui.util.UIUtils;

/**
 * Set on a table, this form will then hide or show every fields according to one or many types.
 *
 * A field belongs to one or many types as soon as they are listed in the field "information" of the data model element. Types are here listed separated by ";" (e.g : customer;supplier). A field can
 * be made mandatory for a given type adding ",true" to the type name (e.g : customer,true;supplier,false)
 *
 * Types are specified by one or many field of the current object. The list of paths leading to types must be entered separated by ";" in the unique parameter of this form. Also, widgets must be set
 * on these fields to trigger the refresh of the form. It can be set to a combo box, a boolean, or a complex made of booleans.
 *
 * Types can also be specified by a collection stored in a referenced record (FK). In this case pathToTypeConfigurator and pathToCollectionInConfigurator must be set and pathToTypes will be ignored.
 * The collection must be a list of string representing field paths. The parameter ignoreFieldsWithInformationEquals allow to specify a information that will make nodes carrying it always accessible.
 *
 * @see TypeAsComboBoxWidgetFactory
 * @see TypeAsBooleansWidgetFactory
 * @author Mickaël Chevalier
 *
 */
public class DynamicForm extends UIForm {
	/**
	 * Default constructor
	 */
	public DynamicForm() {
		super();
	}

	public void setIgnoreFieldsWithInformationEquals(final String ignoreFieldsWithInformationEquals) {
		this.ignoreFieldsWithInformationEquals = ignoreFieldsWithInformationEquals;
	}

	private class UIFormPaneForNode implements UIFormPane {
		private final SchemaNode node;

		public UIFormPaneForNode(final SchemaNode pNode) {
			this.node = pNode;
		}

		@Override
		public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
			DynamicForm.this.writePane(this.node, UIUtils.getCurrentAdaptation(pContext), pWriter);
		}
	}

	private class UIFormPaneGeneral implements UIFormPane {
		@Override
		public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
			DynamicForm.this.addJavascript(pWriter, pContext);
			SchemaNode rootNode = pContext.getCurrentTable().getTableOccurrenceRootNode();
			DynamicForm.this.writePane(rootNode, UIUtils.getCurrentAdaptation(pContext), pWriter);
		}
	}

	private static final String ILLEGAL_STATE_MESSAGE = "Type should be either a string or a complex type composed by boolean fields";

	private Path pathToTypeConfigurator;
	private Path pathToCollectionInConfigurator;
	private String ignoreFieldsWithInformationEquals;
	private String pathToTypes;

	public String getPathToTypes() {
		return this.pathToTypes;
	}

	public void setPathToTypeConfigurator(final Path pathToTypeConfigurator) {
		this.pathToTypeConfigurator = pathToTypeConfigurator;
	}

	public void setPathToCollectionInConfigurator(final Path pathToCollectionInConfigurator) {
		this.pathToCollectionInConfigurator = pathToCollectionInConfigurator;
	}

	private List<SchemaNode> tabs = new ArrayList<>();
	private Map<String, List<String>> typesMap = new HashMap<>();

	private final List<SchemaNode> typesAsListsOfStrings = new ArrayList<>();
	private final List<SchemaNode> typesAsBoolean = new ArrayList<>();

	private static void addFormRow(final SchemaNode pNode, final UIFormPaneWriter pWriter, final Locale locale, final List<String> pTypes) {
		UIFormRow formRow = pWriter.newFormRow(Path.SELF.add(pNode.getPathInAdaptation()));
		formRow.setRowId(UIUtils.getUniqueWebIdentifierForNode(pNode));
		DocumentationPane docPane = new DocumentationPane(pNode.getPathInSchema(), true);
		UIFormLabelSpec labelSpec = new UIFormLabelSpec(docPane, pNode.getLabel(locale));
		if (SchemaUtils.isNodeMandatory(pNode, pTypes)) {
			labelSpec.setMandatoryIndicator();
		}
		formRow.setLabel(labelSpec);
		pWriter.addFormRow(formRow);
	}

	private void addJavascript(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		if (this.isTypesConfiguredInModel()) {
			this.resolveTypesNodes(pContext);
		}

		this.addJsFnToGetTypes(pWriter, pContext);

		this.addJsNodesToTypesMap(pWriter, pContext);

		this.addJsTabsRelatedMaps(pWriter, pContext);

		DynamicForm.addJsFnToRefreshForm(pWriter);

		addJsFnContains(pWriter);

		pWriter.addJS_cr("refreshForm();");
	}

	private static void addJsFnContains(final UIFormPaneWriter pWriter) {
		pWriter.addJS_cr("function contains(array, element){");
		pWriter.addJS_cr("for(var i=0; i< array.length; i++){");
		pWriter.addJS_cr("if(array[i] === element){");
		pWriter.addJS_cr("return true;");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr("return false;");
		pWriter.addJS_cr("}");
	}

	private void addJsFnToGetTypes(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		pWriter.addJS_cr("function getTypes(){");
		pWriter.addJS_cr("var types = [];");
		pWriter.addJS_cr("var count = 0;");

		Adaptation currentAdaptation = UIUtils.getCurrentAdaptation(pContext);
		if (this.isTypesConfiguredInModel()) {
			for (SchemaNode node : this.typesAsListsOfStrings) {
				if (pContext.getSession().getPermissions().getNodeAccessPermission(node, currentAdaptation).isHidden()) {
					continue;
				}
				pWriter.addJS("var type = ");
				pWriter.addJS_getNodeValue(Path.SELF.add(node.getPathInAdaptation()));
				pWriter.addJS_cr(";");
				pWriter.addJS_cr("if(type!=null){");
				pWriter.addJS_cr("if(type.key !=null){");
				pWriter.addJS_cr("types[count] = type.key;");
				pWriter.addJS_cr("}else{");
				pWriter.addJS_cr("types[count] = type;");
				pWriter.addJS_cr("}");
				pWriter.addJS_cr("count++;");
				pWriter.addJS_cr("}");
			}

			for (SchemaNode node : this.typesAsBoolean) {
				if (pContext.getSession().getPermissions().getNodeAccessPermission(node, currentAdaptation).isHidden()) {
					continue;
				}
				String name = node.getPathInSchema().getLastStep().format();
				if (UIUtils.isNodeReadOnlyOrHidden(node, pContext)) {
					if (pContext.getCurrentRecord() != null && pContext.getCurrentRecord().get_boolean(node.getPathInAdaptation())) {
						pWriter.addJS_cr("types[count] = '" + node.getPathInSchema().getLastStep().format() + "';");
						pWriter.addJS_cr("count++;");
					}
				} else {
					pWriter.addJS("if(document.getElementById('" + name + "').checked){");
					pWriter.addJS_cr("types[count] = '" + node.getPathInSchema().getLastStep().format() + "';");
					pWriter.addJS_cr("count++;");
					pWriter.addJS_cr("}");
				}
			}
		} else {
			pWriter.addJS("var type = ");
			pWriter.addJS_getNodeValue(Path.SELF.add(this.pathToTypeConfigurator));
			pWriter.addJS_cr(";");
			pWriter.addJS_cr("if(type!=null){");
			pWriter.addJS_cr("if(type.key !=null){");
			pWriter.addJS_cr("types[count] = type.key;");
			pWriter.addJS_cr("}else{");
			pWriter.addJS_cr("types[count] = type;");
			pWriter.addJS_cr("}");
			pWriter.addJS_cr("count++;");
			pWriter.addJS_cr("}");
		}

		pWriter.addJS("return types;");
		pWriter.addJS_cr("}");
	}

	// TODO replace private code
	private static void addJsFnToRefreshForm(final UIFormPaneWriter pWriter) {
		pWriter.addJS_cr("function refreshForm(){");
		pWriter.addJS_cr("var types = getTypes();");
		pWriter.addJS_cr("for(var node in nodesToTypes){");
		pWriter.addJS_cr("for(var i=0; i< nodesToTypes[node].length; i++){");
		pWriter.addJS_cr("var div = document.getElementById(node);");
		pWriter.addJS_cr("if(nodesToTypes[node] != null && !contains(types,nodesToTypes[node][i])){");
		pWriter.addJS_cr("if(div.style.display == ''){");
		pWriter.addJS_cr("div.style.display='none';");
		pWriter.addJS_cr("tab = nodesToTabs[node]");
		pWriter.addJS_cr("if(tab != null && tabsDisplayIndicators[tab] != null){");
		pWriter.addJS_cr("tabsDisplayIndicators[tab]--;");
		pWriter.addJS_cr("if(tabsDisplayIndicators[tab] == 0){");
		pWriter.addJS_cr("EBX_Form.hideTab(tab);");
		pWriter.addJS_cr("}}}}else{");
		pWriter.addJS_cr("if(div.style.display=='none'){");
		pWriter.addJS_cr("div.style.display='';");
		pWriter.addJS_cr("tab = nodesToTabs[node]");
		pWriter.addJS_cr("if(tab != null && tabsDisplayIndicators[tab] != null){");
		pWriter.addJS_cr("tabsDisplayIndicators[tab]++;");
		pWriter.addJS_cr("if(tabsDisplayIndicators[tab] == 1){");
		pWriter.addJS_cr("EBX_Form.showTab(tab);");
		pWriter.addJS_cr("}}}");
		pWriter.addJS_cr("break;");
		pWriter.addJS_cr("}}}");
		pWriter.addJS_cr("for(var tab in tabsToTypes){");
		pWriter.addJS_cr("for(var i=0; i< tabsToTypes[tab].length; i++){");
		pWriter.addJS_cr("if(tabsToTypes[tab] != null && !contains(types,tabsToTypes[tab][i])){");
		pWriter.addJS_cr("EBX_Form.hideTab(tab);");
		pWriter.addJS_cr("}else{");
		pWriter.addJS_cr("EBX_Form.showTab(tab);");
		pWriter.addJS_cr("break;");
		pWriter.addJS_cr("}}}}");
	}

	private void addJsNodesToTypesMap(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(UIUtils.getCurrentAdaptation(pContext), pContext.getCurrentTable().getTableOccurrenceRootNode(), null, pContext.getSession());

		pWriter.addJS_cr("var nodesToTypes = {};");
		for (SchemaNode node : nodes) {
			List<String> types = this.typesMap.get(node.getPathInAdaptation().format());
			if (types != null && !types.isEmpty()) {
				pWriter.addJS("nodesToTypes['" + UIUtils.getUniqueWebIdentifierForNode(node) + "'] = [");
				for (String type : types) {
					pWriter.addJS("'" + type + "',");
				}
				pWriter.addJS_cr("];");
			}
		}
	}

	private void addJsTabsRelatedMaps(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		pWriter.addJS_cr("var tabsDisplayIndicators = {};");
		pWriter.addJS_cr("var nodesToTabs = {};");
		pWriter.addJS_cr("var tabsToTypes = {};");
		for (SchemaNode tab : this.tabs) {
			List<SchemaNode> nodesInTab = SchemaUtils.getVisibleTerminalNodes(tab, pContext.getSession(), pContext.getCurrentRecord());
			for (SchemaNode node : nodesInTab) {
				pWriter.addJS("nodesToTabs['" + UIUtils.getUniqueWebIdentifierForNode(node) + "'] = '" + UIUtils.getUniqueWebIdentifierForNode(tab) + "';");
			}

			if (!this.hasAlwaysVisibleNodes(tab, pContext.getSession())) {
				pWriter.addJS("tabsDisplayIndicators['" + UIUtils.getUniqueWebIdentifierForNode(tab) + "'] = " + nodesInTab.size() + ";");
			}

			List<String> types = this.typesMap.get(tab.getPathInAdaptation().format());
			if (types != null && !types.isEmpty()) {
				pWriter.addJS("tabsToTypes['" + UIUtils.getUniqueWebIdentifierForNode(tab) + "'] = [");
				for (String type : types) {
					pWriter.addJS("'" + type + "',");
				}
				pWriter.addJS_cr("];");
			}
		}
	}

	private void addTab(final SchemaNode tabNode, final UIFormPaneWithTabs uiFormPaneWithTabs, final UIFormContext pContext) {
		UIFormLabelSpec label = new UIFormLabelSpec(tabNode.getLabel(pContext.getLocale()));
		UIFormPane pane = new UIFormPaneForNode(tabNode);
		Tab tab = new Tab(label, pane);
		tab.setId(UIUtils.getUniqueWebIdentifierForNode(tabNode));
		uiFormPaneWithTabs.addTab(tab);
	}

	@Override
	public void defineBody(final UIFormBody pBody, final UIFormContext pContext) {

		this.typesMap = this.getTypesMap(pContext);
		this.tabs = SchemaUtils.getTabs(pContext.getCurrentTable().getTableOccurrenceRootNode());

		if (this.tabs.isEmpty()) {
			pBody.setContent(new UIFormPaneGeneral());
			return;
		}

		UIFormPaneWithTabs uiFormPaneWithTabs = new UIFormPaneWithTabs();
		uiFormPaneWithTabs.addHomeTab(new UIFormPaneGeneral());

		for (SchemaNode tabNode : this.tabs) {
			this.addTab(tabNode, uiFormPaneWithTabs, pContext);
		}

		pBody.setContent(uiFormPaneWithTabs);
	}

	private Map<String, List<String>> getTypesMap(final UIFormContext pContext) {
		Map<String, List<String>> map = new HashMap<>();
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(UIUtils.getCurrentAdaptation(pContext), pContext.getCurrentTable().getTableOccurrenceRootNode(), null, pContext.getSession());
		if (this.isTypesConfiguredInModel()) {
			for (SchemaNode node : nodes) {
				List<String> types = getTypesForNode(node);
				if (!types.isEmpty()) {
					map.put(node.getPathInAdaptation().format(), types);
				}
			}
		} else {
			SchemaNode typeConfiguratorNode = pContext.getCurrentTable().getTableOccurrenceRootNode().getNode(Path.SELF.add(this.pathToTypeConfigurator));
			AdaptationTable table = typeConfiguratorNode.getFacetOnTableReference().getTable(pContext.getValueContext());
			for (Adaptation record : table.selectOccurrences(null)) {
				for (Object path : record.getList(this.pathToCollectionInConfigurator)) {
					SchemaNode node = pContext.getCurrentTable().getTableOccurrenceRootNode().getNode(Path.SELF.add(Path.parse((String) path)));
					if (node.getInformation() != null && !StringUtils.isBlank(this.ignoreFieldsWithInformationEquals)
							&& this.ignoreFieldsWithInformationEquals.equals(node.getInformation().getInformation())) {
						continue;
					}
					List<String> types = map.get(node.getPathInAdaptation().format());
					if (types == null) {
						types = new ArrayList<>();
					}
					types.add(record.getOccurrencePrimaryKey().format());
					map.put(node.getPathInAdaptation().format(), types);
				}
			}
		}
		return map;
	}

	/**
	 * Gets categories of a given node This method is used by DynamicFormBasedOnType and DynamicAccessRuleBasedOnType
	 *
	 * The category of an attribute is expected to be a succession of token separated by ';'. Each token is composed as the type and the associated mandatory boolean separated by a ','. Example :
	 * catA,true;catB,false --&gt; the node belongs to 2 categories typeA and typeB.
	 *
	 *
	 * @param pNode the node
	 *
	 * @return a list of types that can be empty
	 */
	public static List<String> getTypesForNode(final SchemaNode pNode) {
		List<String> types = new ArrayList<>();
		SchemaNodeInformation nodeInfo = pNode.getInformation();

		if (nodeInfo == null) {
			return types;
		}
		String information = nodeInfo.getInformation();
		if (information == null) {
			return types;
		}
		String[] tokens = information.split(";");
		for (String token : tokens) {
			types.add(token.split(",")[0]);
		}
		return types;
	}

	private boolean isTypesConfiguredInModel() {
		return this.pathToTypeConfigurator == null || this.pathToCollectionInConfigurator == null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.orchestranetworks.ui.form.UIForm#defineBottomBar(com.orchestranetworks .ui.form.UIFormBottomBar, com.orchestranetworks.ui.form.UIFormContext)
	 */
	@Override
	public void defineBottomBar(final UIFormBottomBar uiFormBottomBar, final UIFormContext uifFormContext) {
		uiFormBottomBar.setSubmitAndCloseButtonDisplayable(true);
	}

	private List<String> getTypes(final ValueContext pValueContext) {
		List<String> types = new ArrayList<>();
		if (this.isTypesConfiguredInModel()) {
			for (SchemaNode node : this.typesAsListsOfStrings) {
				types.add((String) pValueContext.getValue(Path.SELF.add(node.getPathInAdaptation())));
			}
			for (SchemaNode node : this.typesAsBoolean) {
				if ((Boolean) pValueContext.getValue(Path.SELF.add(node.getPathInAdaptation()))) {
					types.add(node.getPathInAdaptation().getLastStep().format());
				}
			}
		} else {
			pValueContext.getValue(Path.SELF.add(this.pathToTypeConfigurator));
		}
		return types;
	}

	private boolean hasAlwaysVisibleNodes(final SchemaNode pRootNode, final Session pSession) {
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pRootNode, pSession, null);
		for (SchemaNode node : nodes) {
			if (this.typesMap.get(node.getPathInAdaptation().format()).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void resolveTypesNodes(final UIFormContext pContext) {
		String[] pathsToTypes = this.pathToTypes.split(";");
		for (String pathToType : pathsToTypes) {
			SchemaNode typeNode = pContext.getCurrentTable().getTableOccurrenceRootNode().getNode(Path.SELF.add(Path.parse(pathToType)));

			if (SchemaTypeName.XS_STRING.equals(typeNode.getXsTypeName()) && typeNode.getMaxOccurs() == 1) {
				this.typesAsListsOfStrings.add(typeNode);
				continue;
			}

			if (SchemaTypeName.XS_BOOLEAN.equals(typeNode.getXsTypeName()) && typeNode.getMaxOccurs() == 1) {
				this.typesAsBoolean.add(typeNode);
				continue;
			}

			SchemaNode[] nodes = typeNode.getNodeChildren();
			if (nodes.length == 0) {
				throw new IllegalStateException(typeNode.getPathInAdaptation().format() + " : " + ILLEGAL_STATE_MESSAGE);
			}

			for (SchemaNode node : nodes) {
				if (!node.isTerminalValueDescendant() || !SchemaTypeName.XS_BOOLEAN.equals(node.getXsTypeName())) {
					throw new IllegalStateException(typeNode.getPathInAdaptation().format() + " : " + ILLEGAL_STATE_MESSAGE);
				}

				this.typesAsBoolean.add(node);
			}
		}
	}

	public void setPathToTypes(final String pathToTypes) {
		this.pathToTypes = pathToTypes;
	}

	@Override
	public void validateForm(final UIFormRequestContext pContext) {
		List<String> types = this.getTypes(pContext.getValueContext(Path.SELF));
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(UIUtils.getCurrentAdaptation(pContext), pContext.getTableNode(), null, pContext.getSession());
		for (SchemaNode node : nodes) {
			List<String> nodeTypes = this.typesMap.get(node.getPathInAdaptation().format());
			if (nodeTypes == null) {
				continue;
			}
			int size = nodeTypes.size();
			nodeTypes.removeAll(types);
			if (size == 0 || nodeTypes.size() < size) {
				if (SchemaUtils.isNodeMandatory(node, types) && pContext.getValueContext(Path.SELF.add(node.getPathInAdaptation())).getValue() == null) {
					pContext.getValueContext(Path.SELF.add(node.getPathInAdaptation())).addError("Field " + node.getLabel(pContext.getSession().getLocale()) + "' is mandatory.");
				}
				continue;
			}
			pContext.getValueContext(Path.SELF.add(node.getPathInAdaptation())).setNewValue(null);
		}
		super.validateForm(pContext);
	}

	private void writePane(final SchemaNode pRootNode, final Adaptation pAdaptation, final UIFormPaneWriter pWriter) {

		if (pRootNode.isAssociationNode()) {
			UIFormRow row = pWriter.newFormRow();
			row.setLabelDisplayed(false);
			pWriter.startFormRow(row);
			pWriter.addWidget(pWriter.newTable(pRootNode.getPathInAdaptation()));
			pWriter.endFormRow();
		}
		List<String> types = null;
		if (pAdaptation.isTableOccurrence()) {
			types = this.getTypes(pAdaptation.createValueContext());
		} else {
			types = new ArrayList<>();
		}
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pAdaptation, pRootNode, this.tabs, pWriter.getSession());
		pWriter.startTableFormRow();
		for (SchemaNode aNode : nodes) {
			DynamicForm.addFormRow(aNode, pWriter, pWriter.getLocale(), types);
		}
	}
}
