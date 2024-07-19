/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.scheduledtask;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.scheduler.ScheduledExecutionContext;
import com.orchestranetworks.scheduler.ScheduledTask;
import com.orchestranetworks.scheduler.ScheduledTaskInterruption;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.extensions.ReplicationUnit;
import com.orchestranetworks.service.extensions.ReplicationUnitKey;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;

/**
 * A scheduled task to refresh replicas for a given dataspace, dataset and replication unit. To specify multiple, use a comma-separated list. The number of data spaces, data sets, and replication unit
 * names must match and be in the right order. (i.e. if you specify two data sets for the same data space, you should repeat the data space twice)
 */
public class RefreshReplicasScheduledTask extends ScheduledTask {
	private static final char SEPARATOR = ',';

	private String dataSpaceName;
	private String dataSetName;
	private String replicationUnitName;

	@Override
	public void execute(final ScheduledExecutionContext context) throws ScheduledTaskInterruption {
		Repository repo = context.getRepository();
		Session session = context.getSession();

		String[] dataSpaceNameArr = getValueAsArray(this.dataSpaceName);
		String[] dataSetNameArr = getValueAsArray(this.dataSetName);
		String[] replicationUnitNameArr = getValueAsArray(this.replicationUnitName);

		for (int i = 0; i < dataSpaceNameArr.length; i++) {
			try {
				refreshReplication(dataSpaceNameArr[i], dataSetNameArr[i], replicationUnitNameArr[i], repo, session);
			} catch (EBXResourceNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	@Override
	public void validate(final ValueContextForValidation context) {
		boolean checkSizes = true;

		if (this.dataSpaceName == null) {
			context.addError("dataSpaceName must be specified.");
			checkSizes = false;
		}
		if (this.dataSetName == null) {
			context.addError("dataSetName must be specified.");
			checkSizes = false;
		}
		if (this.replicationUnitName == null) {
			context.addError("replicationUnitName must be specified.");
			checkSizes = false;
		}

		if (checkSizes) {
			String[] dataSpaceNameArr = getValueAsArray(this.dataSpaceName);
			String[] dataSetNameArr = getValueAsArray(this.dataSetName);
			String[] replicationUnitNameArr = getValueAsArray(this.replicationUnitName);

			if (dataSpaceNameArr.length != dataSetNameArr.length || dataSpaceNameArr.length != replicationUnitNameArr.length) {
				context.addError("Number of specified data spaces, data sets, and replication units must match.");
			}
		}
	}

	private static void refreshReplication(final String dSpace, final String dSet, final String rUnit, final Repository repo, final Session session) throws EBXResourceNotFoundException {
		Adaptation dataSet = AdaptationUtils.getDataset(repo, dSpace, dSet);
		ReplicationUnit replicationUnit = ReplicationUnit.newReplicationUnit(ReplicationUnitKey.forName(rUnit), dataSet);
		replicationUnit.performRefresh(session);
	}

	private static String[] getValueAsArray(final String value) {
		return StringUtils.split(value, SEPARATOR);
	}

	public String getDataSpaceName() {
		return this.dataSpaceName;
	}

	public void setDataSpaceName(final String dataSpaceName) {
		this.dataSpaceName = dataSpaceName;
	}

	public String getDataSetName() {
		return this.dataSetName;
	}

	public void setDataSetName(final String dataSetName) {
		this.dataSetName = dataSetName;
	}

	public String getReplicationUnitName() {
		return this.replicationUnitName;
	}

	public void setReplicationUnitName(final String replicationUnitName) {
		this.replicationUnitName = replicationUnitName;
	}
}
