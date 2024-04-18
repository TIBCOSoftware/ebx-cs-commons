package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.service.AccessPermission;

public class AccessPermissionEnumeration implements ConstraintEnumeration<String> {

	@Override
	public void checkOccurrence(final String arg0, final ValueContextForValidation arg1) throws InvalidSchemaException {
		// nothing to check

	}

	@Override
	public String displayOccurrence(final String value, final ValueContext vc, final Locale locale) throws InvalidSchemaException {
		if (value != null) {
			return AccessPermission.parseFlag(value).getLabel();
		}

		return null;
	}

	@Override
	public List<String> getValues(final ValueContext vc) throws InvalidSchemaException {
		List<String> accessRights = new ArrayList<>();
		accessRights.add(AccessPermission.getHidden().getFlagString());
		accessRights.add(AccessPermission.getReadOnly().getFlagString());
		accessRights.add(AccessPermission.getReadWrite().getFlagString());
		return accessRights;
	}

	@Override
	public void setup(final ConstraintContext arg0) {
		// nothing to setup
	}

	@Override
	public String toUserDocumentation(final Locale arg0, final ValueContext arg1) throws InvalidSchemaException {
		return null;
	}
}
