package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.AdaptationHome;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.tibco.ebx.cs.commons.lib.utils.HomeCollector;

/**
 * 
 * @author Mickaël Chevalier
 *
 */
public class DataspacesEnumeration implements ConstraintEnumeration<String> {
	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		AdaptationHome home = pContext.getHome().getRepository().lookupHome(HomeKey.forBranchName(pValue));
		return home.getLabelOrName(pLocale);
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		Repository repository = pContext.getAdaptationInstance().getHome().getRepository();
		HomeCollector collector = new HomeCollector();
		collector.setIncludingVersion(false);
		List<AdaptationHome> homes = collector.collectHomes(repository.getReferenceBranch(), true);
		for (AdaptationHome home : homes) {
			values.add(home.getKey().getName());
		}
		return values;
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// nothing to setup
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}

}
