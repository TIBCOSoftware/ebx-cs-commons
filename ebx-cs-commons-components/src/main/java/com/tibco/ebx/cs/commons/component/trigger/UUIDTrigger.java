package com.tibco.ebx.cs.commons.component.trigger;

import java.util.UUID;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.trigger.NewTransientOccurrenceContext;
import com.orchestranetworks.schema.trigger.TableTrigger;
import com.orchestranetworks.schema.trigger.TriggerSetupContext;

/**
 * @author Mickaël Chevalier
 */
public class UUIDTrigger extends TableTrigger {

	private String uuidFieldPath = "./uuid";
	private Path uuidField;

	@Override
	public void handleNewContext(final NewTransientOccurrenceContext aContext) {
		aContext.getOccurrenceContextForUpdate().setValue(UUID.randomUUID().toString(), uuidField);
		super.handleNewContext(aContext);
	}

	@Override
	public void setup(final TriggerSetupContext aContext) {
		uuidField = Path.parse(uuidFieldPath);
	}

	public String getUuidFieldPath() {
		return uuidFieldPath;
	}

	public void setUuidFieldPath(final String uuidFieldPath) {
		this.uuidFieldPath = uuidFieldPath;
	}
}
