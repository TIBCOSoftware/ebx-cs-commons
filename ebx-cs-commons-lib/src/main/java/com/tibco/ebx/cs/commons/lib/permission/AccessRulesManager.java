/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaExtensionsContext;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.ServiceKey;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.selection.RecordEntitySelection;
import com.orchestranetworks.userservice.permission.ServicePermissionRule;
import com.orchestranetworks.userservice.permission.ServicePermissionRuleContext;
import com.orchestranetworks.userservice.permission.UserServicePermission;

/**
 * 
 *
 * Allows to set many access rules on the same nodes and on the same occurrences.
 *
 * This access rule must be set to all the nodes and the occurrences or at least on those which are subject to one of the rules set in this manager.
 *
 * If many rules are applied on the same node or on the same occurrences, the more restrictive one is applied.
 * 
 * @author Mickaël Chevalier
 */
public class AccessRulesManager implements AccessRule, ServicePermissionRule<RecordEntitySelection> {

	/** The rules on nodes. */
	private final Map<Path, List<AccessRule>> rulesOnNodes = new HashMap<>();

	/** The rules on occurrences. */
	private final Map<Path, List<AccessRule>> rulesOnOccurrences = new HashMap<>();

	/** The rules on services. */
	private final Map<Path, List<ServicePermissionRule<RecordEntitySelection>>> rulesOnServices = new HashMap<>();

	/** The context of the schema extension */
	private final SchemaExtensionsContext context;

	/** if restrictive, the more restricting access right will be applied */
	private final boolean restrictive;

	/**
	 * Instantiates a new access rules manager.
	 *
	 * @param pContext     the context of the schema extension
	 * @param pRestrictive if restrictive, the more restricting access right will be applied
	 */
	public AccessRulesManager(final SchemaExtensionsContext pContext, final boolean pRestrictive) {
		this.context = pContext;
		this.restrictive = pRestrictive;
	}

	/**
	 * Adds the rule to the list of rules associated with the path. Create a new list if no one already exist.
	 *
	 * @param pPath     the path of the node
	 * @param pRule     to add the rule
	 * @param pRulesMap the rules map
	 */
	private static void addRuleToMap(final Path pPath, final AccessRule pRule, final Map<Path, List<AccessRule>> pRulesMap) {
		List<AccessRule> rules = pRulesMap.get(pPath);
		if (rules != null) {
			rules.add(pRule);
		} else {
			rules = new ArrayList<>();
			rules.add(pRule);
			pRulesMap.put(pPath, rules);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.orchestranetworks.service.AccessRule#getPermission(com.onwbp.adaptation .Adaptation, com.orchestranetworks.service.Session, com.orchestranetworks.schema.SchemaNode)
	 */
	@Override
	public AccessPermission getPermission(final Adaptation pAdaptation, final Session pSession, final SchemaNode pNode) {
		List<AccessRule> rules = null;
		if (pNode.getPathInAdaptation().equals(Path.ROOT) && pAdaptation.isTableOccurrence()) {
			rules = this.rulesOnOccurrences.get(pNode.getPathInSchema());
		} else {
			rules = this.rulesOnNodes.get(pNode.getPathInSchema());
		}
		AccessPermission permission = AccessPermission.getReadWrite();
		if (rules != null) {
			for (AccessRule aRule : rules) {
				if (this.restrictive) {
					permission = permission.min(aRule.getPermission(pAdaptation, pSession, pNode));
				} else {
					permission = permission.max(aRule.getPermission(pAdaptation, pSession, pNode));
				}
			}
		}
		return permission;
	}

	/**
	 * Sets the access rule on node.
	 *
	 * @param pPath the path of the node
	 * @param pRule the access rule
	 */
	public void setAccessRuleOnNode(final Path pPath, final AccessRule pRule) {
		AccessRulesManager.addRuleToMap(pPath, pRule, this.rulesOnNodes);

	}

	/**
	 * Sets the access rule on node and all descendants.
	 *
	 * @param pPath        the path of the node
	 * @param pIncludeRoot set to true to include the root node.
	 * @param pRule        the rule to set
	 */
	public void setAccessRuleOnNodeAndAllDescendants(final Path pPath, final boolean pIncludeRoot, final AccessRule pRule) {
		if (pIncludeRoot) {
			this.setAccessRuleOnNode(pPath, pRule);
		}

		SchemaNode rootNode = this.context.getSchemaNode().getNode(pPath);

		if (rootNode.isTableNode()) {
			rootNode = rootNode.getTableOccurrenceRootNode();
		}

		for (SchemaNode aNode : rootNode.getNodeChildren()) {
			this.setAccessRuleOnNodeAndAllDescendants(aNode.getPathInSchema(), true, pRule);
		}
	}

	/**
	 * Sets the access rule on occurrence.
	 *
	 * @param pPath the path
	 * @param pRule the rule
	 */
	public void setAccessRuleOnOccurrence(final Path pPath, final AccessRule pRule) {
		AccessRulesManager.addRuleToMap(pPath, pRule, this.rulesOnOccurrences);
	}

	/**
	 * Sets the permission rule on a given path.
	 *
	 * @param pPath       the path
	 * @param pServiceKey the service key
	 * @param pRule       the rule
	 */
	public void setServicePermissionRuleOnNode(final Path pPath, final ServiceKey pServiceKey, final ServicePermissionRule<RecordEntitySelection> pRule) {
		List<ServicePermissionRule<RecordEntitySelection>> rules = this.rulesOnServices.get(pPath);
		if (rules != null) {
			rules.add(pRule);
		} else {
			rules = new ArrayList<>();
			rules.add(pRule);
			this.rulesOnServices.put(pPath, rules);
		}
	}

	@Override
	public UserServicePermission getPermission(final ServicePermissionRuleContext<RecordEntitySelection> pContext) {
		UserServicePermission permission = UserServicePermission.getEnabled();
		if (pContext.getEntitySelection().getRecord() != null) {
			List<ServicePermissionRule<RecordEntitySelection>> rules = this.rulesOnServices.get(pContext.getEntitySelection().getRecord().getSchemaNode().getPathInSchema());
			if (rules != null) {
				for (ServicePermissionRule<RecordEntitySelection> aRule : rules) {
					if (this.restrictive) {
						permission = permission.min(aRule.getPermission(pContext));
					} else {
						permission = permission.max(aRule.getPermission(pContext));
					}
				}
			}
		}
		return permission;
	}
}
