/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.scheduledtask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.HomeCreationSpec;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;
import com.tibco.ebx.cs.commons.lib.utils.DateUtils;
import com.tibco.ebx.cs.commons.lib.utils.DateUtils.DateConstant;
import com.tibco.ebx.cs.commons.lib.utils.HomeUtils;

/**
 * Creates a snapshot of a specified data space and parses a label from the prefix and domain settings. This scheduled task will also delete any snapshots that are older than specified in the
 * daysTokeep parameter.
 * <p>
 * The following arguments are required by this scheduled task:
 * <ul>
 * <li><b>dataSpace:</b> String representing the data space (branch) from which the snapshot will be created.</li>
 * <li><b>domain:</b> String representing the Domain of the targeted data space.</li>
 * <li><b>prefix:</b> String to help distinguish the type of snapshot this scheduled task is creating.</li>
 * <li><b>daysToKeep:</b> Integer specifying the number of days to keep the snapshot.</li>
 * </ul>
 * The snapshot label will appear as <i>prefix</i>_<i>domain</i>_<i>date of creation</i>
 * <p>
 * For example, if the following configuration is defined for this scheduled task:
 * <ul>
 * <li><b>dataSpace</b> = MenuMasterDataSpace</li>
 * <li><b>prefix</b> = NIGHTLYSNAPSHOT</li>
 * <li><b>domain</b> = MENU</li>
 * <li><b>daysToKeep</b> = 7</li>
 * </ul>
 * then the snapshot label would read, for an August, 17, 2017 creation date, as:
 * <p>
 * <code>NIGHTLYSNAPSHOT_MENU_2017-08-17</code>
 * <p>
 * Once 7 days goes by, based on the example configuration above, the snapshot <code>NIGHTLYSNAPSHOT_MENU_2017-08-17</code> will be deleted.
 *
 * @author Orchestra Professional Services
 *
 */
public class SnapshotScheduledTask extends ScheduledTask {
	/**
	 * Used to format the date on the snapshot label.
	 */
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(CommonsConstants.EBX_DATE_FORMAT);

	private String dataSpace = Repository.REFERENCE.getName();

	private String prefix = "";

	private String domain = "";

	private int daysToKeep;

	public String getDataSpace() {
		return this.dataSpace;
	}

	public void setDataSpace(final String dataSpace) {
		this.dataSpace = dataSpace;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(final String domain) {
		this.domain = domain;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	public int getDaysToKeep() {
		return this.daysToKeep;
	}

	public void setDaysToKeep(final int daysToKeep) {
		this.daysToKeep = daysToKeep;
	}

	/**
	 * Override this method if the data space is different than the one found by looking up by dataSpace name.
	 */
	protected AdaptationHome getBranch(final Repository repo) {
		return repo.lookupHome(HomeKey.forBranchName(this.dataSpace));
	}

	/**
	 * If the branch can be null or closed, override this to return false
	 */
	protected boolean warnIfNoBranch() {
		return true;
	}

	@Override
	public void execute(final ScheduledExecutionContext context) throws OperationException, ScheduledTaskInterruption {
		Repository repo = context.getRepository();
		AdaptationHome dataSpaceHome = this.getBranch(repo);
		if (dataSpaceHome == null || !dataSpaceHome.isOpen() && this.warnIfNoBranch()) {
			LoggingCategory.getKernel().warn("Data space " + this.dataSpace + " does not exist.");
		}
		this.createSnapshot(context, repo, dataSpaceHome);
		this.purgeSnapshots(context, dataSpaceHome);
	}

	protected void createSnapshot(final ScheduledExecutionContext context, final Repository repo, final AdaptationHome dataSpaceHome) throws OperationException, ScheduledTaskInterruption {
		String creationDate = this.dateFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));

		String label = this.prefix + "_" + this.domain + "_" + creationDate;
		HomeKey key = HomeKey.forVersionName(label);

		HomeCreationSpec snapshot = new HomeCreationSpec();
		snapshot.setParent(dataSpaceHome);
		snapshot.setKey(key);
		snapshot.setOwner(CommonsConstants.TECH_ADMIN);
		snapshot.setLabel(UserMessage.createInfo(label));
		snapshot.setDescription(UserMessage.createInfo("Snapshot created on " + creationDate + " for the Data Space: " + this.dataSpace));
		snapshot.setHomeToCopyPermissionsFrom(dataSpaceHome);

		// Verify that the snapshot does not already exist.
		if (repo.lookupHome(key) == null) {
			repo.createHome(snapshot, context.getSession());
		}
	}

	protected void purgeSnapshots(final ScheduledExecutionContext context, final AdaptationHome dataSpaceHome) throws OperationException {
		// Find the date at the specified number of days prior to the current date
		Date today = new Date();
		Date daysBack = DateUtils.subtract(today, DateConstant.DAY, this.daysToKeep);
		// Delete all children that were closed earlier than that date
		this.markChildrenForDeletion(context.getSession(), dataSpaceHome, daysBack);
	}

	protected void markChildrenForDeletion(final Session session, final AdaptationHome dataSpace, final Date earlierThanDate) throws OperationException {
		List<AdaptationHome> childVersions = dataSpace.getVersionChildren();

		for (AdaptationHome childVersion : childVersions) {
			if (childVersion.isTechnicalVersion()) {
				continue;
			}
			if (!childVersion.isVersion()) {
				continue;
			}
			String name = childVersion.getKey().getName();

			// Search for snapshots with a label starting with the prefix_domain string.
			// This string makes up the first part of a snapshot created by this scheduled task.
			if (name.startsWith(this.prefix + "_" + this.domain)) {
				Date creationDate = childVersion.getCreationDate();

				if (DateUtils.afterExclusive(earlierThanDate, creationDate)) {
					if (childVersion.isOpen()) {
						HomeUtils.closeDataSpace(session, childVersion);
					}

					if (!childVersion.isOpen() && childVersion.getTerminationDate() != null) {
						HomeUtils.deleteDataspace(session, childVersion, false);
					}
				}
			}
		}
	}

}
