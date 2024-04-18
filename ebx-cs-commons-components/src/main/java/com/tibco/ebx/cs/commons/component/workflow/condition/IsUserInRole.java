package com.tibco.ebx.cs.commons.component.workflow.condition;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.orchestranetworks.service.OperationException;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.workflow.ConditionBean;
import com.orchestranetworks.workflow.ConditionBeanContext;
import com.tibco.ebx.cs.commons.lib.utils.SessionUtils;

/**
 * @author Mickaël Chevalier
 */
public class IsUserInRole extends ConditionBean {

	private String user;
	private String roles;
	private Boolean all;

	@Override
	public boolean evaluateCondition(final ConditionBeanContext pContext) throws OperationException {

		UserReference userReference = Profile.forUser(this.user);
		if (StringUtils.isBlank(this.roles) || StringUtils.isBlank(this.user)) {
			return false;
		} else {
			return SessionUtils.isUserInRoles(pContext.getRepository(), userReference, Arrays.asList(this.roles.split(",")), this.all);
		}
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public void setRoles(final String roles) {
		this.roles = roles;
	}

	public void setAll(final Boolean all) {
		this.all = all;
	}

}
