/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.interactions.InteractionHelper.ParametersMap;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.schema.info.SchemaNodeInformation;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.UIButtonSpec;
import com.orchestranetworks.ui.UIButtonSpecNavigation;
import com.orchestranetworks.ui.UIButtonSpecSubmit;
import com.orchestranetworks.ui.UIFormLabelSpec;
import com.orchestranetworks.ui.UIFormLabelSpec.DocumentationPane;
import com.orchestranetworks.ui.form.UIFormRow;
import com.orchestranetworks.ui.selection.RecordEntitySelection;
import com.orchestranetworks.ui.toolbar.Toolbar;
import com.orchestranetworks.userservice.ObjectKey;
import com.orchestranetworks.userservice.UserServiceDisplayConfigurator;
import com.orchestranetworks.userservice.UserServiceEventContext;
import com.orchestranetworks.userservice.UserServiceEventOutcome;
import com.orchestranetworks.userservice.UserServiceGetCreatedRecordContext;
import com.orchestranetworks.userservice.UserServiceNext;
import com.orchestranetworks.userservice.UserServiceObjectContext;
import com.orchestranetworks.userservice.UserServiceObjectContextBuilder;
import com.orchestranetworks.userservice.UserServiceObjectContextForInputValidation;
import com.orchestranetworks.userservice.UserServicePane;
import com.orchestranetworks.userservice.UserServicePaneContext;
import com.orchestranetworks.userservice.UserServicePaneWriter;
import com.orchestranetworks.userservice.UserServiceSetupDisplayContext;
import com.orchestranetworks.userservice.UserServiceSetupObjectContext;
import com.orchestranetworks.userservice.UserServiceTab;

import com.orchestranetworks.userservice.UserServiceTabbedPane;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotIdentifiedException;
import com.tibco.ebx.cs.commons.lib.exception.WorkflowConfigurationException;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;
import com.tibco.ebx.cs.commons.lib.utils.WorkflowUtils;
import com.tibco.ebx.cs.commons.lib.workflow.WorkflowConstants;
import com.tibco.ebx.cs.commons.ui.util.UIUtils;

/**
 * Dynamic service form operations
 * 
 * @author Mickaël Chevalier
 */
public class DynamicServiceFormOperations {

	private List<SchemaNode> tabs = new ArrayList<>();
	private Map<String, List<String>> typesMap;
	private Map<String, List<String>> mandatoryMap;

	private final List<SchemaNode> typesAsListsOfStrings = new ArrayList<>();
	private final List<SchemaNode> typesAsBoolean = new ArrayList<>();
	private final ObjectKey key = ObjectKey.forName("record");
	private final AdaptationTable table;
	private Adaptation record;
	private boolean isDuplicate;
	private UserServiceEventOutcome eventAfterDuplicate;
	private UserServiceEventOutcome eventOnRevert;
	private String javascriptOnClose;

	private Path pathToTypeConfigurator;
	private Path pathToCollectionInConfigurator;
	private String ignoreFieldsWithInformationEquals;
	private String pathToTypes;
	private boolean isRecordJustCreated = false;
	private boolean isRecordDuplicated = false;
	private String toolbarName;
	private Method toolbarBuilderMethod;

	/**
	 * Constructor
	 * 
	 * @param pTable       table
	 * @param pRecord      record
	 * @param pIsDuplicate is duplicate ?
	 */
	public DynamicServiceFormOperations(final AdaptationTable pTable, final Adaptation pRecord, final Boolean pIsDuplicate) {
		this.table = pTable;
		this.record = pRecord;
		this.isDuplicate = pIsDuplicate;
	}

	public void setJavascriptOnClose(final String javascriptOnClose) {
		this.javascriptOnClose = javascriptOnClose;
	}

	public String getIgnoreFieldsWithInformationEquals() {
		return this.ignoreFieldsWithInformationEquals;
	}

	public void setIgnoreFieldsWithInformationEquals(final String ignoreFieldsWithInformationEquals) {
		this.ignoreFieldsWithInformationEquals = ignoreFieldsWithInformationEquals;
	}

	public void setDuplicate(final boolean isDuplicate) {
		this.isDuplicate = isDuplicate;
	}

	public void setEventAfterDuplicate(final UserServiceEventOutcome eventAfterDuplicate) {
		this.eventAfterDuplicate = eventAfterDuplicate;
	}

	public void setEventOnRevert(final UserServiceEventOutcome eventOnRevert) {
		this.eventOnRevert = eventOnRevert;
	}

	public Path getPathToTypeConfigurator() {
		return this.pathToTypeConfigurator;
	}

	public void setPathToTypeConfigurator(final Path pathToTypeConfigurator) {
		this.pathToTypeConfigurator = pathToTypeConfigurator;
	}

	public Path getPathToCollectionInConfigurator() {
		return this.pathToCollectionInConfigurator;
	}

	public void setPathToCollectionInConfigurator(final Path pathToCollectionInConfigurator) {
		this.pathToCollectionInConfigurator = pathToCollectionInConfigurator;
	}

	public String getPathToTypes() {
		return this.pathToTypes;
	}

	public void setPathToTypes(final String pathToTypes) {
		this.pathToTypes = pathToTypes;
	}

	/**
	 * Process event outcome
	 * 
	 * @param pContext      context
	 * @param pEventOutcome event outcome
	 * @return UserServiceEventOutcome
	 */
	public UserServiceEventOutcome processEventOutcome(final UserServiceObjectContext pContext, final UserServiceEventOutcome pEventOutcome) {
		return pEventOutcome;
	}

	/**
	 * Setup display
	 * 
	 * @param pContext      UserServiceSetupDisplayContext
	 * @param pConfigurator UserServiceDisplayConfigurator
	 * @param pSession      sessionF
	 */
	public void setupDisplay(final UserServiceSetupDisplayContext<?> pContext, final UserServiceDisplayConfigurator pConfigurator, final Session pSession) {
		if (this.record != null) {
			pConfigurator.setTitle(this.record.getLabel(pSession.getLocale()));
		}
		UIButtonSpecSubmit saveButton = this.getSaveButton(pContext, pConfigurator);
		UIButtonSpecNavigation revertButton = this.getRevertButton(pConfigurator);
		UIButtonSpec saveCloseButton = this.getSaveAndCloseButton(pContext, pConfigurator);
		UIButtonSpec closeButton = this.getCloseButton(pContext, pConfigurator);

		try {
			if (WorkflowUtils.isInWorkflow(pSession)) {
				if (this.record == null || WorkflowUtils.isRecordCurrentlySubjectOfWorkflow(this.record, pSession)) {
					pConfigurator.setLeftButtons(saveButton, revertButton);
				}
			} else {
				pConfigurator.setLeftButtons(saveButton, saveCloseButton, revertButton, closeButton);
			}
		} catch (EBXResourceNotFoundException | WorkflowConfigurationException | EBXResourceNotIdentifiedException ex) {
			pConfigurator.setLeftButtons(saveButton, saveCloseButton, revertButton, closeButton);
			CommonsLogger.getLogger().error(ex.getMessage());
		}

		if (this.toolbarBuilderMethod != null) {
			try {
				if (!this.isDuplicate && this.record != null) {
					pConfigurator.setToolbar((Toolbar) this.toolbarBuilderMethod.invoke(null, this.record, pContext.getSession(), pConfigurator, false));
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new RuntimeException(ex);
			}
		} else if (!StringUtils.isBlank(this.toolbarName)) {
			if (this.record != null) {
				pConfigurator.setToolbar(this.toolbarName, this.record);
			} else {
				pConfigurator.setToolbar(this.toolbarName, this.table);
			}
		}

		this.initMaps(pContext);
		this.tabs = SchemaUtils.getTabs(pContext.getValueContext(this.key).getNode());

		if (this.tabs.isEmpty()) {
			pConfigurator.setContent(new UIFormPaneGeneral());
		} else {
			UserServiceTabbedPane rootPane = pConfigurator.newTabbedPane();
			UserServiceTab mainTab = rootPane.newTab(new UIFormPaneGeneral());
			rootPane.addTab(mainTab);
			for (SchemaNode tabNode : this.tabs) {
				if (tabNode.isAssociationNode() && SchemaUtils.isNodeVisible(tabNode, pSession, this.record) || !SchemaUtils.getVisibleNodes(tabNode, pSession, this.record).isEmpty()) {
					UserServiceTab tab = rootPane.newTab(tabNode.getLabel(pSession), new PaneForTab(tabNode));
					tab.setId(UIUtils.getUniqueWebIdentifierForNode(tabNode));
					rootPane.addTab(tab);
				}
			}

			pConfigurator.setContent(rootPane);
		}
	}

	private UIButtonSpec getCloseButton(final UserServiceSetupDisplayContext<?> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		UserMessage label = Messages.getInfo(this.getClass(), "DynamicServiceFormOperations.button.close");
		return pConfigurator.newActionButton(label, userServiceEventContext -> {
			if (this.record != null) {
				return UserServiceNext.nextService(ServiceKey.DEFAULT_SERVICE, this.record);
			} else {
				return UserServiceNext.nextClose();
			}
		});
	}

	private UIButtonSpec getSaveAndCloseButton(final UserServiceSetupDisplayContext<?> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		UIButtonSpec saveCloseButton = pConfigurator.newSaveButton(userServiceEventContext -> {
			ProcedureResult result = userServiceEventContext.save(DynamicServiceFormOperations.this.key);
			if (result.hasFailed()) {
				return null;
			} else {
				Optional<Adaptation> savedRecord = AdaptationUtils.getRecordForValueContext(pContext.getValueContext(DynamicServiceFormOperations.this.key));
				if (savedRecord.isPresent()) {
					this.record = savedRecord.get();
					DynamicServiceFormOperations.completeWorkItemIfCreatingOrDuplicatingInWorkflow(userServiceEventContext, this.record);
					return UserServiceNext.nextService(ServiceKey.DEFAULT_SERVICE, this.record);
				} else {
					return UserServiceNext.nextClose();
				}
			}
		});
		UserMessage label = Messages.getInfo(this.getClass(), "DynamicServiceFormOperations.button.save.and.close");
		saveCloseButton.setLabel(label);
		return saveCloseButton;
	}

	private UIButtonSpecNavigation getRevertButton(final UserServiceDisplayConfigurator pConfigurator) {
		UIButtonSpecNavigation revertButton = pConfigurator.newRevertButton();
		if (this.eventOnRevert != null) {
			UserMessage label = Messages.getInfo(this.getClass(), "DynamicServiceFormOperations.button.revert");
			revertButton = pConfigurator.newActionButton(label, userServiceEventContext -> {
				for (Entry<Path, Object> entry : INITIAL_VALUES.entrySet()) {
					userServiceEventContext.getValueContext(this.key, entry.getKey()).setNewValue(entry.getValue());
				}
				return this.eventOnRevert;
			});
		}
		revertButton.setAskBeforeLeavingModifiedForm(false);
		return revertButton;
	}

	private UIButtonSpecSubmit getSaveButton(final UserServiceSetupDisplayContext<?> pContext, final UserServiceDisplayConfigurator pConfigurator) {
		UIButtonSpecSubmit saveButton = pConfigurator.newSaveButton(userServiceEventContext -> {
			Optional<Adaptation> preexistingRecord = AdaptationUtils.getRecordForValueContext(pContext.getValueContext(DynamicServiceFormOperations.this.key));
			ProcedureResult result = userServiceEventContext.save(DynamicServiceFormOperations.this.key);
			if (!result.hasFailed()) {
				Optional<Adaptation> savedRecord = AdaptationUtils.getRecordForValueContext(pContext.getValueContext(DynamicServiceFormOperations.this.key));
				if (savedRecord.isPresent()) {
					if (!preexistingRecord.isPresent()) {
						this.isRecordJustCreated = true;
					}
					this.record = savedRecord.get();
					completeWorkItemIfCreatingOrDuplicatingInWorkflow(userServiceEventContext, this.record);
				}
				if (this.isDuplicate) {
					this.isDuplicate = false;
					this.isRecordDuplicated = true;
					return this.eventAfterDuplicate;
				}
			}
			return null;
		});
		saveButton.setDefaultButton(true);
		return saveButton;
	}

	private static void completeWorkItemIfCreatingOrDuplicatingInWorkflow(final UserServiceEventContext pContext, final Adaptation pRecord) {
		if (pContext.getServiceKey().equals(ServiceKey.CREATE) || pContext.getServiceKey().equals(ServiceKey.DUPLICATE)) {
			SessionInteraction interaction = pContext.getSession().getInteraction(true);
			if (interaction != null && !interaction.isComplete()) {
				ParametersMap internalParameters = interaction.getInternalParameters();
				if (internalParameters == null) {
					internalParameters = new ParametersMap();
				}
				internalParameters.setVariableString(WorkflowConstants.PARAM_CREATED, pRecord.toXPathExpression());
				interaction.setInternalParameters(internalParameters);
				interaction.complete(internalParameters);

			}
		}
	}

	public void setupObjectContext(final UserServiceSetupObjectContext<?> pContext, final UserServiceObjectContextBuilder pBuilder) {
		if (this.record != null) {
			if (this.isDuplicate) {
				pBuilder.registerNewDuplicatedRecord(this.key, this.record);
			} else {
				if (!pContext.isInitialDisplay()) {
					if (this.isRecordJustCreated || this.isRecordDuplicated) {
						this.isRecordJustCreated = false;
						return;
					}
				}

				pBuilder.registerRecordOrDataSet(this.key, this.record);
				this.isRecordJustCreated = false;
			}
		} else {
			pBuilder.registerNewRecord(this.key, this.table);
		}
	}



	/**
	 * Validation of the form
	 * 
	 * @param pContext UserServiceObjectContextForInputValidation
	 * @param pSession session
	 */
	public void validate(final UserServiceObjectContextForInputValidation pContext, final Session pSession) {
		List<String> types = this.getTypes(pContext.getValueContext(this.key));
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pContext.getValueContext(this.key).getNode(), pContext, this.key, this.tabs);
		for (SchemaNode node : nodes) {
			List<String> nodeTypes = this.typesMap.get(node.getPathInAdaptation().format());
			if (nodeTypes == null) {
				continue;
			}
			int size = nodeTypes.size();
			nodeTypes.removeAll(types);
			if (size == 0 || nodeTypes.size() < size) {
				if (SchemaUtils.isNodeMandatory(node, types) || this.mandatoryMap.get(node.getPathInAdaptation().format()).contains(types.get(0))
						&& pContext.getValueContext(this.key, Path.SELF.add(node.getPathInAdaptation())).getValue() == null) {
					pContext.getValueContext(this.key, Path.SELF.add(node.getPathInAdaptation())).addError("Field '" + node.getLabel(pSession.getLocale()) + "' is mandatory.");
				}
				continue;
			}
			pContext.getValueContext(this.key, Path.SELF.add(node.getPathInAdaptation())).setNewValue(null);
		}
	}

	/**
	 * UserServicePane for tab
	 * 
	 * @author Mickaël Chevalier
	 */
	public class PaneForTab implements UserServicePane {

		private final SchemaNode tabNode;

		/**
		 * Constructor
		 * 
		 * @param pTabNode SchemaNode
		 */
		public PaneForTab(final SchemaNode pTabNode) {
			this.tabNode = pTabNode;
		}

		@Override
		public void writePane(final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
			DynamicServiceFormOperations.this.writePane(this.tabNode, pContext, pWriter);
		}
	}

	private class UIFormPaneGeneral implements UserServicePane {
		@Override
		public void writePane(final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
			pWriter.setCurrentObject(DynamicServiceFormOperations.this.key);
			DynamicServiceFormOperations.this.addJavascript(pWriter, pContext);
			SchemaNode rootNode = pContext.getValueContext(DynamicServiceFormOperations.this.key).getNode();
			DynamicServiceFormOperations.this.writePane(rootNode, pContext, pWriter);
		}
	}

	private static final String ILLEGAL_STATE_MESSAGE = "Type should be either a string or a complex type composed by boolean fields";
	private static final Map<Path, Object> INITIAL_VALUES = new HashMap<>();

	/**
	 * Write pane
	 * 
	 * @param rootNode SchemaNode
	 * @param pContext UserServicePaneContext
	 * @param pWriter  UserServicePaneWriter
	 */
	public void writePane(final SchemaNode rootNode, final UserServicePaneContext pContext, final UserServicePaneWriter pWriter) {
		if (rootNode.isAssociationNode()) {
			pWriter.addWidget(Path.SELF.add(rootNode.getPathInAdaptation()));
			return;
		}
		List<String> types = this.getTypes(pContext.getValueContext(DynamicServiceFormOperations.this.key));

		pWriter.startTableFormRow();
		this.addRowsForNodeUnder(pContext.getValueContext(this.key).getNode(), pContext, pWriter, types);
		pWriter.endTableFormRow();
	}

	private void addRowsForNodeUnder(final SchemaNode pNode, final UserServicePaneContext pContext, final UserServicePaneWriter pWriter, final List<String> pTypes) {
		for (SchemaNode node : pNode.getNodeChildren()) {
			if (this.tabs.contains(node)) {
				continue;
			}
			if (node.isComplex() && !node.isTerminalValue()) {
				pWriter.startFormGroup(Path.SELF.add(node.getPathInAdaptation()));
				this.addRowsForNodeUnder(node, pContext, pWriter, pTypes);
				pWriter.endFormGroup();
			} else {
				if (node.isTerminalValue() && !pContext.getPermission(this.key, Path.SELF.add(node.getPathInAdaptation())).isHidden() && !node.getDefaultViewProperties().isHidden()) {
					DynamicServiceFormOperations.addFormRow(node, pWriter, pWriter.getLocale(), pTypes);
					Path path = Path.SELF.add(node.getPathInAdaptation());
					if (node.getAccessMode().isReadWrite()) {
						INITIAL_VALUES.put(path, pContext.getValueContext(this.key, path).getValue());
					}
				} else {
					this.addRowsForNodeUnder(node, pContext, pWriter, pTypes);
				}
			}
		}
	}

	private static void addFormRow(final SchemaNode pNode, final UserServicePaneWriter pWriter, final Locale locale, final List<String> pTypes) {
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

	private void addJavascript(final UserServicePaneWriter pWriter, final UserServicePaneContext pContext) {
		if (!this.isDynamicityActivated()) {
			return;
		}

		if (this.isTypesConfiguredInModel()) {
			this.resolveTypesNodes(pContext);
		}

		this.addJsFnToGetTypes(pWriter, pContext);

		this.addJsNodesToTypesMap(pWriter, pContext);

		this.addJsNodesToMandatoryMap(pWriter, pContext);

		this.addJsTabsRelatedMaps(pWriter, pContext);

		DynamicServiceFormOperations.addJsFnToRefreshForm(pWriter);

		DynamicServiceFormOperations.addJsFnContains(pWriter);

		pWriter.addJS_cr("refreshForm();");
	}

	private boolean isDynamicityActivated() {
		return !this.isTypesConfiguredInModel() || !StringUtils.isBlank(this.pathToTypes);
	}

	private static void addJsFnContains(final UserServicePaneWriter pWriter) {
		pWriter.addJS_cr("function contains(array, element){");
		pWriter.addJS_cr("for(var i=0; i< array.length; i++){");
		pWriter.addJS_cr("if(array[i] === element){");
		pWriter.addJS_cr("return true;");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr("}");
		pWriter.addJS_cr("return false;");
		pWriter.addJS_cr("}");
	}

	private void addJsFnToGetTypes(final UserServicePaneWriter pWriter, final UserServicePaneContext pContext) {
		pWriter.addJS_cr("function getTypes(){");
		pWriter.addJS_cr("var types = [];");
		pWriter.addJS_cr("var count = 0;");

		if (this.isTypesConfiguredInModel()) {
			for (SchemaNode node : this.typesAsListsOfStrings) {
				if (pContext.getPermission(this.key, Path.SELF.add(node.getPathInAdaptation())).isHidden()) {
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
				if (pContext.getPermission(this.key, node.getPathInAdaptation()).isHidden()) {
					continue;
				}
				String name = node.getPathInSchema().getLastStep().format();
				if (pContext.getPermission(this.key, node.getPathInAdaptation()).isReadOnly()) {
					if ((Boolean) pContext.getValueContext(this.key).getValue(node.getPathInAdaptation())) {
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

	private boolean isTypesConfiguredInModel() {
		return this.pathToTypeConfigurator == null || this.pathToCollectionInConfigurator == null;
	}

	private static void addJsFnToRefreshForm(final UserServicePaneWriter pWriter) {
		pWriter.addJS_cr("function refreshForm(){");
		pWriter.addJS_cr("var types = getTypes();");
		pWriter.addJS_cr("for(var node in nodesToTypes){");
		pWriter.addJS_cr("for(var i=0; i<= nodesToTypes[node].length; i++){");
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
		pWriter.addJS_cr("if(nodesToMandatory[node]  != null && nodesToMandatory[node].filter(value => types.includes(value)).length > 0){");
		pWriter.addJS_cr("ebx_form_setMandatoryIndicator(prefixedPaths[node],true);");
		pWriter.addJS_cr("}else{");
		pWriter.addJS_cr("ebx_form_setMandatoryIndicator(prefixedPaths[node],false);");
		pWriter.addJS_cr("}");
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

	private void addJsNodesToTypesMap(final UserServicePaneWriter pWriter, final UserServicePaneContext pContext) {
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pContext.getValueContext(this.key).getNode(), pContext, this.key, this.tabs);

		pWriter.addJS_cr("var nodesToTypes = {};");
		for (SchemaNode node : nodes) {
			if (this.isNodeIgnored(node)) {
				continue;
			}

			List<String> types = this.typesMap.get(node.getPathInAdaptation().format());
			if (types == null) {
				types = new ArrayList<>();
			}
			if (types != null && (!this.isTypesConfiguredInModel() || !types.isEmpty())) {
				pWriter.addJS("nodesToTypes['" + UIUtils.getUniqueWebIdentifierForNode(node) + "'] = [");
				StringBuilder str = new StringBuilder();
				for (String type : types) {
					str.append("'" + type + "',");
				}
				if (str.length() > 0) {
					str.deleteCharAt(str.length() - 1);
					pWriter.addJS(str.toString());
				}
				pWriter.addJS_cr("];");
			}
		}
	}

	private void addJsNodesToMandatoryMap(final UserServicePaneWriter pWriter, final UserServicePaneContext pContext) {
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pContext.getValueContext(this.key).getNode(), pContext, this.key, this.tabs);

		pWriter.addJS_cr("var nodesToMandatory = {};");
		pWriter.addJS_cr("var prefixedPaths = {};");

		for (SchemaNode node : nodes) {
			if (this.isNodeIgnored(node)) {
				continue;
			}

			List<String> types = this.mandatoryMap.get(node.getPathInAdaptation().format());
			if (types == null) {
				types = new ArrayList<>();
			}
			if (types != null && (!this.isTypesConfiguredInModel() || !types.isEmpty())) {
				pWriter.addJS_cr("prefixedPaths['" + UIUtils.getUniqueWebIdentifierForNode(node) + "'] = '" + pWriter.getPrefixedPath(Path.SELF.add(node.getPathInAdaptation())).format() + "';");
				pWriter.addJS("nodesToMandatory['" + UIUtils.getUniqueWebIdentifierForNode(node) + "'] = [");
				StringBuilder str = new StringBuilder();
				for (String type : types) {
					str.append("'" + type + "',");
				}
				if (str.length() > 0) {
					str.deleteCharAt(str.length() - 1);
					pWriter.addJS(str.toString());
				}
				pWriter.addJS_cr("];");
			}
		}
	}

	private void addJsTabsRelatedMaps(final UserServicePaneWriter pWriter, final UserServicePaneContext pContext) {
		pWriter.addJS_cr("var tabsDisplayIndicators = {};");
		pWriter.addJS_cr("var nodesToTabs = {};");
		pWriter.addJS_cr("var tabsToTypes = {};");
		for (SchemaNode tab : this.tabs) {
			List<SchemaNode> nodesInTab = SchemaUtils.getVisibleTerminalNodes(tab, pContext.getSession(), this.record);
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

	private void initMaps(final UserServiceObjectContext pContext) {
		this.typesMap = new HashMap<>();
		this.mandatoryMap = new HashMap<>();
		INITIAL_VALUES.clear();
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pContext.getValueContext(this.key).getNode(), pContext, this.key, this.tabs);
		if (this.isTypesConfiguredInModel()) {
			for (SchemaNode node : nodes) {
				List<String> types = getTypesForNode(node);
				if (!types.isEmpty()) {
					this.typesMap.put(node.getPathInAdaptation().format(), types);
				}
			}
		} else {
			SchemaNode typeConfiguratorNode = pContext.getValueContext(this.key).getNode(Path.SELF.add(this.pathToTypeConfigurator));
			AdaptationTable table = typeConfiguratorNode.getFacetOnTableReference().getTable(pContext.getValueContext(this.key));
			for (Adaptation record : table.selectOccurrences(null)) {
				for (Object object : record.getList(this.pathToCollectionInConfigurator)) {
					DynamicAttribute attribute = (DynamicAttribute) object;
					SchemaNode node = pContext.getValueContext(this.key).getNode(Path.SELF.add(Path.parse(attribute.getField())));
					if (node == null) {
						continue;
					}
					if (this.isNodeIgnored(node)) {
						continue;
					}
					List<String> types = this.typesMap.get(node.getPathInAdaptation().format());
					if (types == null) {
						types = new ArrayList<>();
					}
					types.add(record.getOccurrencePrimaryKey().format());

					List<String> mandatory = this.mandatoryMap.get(node.getPathInAdaptation().format());
					if (mandatory == null) {
						mandatory = new ArrayList<>();
					}
					if (attribute.getMandatory().booleanValue()) {
						mandatory.add(record.getOccurrencePrimaryKey().format());
					}

					this.typesMap.put(node.getPathInAdaptation().format(), types);
					this.mandatoryMap.put(node.getPathInAdaptation().format(), mandatory);
				}
			}
		}
	}

	private boolean isNodeIgnored(final SchemaNode node) {
		return node.getInformation() != null && !StringUtils.isBlank(this.ignoreFieldsWithInformationEquals) && this.ignoreFieldsWithInformationEquals.equals(node.getInformation().getInformation());
	}

	/**
	 * Gets categories of a given node This method is used by DynamicFormBasedOnType and DynamicAccessRuleBasedOnType
	 *
	 * The category of an attribute is expected to be a succession of token separated by ';'. Each token is composed as the type and the associated mandatory boolean separated by a ','. Example :
	 *  catA,true;catB,false -> the node belongs to 2 categories typeA and typeB.
	 *
	 * @see DynamicAccessRuleBasedOnTypes
	 * @see DynamicForm
	 *
	 * @author MCH ======= catA,true;catB,false -&gt; the node belongs to 2 categories typeA and typeB.
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
			types.add((String) pValueContext.getValue(Path.SELF.add(this.pathToTypeConfigurator)));
		}
		return types;
	}

	private boolean hasAlwaysVisibleNodes(final SchemaNode pRootNode, final Session pSession) {
		List<SchemaNode> nodes = SchemaUtils.getVisibleTerminalNodes(pRootNode, pSession, this.record);
		for (SchemaNode node : nodes) {
			if (this.typesMap.get(node.getPathInAdaptation().format()).isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void resolveTypesNodes(final UserServicePaneContext pContext) {
		String[] pathsToTypes = this.pathToTypes.split(";");
		for (String pathToType : pathsToTypes) {
			SchemaNode typeNode = pContext.getValueContext(this.key).getNode(Path.SELF.add(Path.parse(pathToType)));

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

	public void setToolbarName(final String pName) {
		this.toolbarName = pName;

	}

	public void setToolbarBuilderMethod(final Method pMethod) {
		this.toolbarBuilderMethod = pMethod;

	}
	
	public Adaptation getCreatedRecord(UserServiceGetCreatedRecordContext<RecordEntitySelection> aContext) {
		return aContext.getCreatedRecord(this.key);
	}
}
