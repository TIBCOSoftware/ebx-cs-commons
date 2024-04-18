/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.SessionPermissions;
import com.orchestranetworks.service.UserReference;

/**
 * @author Mickaël Chevalier
 */
public interface PermissionsUserManager {
	SessionPermissions getSessionPermissions(Repository repo, UserReference user);
}
