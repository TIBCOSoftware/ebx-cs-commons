package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;

/**
 * 
 * @author Mickaël Chevalier
 *
 */
public class DatasetsEnumeration implements ConstraintEnumeration<String> {
	private Path dataspace;

	@Override
	public void checkOccurrence(final String arg0, final ValueContextForValidation arg1) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		Repository repository = pContext.getAdaptationInstance().getHome().getRepository();
		String branchName = (String) pContext.getValue(this.dataspace);
		AdaptationHome home = repository.lookupHome(HomeKey.forBranchName(branchName));
		Adaptation instance = home.findAdaptationOrNull(AdaptationName.forName(pValue));
		return instance.getLabelOrName(pLocale);
	}

	public Path getDataspace() {
		return this.dataspace;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		Repository repository = pContext.getAdaptationInstance().getHome().getRepository();
		String branchName = (String) pContext.getValue(this.dataspace);
		if (StringUtils.isBlank(branchName)) {
			return values;
		}
		AdaptationHome home = repository.lookupHome(HomeKey.forBranchName(branchName));
		for (Adaptation instance : home.findAllRoots()) {
			values.add(instance.getAdaptationName().getStringName());
		}
		return values;
	}

	public void setDataspace(final Path dataspace) {
		this.dataspace = dataspace;
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		// no implementation
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}

}
