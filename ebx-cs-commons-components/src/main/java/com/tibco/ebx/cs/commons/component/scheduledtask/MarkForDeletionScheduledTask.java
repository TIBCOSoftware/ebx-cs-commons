/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.scheduledtask;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Session;

//TODO To be documented and standardized.
/**
 *
 * @author US-Team
 *
 *         A scheduled task that marks for deletion closed data spaces older than a specified number of days
 */
public class MarkForDeletionScheduledTask extends ScheduledTask {
	private String parentDataSpace = Repository.REFERENCE.getName();
	private int daysToKeep;
	private boolean deleteHistory;

	private void deleteHome(final AdaptationHome home, final Date earlierThanDate, final Session session) throws OperationException {
		// Only delete if the date it was closed is before the specified date
		Date closeDate = home.getTerminationDate();
		if (closeDate != null && closeDate.before(earlierThanDate)) {
			Repository repo = home.getRepository();
			repo.deleteHome(home, session);
			if (this.deleteHistory) {
				repo.getPurgeDelegate().markHomeForHistoryPurge(home, session);
			}
		}
	}

	@Override
	public void execute(final ScheduledExecutionContext context) throws OperationException, ScheduledTaskInterruption {
		Repository repo = context.getRepository();
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(this.parentDataSpace));
		if (dataSpace == null) {
			LoggingCategory.getKernel().warn("Parent data space " + this.parentDataSpace + " does not exist.");
		} else {
			// Find the date at the specified number of days prior to the current date
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, this.daysToKeep * -1);
			// Delete all children that were closed earlier than that date
			this.markChildrenForDeletion(context.getSession(), dataSpace, cal.getTime());
		}
	}

	public int getDaysToKeep() {
		return this.daysToKeep;
	}

	public String getParentDataSpace() {
		return this.parentDataSpace;
	}

	public boolean isDeleteHistory() {
		return this.deleteHistory;
	}

	private void markChildrenForDeletion(final Session session, final AdaptationHome dataSpace, final Date earlierThanDate) throws OperationException {
		List<AdaptationHome> childVersions = dataSpace.getVersionChildren();
		for (AdaptationHome childVersion : childVersions) {
			// Skip if it's a technical snapshot
			if (childVersion.isTechnicalVersion()) {
				continue;
			}
			// If it's open then delete its closed child data spaces
			if (childVersion.isOpen()) {
				List<AdaptationHome> childDataSpaces = childVersion.getBranchChildren();
				for (AdaptationHome childDataSpace : childDataSpaces) {
					// Skip if it's a technical data space
					if (childDataSpace.isTechnicalBranch()) {
						continue;
					}
					// If it's open, call this method on its children
					if (childDataSpace.isOpen()) {
						this.markChildrenForDeletion(session, childDataSpace, earlierThanDate);
					} else {
						this.deleteHome(childDataSpace, earlierThanDate, session);
					}
				}
			} else {
				this.deleteHome(childVersion, earlierThanDate, session);
			}
		}
	}

	public void setDaysToKeep(final int daysToKeep) {
		this.daysToKeep = daysToKeep;
	}

	public void setDeleteHistory(final boolean deleteHistory) {
		this.deleteHistory = deleteHistory;
	}

	public void setParentDataSpace(final String parentDataSpace) {
		this.parentDataSpace = parentDataSpace;
	}

	@Override
	public void validate(final ValueContextForValidation context) {
		if (this.parentDataSpace == null || this.parentDataSpace.trim().length() == 0) {
			context.addError("Must specify parent data space. Use \"Reference\" for all data spaces.");
		} else {
			Repository repo = context.getHome().getRepository();
			AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(this.parentDataSpace));
			// Make this a warning because it could be they just haven't created it yet
			if (dataSpace == null) {
				context.addWarning("Parent data space \"" + this.parentDataSpace + "\" does not exist.");
			}
		}
		if (this.daysToKeep < 0) {
			context.addError("daysToKeep parameter must be greater than or equal to 0.");
		}
	}
}
