/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.interactions.SessionInteraction;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.selection.DatasetEntitySelection;
import com.orchestranetworks.ui.selection.EntitySelection;
import com.orchestranetworks.ui.selection.RecordEntitySelection;
import com.orchestranetworks.userservice.declaration.ActivationContext;
import com.orchestranetworks.userservice.declaration.UserServiceDeclaration;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;
import com.tibco.ebx.cs.commons.lib.message.Messages;

/**
 * An implementation of {@link ServicePermissionRule} providing advanced rules.<br>
 * The rules are all checked according to the {@link EntitySelection} and compared to keep the most restrictive.<br>
 * <br>
 * To be used on {@link ActivationContext#setPermissionRule(ServicePermissionRule)} of {@link UserServiceDeclaration#defineActivation(ActivationContext)}.
 *
 * @author Aurélien Ticot
 * @param <S> the {@link EntitySelection} type.
 * @since 1.8.0
 */
public class AdvancedServicePermissionRules<S extends EntitySelection> implements ServicePermissionRule<S> {
	private boolean hideInWorkflow = true;
	private boolean hideOutsideWorkflow = false;
	private boolean showOnlyOnChildDataset = false;
	private boolean hideOnHistory = true;
	private boolean hideExceptOnHistory = false;
	private final HashMap<Path, Object> hideForFieldValue = new HashMap<>();
	private final HashMap<Path, Object> showOnlyForFieldValue = new HashMap<>();

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<S> pContext) {
		UserServicePermission permission = UserServicePermission.getEnabled();

		UserServicePermission historyPermission = this.getHistoryPermission(pContext);
		permission = permission.min(historyPermission);

		UserServicePermission workflowPermission = this.getWorkflowPermission(pContext);
		permission = permission.min(workflowPermission);

		UserServicePermission childDatasetPermission = this.getChildDatasetPermission(pContext);
		permission = permission.min(childDatasetPermission);

		if (!this.hideForFieldValue.isEmpty()) {
			UserServicePermission hideFieldValuePermission = this.getHideFieldValuePermission(pContext);
			permission = permission.min(hideFieldValuePermission);
		}

		if (!this.showOnlyForFieldValue.isEmpty()) {
			UserServicePermission showFieldValuePermission = this.getShowFieldValuePermission(pContext);
			permission = permission.min(showFieldValuePermission);
		}

		return permission;
	}

	/**
	 * Define the service to be shown only on an history table. Only evaluated for {@link DatasetEntitySelection DatasetEntitySelection or inheriting types}. Default is false.
	 *
	 * @param pHideExceptOnHistory true to hide the service except for history.
	 * @since 1.8.0
	 */
	public void hideExceptOnHistory(final boolean pHideExceptOnHistory) {
		this.hideExceptOnHistory = pHideExceptOnHistory;
	}

	/**
	 * Define the service to be hidden when a given field match a given value. This method can be called several times, each path/value will be evaluated with a OR operator. Only evaluated for
	 * {@link RecordEntitySelection} type.
	 *
	 * @param pValue to value to check.
	 * @param pPath  the path of the field to check.
	 * @throws NullPointerException if the path argument is null.
	 * @since 1.8.0
	 */
	public void hideForFieldValue(final Object pValue, final Path pPath) throws NullPointerException {
		Objects.requireNonNull(pPath, "The path argument shall not be null");
		this.hideForFieldValue.put(pPath, pValue);
	}

	/**
	 * Define the service to be hidden in the context of a workflow. Evaluated whatever the {@link EntitySelection} type.<br>
	 * Default is true.
	 *
	 * @param pHideInWorkflow true to hide the service in workflow.
	 * @since 1.8.0
	 */
	public void hideInWorkflow(final boolean pHideInWorkflow) {
		this.hideInWorkflow = pHideInWorkflow;
	}

	/**
	 * Define the service to be hidden on an history table. Only evaluated for {@link DatasetEntitySelection} or inheriting types.<br>
	 * Default is true.
	 *
	 * @param pHideOnHistory true to hide the service on the history.
	 * @since 1.8.0
	 */
	public void hideOnHistory(final boolean pHideOnHistory) {
		this.hideOnHistory = pHideOnHistory;
	}

	/**
	 * Define the service to be hidden when not in a context of a workflow. Evaluated whatever the {@link EntitySelection} type.<br>
	 * Default is false.
	 *
	 * @param pHideOutsideWorkflow true to hide the service outside of workflow.
	 * @since 1.8.0
	 */
	public void hideOutsideWorkflow(final boolean pHideOutsideWorkflow) {
		this.hideOutsideWorkflow = pHideOutsideWorkflow;
	}

	/**
	 * Define the service to be shown only when a given field match a given value. This method can be called several times, each path/value will be evaluated with a OR operator. Only evaluated for
	 * {@link RecordEntitySelection} type.
	 *
	 * @param pValue to value to check.
	 * @param pPath  the path of the field to check.
	 * @throws NullPointerException if the path argument is null.
	 * @since 1.8.0
	 */
	public void showOnlyForFieldValue(final Object pValue, final Path pPath) throws NullPointerException {
		Objects.requireNonNull(pPath, "The path argument shall not be null");
		this.showOnlyForFieldValue.put(pPath, pValue);
	}

	/**
	 * Define the service to be shown only on child dataset, ie hidden on root dataset. Only evaluated for {@link DatasetEntitySelection} or inheriting types.<br>
	 * Default is true.
	 *
	 * @param pShowOnlyOnChildDataset true to hide on root dataset and show on child dataset.
	 * @since 1.8.0
	 */
	public void showOnlyOnChildDataset(final boolean pShowOnlyOnChildDataset) {
		this.showOnlyOnChildDataset = pShowOnlyOnChildDataset;
	}

	private UserServicePermission getChildDatasetPermission(final ServicePermissionRuleContext<S> pContext) {
		S s = pContext.getEntitySelection();

		if (s instanceof DatasetEntitySelection) {
			DatasetEntitySelection entitySelection = (DatasetEntitySelection) pContext.getEntitySelection();

			Adaptation dataset = entitySelection.getDataset();
			if (dataset.isRootAdaptation() && this.showOnlyOnChildDataset) {
				UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceNotAvailableInRootDataset");
				return UserServicePermission.getDisabled(reason);
			}
		}

		return UserServicePermission.getEnabled();
	}

	private UserServicePermission getHideFieldValuePermission(final ServicePermissionRuleContext<S> pContext) {
		S s = pContext.getEntitySelection();

		if (s instanceof RecordEntitySelection) {
			RecordEntitySelection entitySelection = (RecordEntitySelection) pContext.getEntitySelection();

			UserServicePermission permission = UserServicePermission.getEnabled();

			Adaptation record = entitySelection.getRecord();
			if (record == null) {
				return permission;
			}

			Iterator<Entry<Path, Object>> iterator = this.hideForFieldValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Path, Object> fieldValue = iterator.next();
				Path path = fieldValue.getKey();
				Object value = fieldValue.getValue();

				Object recordValue = record.get(path);

				if (value == null && recordValue == null || value != null && value.equals(recordValue)) {
					UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceNotAvailableForValue{0}OnField{1}", value, path);
					permission = permission.min(UserServicePermission.getDisabled(reason));
				}
			}
			return permission;
		}

		return UserServicePermission.getEnabled();
	}

	private UserServicePermission getHistoryPermission(final ServicePermissionRuleContext<S> pContext) {
		S s = pContext.getEntitySelection();

		if (s instanceof DatasetEntitySelection) {
			DatasetEntitySelection entitySelection = (DatasetEntitySelection) pContext.getEntitySelection();

			Adaptation dataset = entitySelection.getDataset();

			if (dataset.isHistory() && this.hideOnHistory) {
				UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceNotAvailableOnHistory");
				return UserServicePermission.getDisabled(reason);
			}

			if (!dataset.isHistory() && this.hideExceptOnHistory) {
				UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceOnlyAvailableOnHistory");
				return UserServicePermission.getDisabled(reason);
			}
		}

		return UserServicePermission.getEnabled();
	}

	private UserServicePermission getShowFieldValuePermission(final ServicePermissionRuleContext<S> pContext) {
		S s = pContext.getEntitySelection();

		if (s instanceof RecordEntitySelection) {
			RecordEntitySelection entitySelection = (RecordEntitySelection) pContext.getEntitySelection();

			UserServicePermission permission = UserServicePermission.getDisabled();

			Adaptation record = entitySelection.getRecord();
			if (record == null) {
				return permission;
			}

			Iterator<Entry<Path, Object>> iterator = this.showOnlyForFieldValue.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<Path, Object> fieldValue = iterator.next();
				Path path = fieldValue.getKey();
				Object value = fieldValue.getValue();

				Object recordValue = record.get(path);

				if (value == null && recordValue == null || value != null && value.equals(recordValue)) {
					permission = permission.max(UserServicePermission.getEnabled());
				}
			}

			return permission;
		}

		return UserServicePermission.getEnabled();
	}

	private UserServicePermission getWorkflowPermission(final ServicePermissionRuleContext<S> pContext) {
		Session session = pContext.getSession();
		SessionInteraction sessionInteraction = session.getInteraction(true);

		if (sessionInteraction != null && this.hideInWorkflow) {
			UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceNotAvailableInWorkflow");
			return UserServicePermission.getDisabled(reason);
		}

		if (sessionInteraction == null && this.hideOutsideWorkflow) {
			UserMessage reason = Messages.getError(AdvancedServicePermissionRules.class, "ServiceOnlyAvailableInWorkflow");
			return UserServicePermission.getDisabled(reason);
		}

		return UserServicePermission.getEnabled();
	}
}
