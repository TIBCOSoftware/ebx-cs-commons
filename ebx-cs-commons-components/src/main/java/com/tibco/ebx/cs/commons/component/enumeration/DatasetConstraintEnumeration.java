package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.message.Messages;
import com.tibco.ebx.cs.commons.lib.repository.RepositoryUtils;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * Constraint enumeration to list the datasets of a given dataspace. You can also include a value "This" to represent the current dataset and restrict to "This".
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class DatasetConstraintEnumeration implements ConstraintEnumeration<String> {
	// TODO include This only if it is actually in the dataspace?

	/**
	 * String value of "This".
	 *
	 */
	private Path dataspacePath;
	private boolean excludeThisDataset = false;
	private boolean restrictToRootDataset = false;
	private boolean restrictToThis = false;
	private boolean includeThis = false;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		if (pValue.equals(CommonsConstants.THIS)) {
			return Messages.get(this.getClass(), pLocale, "dataset.This");
		}

		Repository repository = pContext.getAdaptationInstance().getHome().getRepository();
		String dataspaceName = (String) pContext.getValue(this.dataspacePath);

		if (dataspaceName == null || dataspaceName.isEmpty()) {
			return pValue;
		}

		AdaptationHome dataspace = null;
		if (dataspaceName.equals(CommonsConstants.THIS)) {
			dataspace = pContext.getAdaptationInstance().getHome();
		} else {
			dataspace = repository.lookupHome(HomeKey.forBranchName(dataspaceName));
		}

		Adaptation dataset = dataspace.findAdaptationOrNull(AdaptationName.forName(pValue));
		return dataset.getLabelOrName(pLocale);
	}

	/**
	 * Getter for the dataspacePath parameter.
	 *
	 * @return the path of the dataspace node.
	 * @since 1.8.0
	 */
	public Path getDataspacePath() {
		return this.dataspacePath;
	}

	private Adaptation getRootParent(final Adaptation pDataset) {
		Adaptation parent = pDataset.getParent();
		if (parent == null) {
			return pDataset;
		}

		return this.getRootParent(parent);
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		if (this.isIncludeThis()) {
			values.add(CommonsConstants.THIS);
		}
		if (this.isRestrictToThis()) {
			return values;
		}

		Adaptation currentDataset = pContext.getAdaptationInstance();
		AdaptationHome currentDataspace = currentDataset.getHome();
		Repository repository = currentDataspace.getRepository();

		if (this.dataspacePath == null) {
			return values;
		}

		String dataspaceName = (String) pContext.getValue(this.dataspacePath);
		if (dataspaceName == null || dataspaceName.isEmpty()) {
			return values;
		}

		AdaptationHome dataspace = null;
		if (dataspaceName.equals(CommonsConstants.THIS)) {
			dataspace = currentDataspace;
		} else {
			dataspace = RepositoryUtils.getDataSpace(repository, dataspaceName);
		}

		if (dataspace == null) {
			return values;
		}

		Adaptation currentRootDataset = this.getRootParent(currentDataset);

		for (Adaptation dataset : dataspace.findAllRoots()) {
			String datasetName = dataset.getAdaptationName().getStringName();

			if (dataset.equals(currentRootDataset)) {
				if (!this.excludeThisDataset) {
					values.add(datasetName);
					if (!this.restrictToRootDataset) {
						for (Adaptation childDataset : dataspace.findAllDescendants(dataset)) {
							values.add(childDataset.getAdaptationName().getStringName());
						}
					}
				}
				continue;
			}

			values.add(datasetName);
			if (!this.restrictToRootDataset) {
				for (Adaptation childDataset : dataspace.findAllDescendants(dataset)) {
					values.add(childDataset.getAdaptationName().getStringName());
				}
			}
		}
		return values;
	}

	/**
	 * Getter for the excludeThisDataset parameter.
	 *
	 * @return true if this dataset is excluded, false otherwise.
	 * @since 1.8.0
	 */
	public boolean isExcludeThisDataset() {
		return this.excludeThisDataset;
	}

	/**
	 * Getter for the includeThis parameter.
	 *
	 * @return true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public boolean isIncludeThis() {
		return this.includeThis;
	}

	/**
	 * Getter for the restrictToRootDataset parameter.
	 *
	 * @return true if the enumeration is restricted to the root datasets of a dataspace, false if not.
	 * @since 1.8.0
	 */
	public boolean isRestrictToRootDataset() {
		return this.restrictToRootDataset;
	}

	/**
	 * Getter for the restrictToThis parameter.
	 *
	 * @return true if the enumeration is restricted to "This", false if not.
	 */
	public boolean isRestrictToThis() {
		return this.restrictToThis;
	}

	/**
	 * Setter for the dataspacePath parameter. Can be null.
	 *
	 * @param pDataspacePath the path of the dataspace node.
	 * @since 1.8.0
	 */
	public void setDataspacePath(final Path pDataspacePath) {
		this.dataspacePath = pDataspacePath;
	}

	/**
	 * Setter for the excludeThisDataset parameter.
	 *
	 * @param pExcludeThisDataset true if this dataset is excluded, false otherwise.
	 * @since 1.8.0
	 */
	public void setExcludeThisDataset(final boolean pExcludeThisDataset) {
		this.excludeThisDataset = pExcludeThisDataset;
	}

	/**
	 * Setter for the includeThis parameter.
	 *
	 * @param pIncludeThis true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public void setIncludeThis(final boolean pIncludeThis) {
		this.includeThis = pIncludeThis;
	}

	/**
	 * Setter for the restrictToRootDataset parameter.
	 *
	 * @param pRestrictToRootDataset true if the enumeration is restricted to the root datasets of a dataspace, false if not.
	 * @since 1.8.0
	 */
	public void setRestrictToRootDataset(final boolean pRestrictToRootDataset) {
		this.restrictToRootDataset = pRestrictToRootDataset;
	}

	/**
	 * Setter for the restrictToThis parameter.
	 *
	 * @param pRestrictToThis true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public void setRestrictToThis(final boolean pRestrictToThis) {
		this.restrictToThis = pRestrictToThis;
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		SchemaNode schemaNode = pContext.getSchemaNode();

		if (this.dataspacePath != null) {
			SchemaNode dataspaceNode = schemaNode.getNode(this.dataspacePath);
			if (dataspaceNode != null) {
				pContext.addDependencyToModify(dataspaceNode);
			} else {
				pContext.addError("The path [" + this.dataspacePath.format() + "] does not exist in the table");
			}
		}
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "DatasetConstraintEnumeration.userDocumentation");
	}
}
