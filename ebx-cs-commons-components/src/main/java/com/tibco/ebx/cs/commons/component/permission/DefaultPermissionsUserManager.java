/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.permission;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.SessionPermissions;
import com.orchestranetworks.service.UserReference;

/**
 * @author Mickaël Chevalier
 */
public class DefaultPermissionsUserManager implements PermissionsUserManager {
	// Use a soft reference so that EBX can claim the memory used by this cache if needed
	private static SoftReference<DefaultPermissionsUserManager> instanceRef;

	private final Map<UserReference, SessionPermissions> userSessionPermissions = new HashMap<>();

	public static DefaultPermissionsUserManager getInstance() {
		// Get the object referenced by the soft reference
		DefaultPermissionsUserManager instance = instanceRef == null ? null : instanceRef.get();
		// If it's null (it's either never been initiated or the garbage collector cleaned it up)
		// then create a new instance of the class and store it in the soft reference
		if (instance == null) {
			synchronized (DefaultPermissionsUserManager.class) {
				instance = new DefaultPermissionsUserManager();
				instanceRef = new SoftReference<>(instance);
			}
		}
		return instance;
	}

	@Override
	public final SessionPermissions getSessionPermissions(final Repository repo, final UserReference user) {
		SessionPermissions permissions = userSessionPermissions.get(user);
		if (permissions == null) {
			permissions = refreshCache(repo, user);
		}
		return permissions;
	}

	public final void clearCache() {
		synchronized (userSessionPermissions) {
			userSessionPermissions.clear();
		}
	}

	private SessionPermissions refreshCache(final Repository repo, final UserReference user) {
		SessionPermissions permissions = repo.createSessionPermissionsForUser(user);

		synchronized (userSessionPermissions) {
			userSessionPermissions.put(user, permissions);
		}
		return permissions;
	}

	private DefaultPermissionsUserManager() {
	}
}
