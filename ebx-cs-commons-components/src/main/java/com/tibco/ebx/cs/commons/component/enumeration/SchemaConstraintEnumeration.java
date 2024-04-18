package com.tibco.ebx.cs.commons.component.enumeration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

/**
 * Constraint enumeration to list the schemas (only ones used in non-technical active datasets).
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class SchemaConstraintEnumeration implements ConstraintEnumeration<String> {
	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// not implemented
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		int endModuleName = pValue.substring(15, pValue.length()).indexOf(":") + 15;
		String moduleName = pValue.substring(15, endModuleName);
		File file = SchemaLocation.parse(pValue).getFileOrNull();
		if (file == null) {
			return pValue;
		}
		String fileName = file.getName();

		return moduleName + " - " + fileName;
	}

	private List<AdaptationHome> getDataspaces(final AdaptationHome pDataspace) {
		ArrayList<AdaptationHome> dataspaces = new ArrayList<>();

		if (pDataspace == null) {
			return dataspaces;
		}

		List<AdaptationHome> children = null;

		if (pDataspace.isBranch()) {
			children = pDataspace.getVersionChildren();
		} else {
			children = pDataspace.getBranchChildren();
		}

		for (AdaptationHome child : children) {
			if (child.isTechnicalBranch() || child.isTechnicalVersion()) {
				continue;
			}

			if (child.isBranch() && child.isOpen()) {
				dataspaces.add(child);
			}
			dataspaces.addAll(this.getDataspaces(child));
		}
		return dataspaces;
	}

	private List<String> getSchemas(final Repository pRepository) {
		List<AdaptationHome> dataspaces = new ArrayList<>();
		AdaptationHome referenceDataspace = pRepository.getReferenceBranch();
		dataspaces.add(referenceDataspace);
		dataspaces.addAll(this.getDataspaces(referenceDataspace));

		List<String> schemas = new ArrayList<>();
		for (AdaptationHome dataspace : dataspaces) {
			for (Adaptation dataset : dataspace.findAllRoots()) {
				if (dataset.getSchemaLocation().isReserved()) {
					continue;
				}
				schemas.add(dataset.getSchemaLocation().format());
			}
		}
		return schemas;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		Repository repository = pContext.getHome().getRepository();
		return getSchemas(repository);
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		// not implemented
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "SchemaConstraintEnumeration.userDocumentation");
	}
}
