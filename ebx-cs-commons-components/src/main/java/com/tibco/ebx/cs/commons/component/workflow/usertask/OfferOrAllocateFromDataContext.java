package com.tibco.ebx.cs.commons.component.workflow.usertask;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.workflow.CreationWorkItemSpec;
import com.orchestranetworks.workflow.UserTask;
import com.orchestranetworks.workflow.UserTaskCreationContext;

/**
 * @author Mickaël Chevalier
 */
public class OfferOrAllocateFromDataContext extends UserTask {

	private String profiles;

	public void setProfiles(final String profiles) {
		this.profiles = profiles;
	}

	@Override
	public void handleCreate(final UserTaskCreationContext pContext) throws OperationException {
		List<Profile> listOfProfiles = new ArrayList<>();
		if (StringUtils.isBlank(this.profiles)) {
			listOfProfiles.add(Profile.ADMINISTRATOR);
		} else {
			for (String profile : this.profiles.split(",")) {
				listOfProfiles.add(Profile.parse(profile));
			}
			if (listOfProfiles.isEmpty()) {
				listOfProfiles.add(Profile.ADMINISTRATOR);
			}
		}
		CreationWorkItemSpec forOfferring = CreationWorkItemSpec.forOfferring(listOfProfiles);
		forOfferring.setNotificationMail(pContext.getOfferedToNotificationMail());
		pContext.createWorkItem(forOfferring);
	}
}
