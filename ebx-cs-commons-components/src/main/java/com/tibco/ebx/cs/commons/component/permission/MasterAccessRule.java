/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.permission;

import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.service.AccessRule;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.SessionPermissions;

/**
 * Constructed with the name of a dataSpace and a dataSet as well as an instance of MasterAccessRulePathConfig which is used to determine the permission path as well as other dynamic settings, such as
 * the name of the table on which to access the various dynamic values (including xpath predicate to determine if this rule is applicable for a given record and/or user), this access rule can be used
 * to create permissions that are dynamic, based on values in a reference table.
 * 
 * @see MasterAccessRulePathConfig
 * @author Mickaël Chevalier
 */
public final class MasterAccessRule implements AccessRule {
	private final String dataSpaceName;
	private final String dataSetName;
	private final MasterAccessRulePathConfig pathConfig;

	public MasterAccessRule(final String dataSpaceName, final String dataSetName, final MasterAccessRulePathConfig pathConfig) {
		this.dataSpaceName = dataSpaceName;
		this.dataSetName = dataSetName;
		this.pathConfig = pathConfig;
	}

	@Override
	public final AccessPermission getPermission(final Adaptation aAdaptation, final Session aSession, final SchemaNode aNode) {
		final AdaptationHome home = aAdaptation.getHome();
		final Repository repository = home.getRepository();
		AdaptationHome apHome = repository.lookupHome(HomeKey.forBranchName(this.dataSpaceName));
		if (apHome == null) {
			return AccessPermission.getReadWrite();
		}
		final Adaptation apInstance = apHome.findAdaptationOrNull(AdaptationName.forName(this.dataSetName));
		if (apInstance == null) {
			return AccessPermission.getReadWrite();
		}
		final AdaptationTable apTable = apInstance.getTable(this.pathConfig.getAccessPermissionPath());
		String predicate = this.getPredicateForAccessPermission(aAdaptation, home);

		Adaptation ap = null;
		AccessPermission accessPermission = AccessPermission.getReadWrite();
		AccessPermission dynamicAccessPermission = null;
		SessionPermissions sessionPermissions = null;
		String user = null;
		RequestResult result = apTable.createRequestResult(predicate);
		try {
			while ((ap = result.nextAdaptation()) != null) {
				if (!this.isAdaptationConcerned(ap, aAdaptation)) {
					continue;
				}
				if (!this.isUserConcerned(ap, aSession)) {
					continue;
				}
				user = ap.getString(this.pathConfig.getAccessPermissionPermissionPath());
				sessionPermissions = repository.createSessionPermissionsForUser(Profile.forUser(user));
				dynamicAccessPermission = sessionPermissions.getNodeAccessPermission(aNode, aAdaptation);
				accessPermission = accessPermission.min(dynamicAccessPermission);
			}
		} finally {
			result.close();
		}

		return accessPermission;
	}

	private String getPredicateForAccessPermission(final Adaptation aAdaptation, final AdaptationHome aHome) {
		StringBuilder predicate = new StringBuilder();
		predicate.append("(").append(this.pathConfig.getAccessPermissionDataSpacePath().format()).append(" = '").append(aHome.getKey().getName()).append("' or osd:is-null(")
				.append(this.pathConfig.getAccessPermissionDataSpacePath().format()).append(")) and (osd:is-null(").append(this.pathConfig.getAccessPermissionDataSetPath().format()).append(") or ")
				.append(this.pathConfig.getAccessPermissionDataSetPath().format());
		if (aAdaptation.isSchemaInstance()) {
			predicate.append(" = '").append(aAdaptation.getAdaptationName().getStringName()).append("')");
		} else {
			predicate.append(" = '").append(aAdaptation.getContainer().getAdaptationName().getStringName()).append("') and ").append(this.pathConfig.getAccessPermissionTablePath().format())
					.append(" = '").append(aAdaptation.getContainerTable().getTablePath().format()).append("'");
		}
		return predicate.toString();
	}

	private boolean isAdaptationConcerned(final Adaptation ap, final Adaptation aAdaptation) {
		final String condition = ap.getString(this.pathConfig.getAccessPermissionConditionPath());
		return aAdaptation.matches(condition);
	}

	private boolean isUserConcerned(final Adaptation ap, final Session aSession) {
		final List<String> roles = ap.getList(this.pathConfig.getAccessPermissionRolePath());
		for (String role : roles) {
			if (aSession.isUserInRole(Profile.forSpecificRole(role))) {
				return true;
			}
		}
		return false;
	}
}
