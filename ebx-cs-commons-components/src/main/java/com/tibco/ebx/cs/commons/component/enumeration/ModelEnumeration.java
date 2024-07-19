/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.SchemaLocation;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.utils.HomeCollector;

/**
 * Constraint enumeration for data models (SchemaLocation)
 *
 * @see ModelTablesEnumeration
 * @see ModelTableFieldsEnumeration
 * @see ModelTableReferenceFieldsEnumeration
 * 
 * @author Mickaël Chevalier
 *
 */
public class ModelEnumeration implements ConstraintEnumeration<String> {

	@Override
	public void setup(final ConstraintContext aContext) {
		// no implementation
	}

	public static Adaptation getAnyDataset(final Repository pRepository, final String pSchemaLocation) {
		Map<String, Adaptation> map = getUsedSchemasLocationsWithOneDataset(pRepository);
		return map.get(pSchemaLocation);
	}

	private static Map<String, Adaptation> getUsedSchemasLocationsWithOneDataset(final Repository repository) {
		Map<String, Adaptation> datasets = new HashMap<>();
		HomeCollector collector = new HomeCollector();
		collector.setIncludingVersion(false);
		List<AdaptationHome> dataspaces = collector.collectHomes(repository.getReferenceBranch(), true);
		for (AdaptationHome dataspace : dataspaces) {
			for (Adaptation instance : dataspace.findAllRoots()) {
				datasets.put(instance.getSchemaLocation().format(), instance);
			}
		}
		return datasets;
	}

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		if (!ModelEnumeration.getUsedSchemasLocationsWithOneDataset(pContext.getHome().getRepository()).containsKey(pValue)) {
			pContext.addMessage(Messages.getError(this.getClass(), "ModelEnumeration.value0NotValid", pValue));
		}
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		int endModuleName = pValue.substring(15, pValue.length()).indexOf(":") + 15;
		String moduleName = pValue.substring(15, endModuleName);
		SchemaLocation loc = SchemaLocation.parse(pValue);
		if (loc == null) {
			return pValue;
		}
		File file;
		try {
			file = loc.getFileOrNull();
		} catch (NullPointerException ex) {
			return pValue;
		}
		if (file == null) {
			return pValue;
		}
		String fileName = file.getName();

		return moduleName + " - " + fileName;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		Map<String, Adaptation> map = ModelEnumeration.getUsedSchemasLocationsWithOneDataset(pContext.getHome().getRepository());
		return new ArrayList<>(map.keySet());
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.getInfo(this.getClass(), "ModelEnumeration.documentation").formatMessage(pLocale);
	}

}
