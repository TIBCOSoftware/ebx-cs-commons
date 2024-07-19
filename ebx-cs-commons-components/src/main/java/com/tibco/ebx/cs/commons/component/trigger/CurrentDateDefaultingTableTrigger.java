/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.component.trigger;

import java.util.Date;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.ValueContextForUpdate;

/**
 * @author Mickaël Chevalier
 */
public class CurrentDateDefaultingTableTrigger extends TableTrigger {
	protected Path datePath;

	@Override
	public void handleNewContext(final NewTransientOccurrenceContext context) {
		Session session = context.getSession();
		if (session.getInteraction(true) != null) {
			ValueContextForUpdate updateContext = context.getOccurrenceContextForUpdate();
			updateContext.setValue(new Date(), datePath);

		}
		super.handleNewContext(context);
	}

	@Override
	public void setup(final TriggerSetupContext context) {
		if (datePath != null) {
			SchemaNode dateNode = context.getSchemaNode().getNode(datePath);
			if (dateNode == null) {
				context.addError("datePath " + datePath.format() + " does not exist.");
			}
		}
	}

	public String getDatePath() {
		return this.datePath.format();
	}

	public void setDatePath(final String datePath) {
		this.datePath = Path.parse(datePath);
	}
}
