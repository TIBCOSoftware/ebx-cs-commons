/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.lib.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.HomeCreationSpec;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Procedure;
import com.orchestranetworks.service.ProcedureContext;
import com.orchestranetworks.service.ProcedureResult;
import com.orchestranetworks.service.ProgrammaticService;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;

/**
 * Utility class to manipulate data spaces and snapshots.
 * 
 * @author Mickaël Chevalier
 */
public final class HomeUtils {

	private HomeUtils() {
		super();
	}

	/**
	 * Gets the ancestors.
	 *
	 * @author MCH
	 * 
	 *         Get all the data spaces, ancestors of a data space or a snapshot
	 * 
	 * @param pHome the home to get the ancestors from
	 * @return data spaces, the ancestors
	 */
	public static List<AdaptationHome> getAncestors(final AdaptationHome pHome) {
		List<AdaptationHome> ancestors = new ArrayList<>();

		AdaptationHome initialVersion = pHome;
		if (!pHome.isBranch()) {
			initialVersion = pHome.getParent();
		}

		if (initialVersion == null) {
			return ancestors;
		}

		AdaptationHome home = initialVersion.getParent();
		if (home == null) {
			return ancestors;
		}

		ancestors.add(home);
		ancestors.addAll(getAncestors(home));

		return ancestors;
	}

	/**
	 * Format home label
	 * 
	 * @param aHome   AdaptationHome
	 * @param aLocale Locale
	 * @return Home Label
	 */
	public static String formatHomeLabel(final AdaptationHome aHome, final Locale aLocale) {
		StringBuilder buffer = new StringBuilder();
		int depth = HomeUtils.getHomeDepth(aHome);

		for (int i = 0; i < depth * 3; i++) {
			buffer.append("&nbsp;");
		}

		if (aHome.isInitialVersion()) {
			for (int i = 0; i < 3; i++) {
				buffer.append("&nbsp;");
			}

			buffer.append((aHome.getBranchChildren().get(0)).getLabelOrName(aLocale)).append("&nbsp;&lt;initial version&gt;");
		} else {
			if (aHome.isVersion()) {
				buffer.append("(V)");
			}
			buffer.append(aHome.getLabelOrName(aLocale));
		}
		return buffer.toString();
	}

	/**
	 * Compute home depth
	 * 
	 * @param aHome aHome
	 * @return depth
	 */
	public static int getHomeDepth(final AdaptationHome aHome) {
		AdaptationHome parentBranch = aHome.getParentBranch();
		if (parentBranch == null) {
			return 0;
		}
		return getHomeDepth(parentBranch) + 1;
	}

	/**
	 * Merge a dataspace to its parent
	 * 
	 * @param session            session
	 * @param childDataSpaceHome child dataspace
	 * @throws OperationException OperationException
	 */
	public static void mergeDataSpaceToParent(final Session session, final AdaptationHome childDataSpaceHome) throws OperationException {
		// Merge of child data space procedure
		final Procedure merge = new Procedure() {
			@Override
			public void execute(final ProcedureContext pContext) throws Exception {
				closeDataSpaceChildren(childDataSpaceHome, session);
				pContext.setAllPrivileges(true);
				pContext.doMergeToParent(childDataSpaceHome);
				pContext.setAllPrivileges(false);
			}
		};

		// Merge of child data space execution
		final ProgrammaticService mergeService = ProgrammaticService.createForSession(session, childDataSpaceHome.getParentBranch());
		ProcedureResult result = mergeService.execute(merge);
		OperationException resultException = result.getException();
		if (resultException != null) {
			throw resultException;
		}
		if (result.hasFailed()) {
			throw OperationException.createError("Dataspace Merge execution failed.");
		}
	}

	/**
	 * Close all children for a dataspace
	 * 
	 * @param dataSpace dataSpace
	 * @param session   session
	 * @throws OperationException OperationException
	 */
	public static void closeDataSpaceChildren(final AdaptationHome dataSpace, final Session session) throws OperationException {
		// Closing a Data Space Child will recursively Close all its Ancestors
		List<AdaptationHome> dataSpaceChildList = dataSpace.getVersionChildren();
		for (AdaptationHome dataSpaceChild : dataSpaceChildList) {
			if (dataSpaceChild.isOpen()) {
				dataSpaceChild.getRepository().closeHome(dataSpaceChild, session);
			}

		}

	}

	/**
	 * Create a child dataspace
	 * 
	 * @param session                          session
	 * @param dataSpace                        parent dataspace
	 * @param childLabelPrefix                 child label prefix
	 * @param permissionsTemplateDataSpaceName permissions template
	 * @return AdaptationHome
	 * @throws OperationException OperationException
	 */
	public static AdaptationHome createChildDataSpace(final Session session, final AdaptationHome dataSpace, final String childLabelPrefix, final String permissionsTemplateDataSpaceName)
			throws OperationException {
		return createChildDataSpace(session, dataSpace, null, childLabelPrefix, permissionsTemplateDataSpaceName);
	}

	/**
	 * Create a child dataspace
	 * 
	 * @param session                          session
	 * @param dataSpace                        parent dataspace
	 * @param childLabelPrefix                 child label prefix
	 * @param childNamePrefix                  child name prefix
	 * @param permissionsTemplateDataSpaceName template
	 * @return AdaptationHome
	 * @throws OperationException OperationException
	 */
	public static AdaptationHome createChildDataSpace(final Session session, final AdaptationHome dataSpace, final String childNamePrefix, final String childLabelPrefix,
			final String permissionsTemplateDataSpaceName) throws OperationException {
		UserReference user = session.getUserReference();

		HomeCreationSpec homeCreationSpec = new HomeCreationSpec();
		SimpleDateFormat dateFormat = new SimpleDateFormat(CommonsConstants.DATA_SPACE_NAME_DATE_TIME_FORMAT);
		String childDataSpaceDateTimeStr = dateFormat.format(new Date());
		String branchName = childDataSpaceDateTimeStr;
		if (childNamePrefix != null) {
			branchName = childNamePrefix + branchName;
		}
		homeCreationSpec.setKey(HomeKey.forBranchName(branchName));
		homeCreationSpec.setOwner(user);
		homeCreationSpec.setParent(dataSpace);
		homeCreationSpec.setLabel(UserMessage.createInfo(childLabelPrefix + user.getLabel() + " at " + childDataSpaceDateTimeStr));

		Repository repo = dataSpace.getRepository();
		if (permissionsTemplateDataSpaceName != null) {
			AdaptationHome templateDataSpace = repo.lookupHome(HomeKey.forBranchName(permissionsTemplateDataSpaceName));
			if (templateDataSpace == null) {
				LoggingCategory.getKernel().error("Permissions template data space " + permissionsTemplateDataSpaceName + " not found.");
			} else {
				homeCreationSpec.setHomeToCopyPermissionsFrom(templateDataSpace);
			}
		}
		return repo.createHome(homeCreationSpec, session);
	}

	/**
	 * Close a dataspace
	 * 
	 * @param session   session
	 * @param dataSpace dataspace
	 * @throws OperationException OperationException
	 */
	public static void closeDataSpace(final Session session, final AdaptationHome dataSpace) throws OperationException {
		dataSpace.getRepository().closeHome(dataSpace, session);
	}

	/**
	 * Close a dataspace
	 * 
	 * @param session       session
	 * @param dataSpace     dataspace
	 * @param deleteHistory delete history ?
	 * @throws OperationException OperationException
	 */
	public static void deleteDataspace(final Session session, final AdaptationHome dataSpace, final boolean deleteHistory) throws OperationException {
		Repository repo = dataSpace.getRepository();
		if (deleteHistory) {
			repo.getPurgeDelegate().markHomeForHistoryPurge(dataSpace, session);
		}
		repo.deleteHome(dataSpace, session);
	}

	/**
	 * Delete a dataspace or throw an OperationException
	 * 
	 * @param aRepository repository
	 * @param aBranchName branch name
	 * @return AdaptationHome dataspace
	 * @throws OperationException OperationException
	 */
	public static AdaptationHome getDataSpaceOrThrowOperationException(final Repository aRepository, final String aBranchName) throws OperationException {
		final AdaptationHome home = aRepository.lookupHome(HomeKey.forBranchName(aBranchName));
		if (home == null) {
			throw OperationException.createError("Data space '" + aBranchName + "' does not exist");
		}
		return home;
	}
}
