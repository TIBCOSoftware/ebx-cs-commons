package com.tibco.ebx.cs.commons.lib.utils;

import java.util.ArrayList;
import java.util.List;

import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.DirectoryHandler;

/**
 * Utility class to manipulate Sessions and directory.
 *
 * @author Mickaël Chevalier
 */
public final class SessionUtils {

	private SessionUtils() {
		super();
	}

	/**
	 * @author MCH
	 *
	 *         Test if a user belongs to one or all profiles in parameters as a list of prefixed strings
	 *
	 * @param pRepository the repository
	 * @param pUser       the user
	 * @param pRoles      a list of roles as formatted strings, prefixed by B for built-in and R for specific.
	 * @param pAll        if true, user must have all roles and not only one for this method to return true.
	 */
	public static boolean isUserInRoles(final Repository pRepository, final UserReference pUser, final List<String> pRoles, final boolean pAll) {
		DirectoryHandler handler = DirectoryHandler.getInstance(pRepository);
		List<Profile> profilesOK = new ArrayList<>();
		for (String profileStr : pRoles) {

			Profile profile = Profile.parse(profileStr);
			if (profile.isUserReference() && profile.format().equals(pUser.format())) {
				profilesOK.add(profile);
			} else if (profile.isSpecificRole()) {
				if (handler.isUserInRole(pUser, Profile.forSpecificRole(profile.getLabel()))) {
					profilesOK.add(profile);
				}
			} else if (handler.isUserInRole(pUser, Profile.forBuiltInRole(profile.getLabel()))) {
				profilesOK.add(profile);
			}
		}
		return pAll && profilesOK.size() == pRoles.size() || !pAll && !profilesOK.isEmpty();
	}
}
