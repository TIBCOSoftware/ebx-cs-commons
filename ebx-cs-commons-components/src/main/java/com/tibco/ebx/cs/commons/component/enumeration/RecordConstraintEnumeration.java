package com.tibco.ebx.cs.commons.component.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.RequestResult;
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
 * Constraint enumeration to list the records of a given table. You can also include a value "This" to represent the current dataset and restrict to "This".
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class RecordConstraintEnumeration implements ConstraintEnumeration<String> {
	/**
	 * String value of "This".
	 *
	 * @since 1.8.0
	 */
	public static final String RECORD_THIS = "This";

	private Path dataspacePath;
	private Path datasetPath;
	private Path tablePath;
	private boolean restrictToThis = false;
	private boolean includeThis = false;
	private String xpathFilter;

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		// no implementation
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		AdaptationTable table = this.getTable(pContext);
		if (table == null) {
			return pValue;
		}

		Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(pValue));
		if (record == null) {
			return pValue;
		}
		return record.getLabel(pLocale);
	}

	/**
	 * Getter for the datasetPath parameter.
	 *
	 * @return the path of the dataset node.
	 * @since 1.8.0
	 */
	public Path getDatasetPath() {
		return this.datasetPath;
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

	private AdaptationTable getTable(final ValueContext pContext) {
		String dataspaceName = (String) pContext.getValue(this.getDataspacePath());
		if (dataspaceName == null || dataspaceName.equals(CommonsConstants.THIS)) {
			AdaptationHome thisDataspace = pContext.getAdaptationInstance().getHome();
			dataspaceName = thisDataspace.getKey().getName();
		}

		String datasetName = (String) pContext.getValue(this.getDatasetPath());
		if (datasetName == null || datasetName.equals(CommonsConstants.THIS)) {
			Adaptation thisDataset = pContext.getAdaptationInstance();
			datasetName = thisDataset.getAdaptationName().getStringName();
		}

		String tablePathString = (String) pContext.getValue(this.getTablePath());
		if (tablePathString != null && tablePathString.equals(TableConstraintEnumeration.TABLE_THIS)) {
			AdaptationTable thisTable = pContext.getAdaptationTable();
			tablePathString = thisTable.getTablePath().format();
		}

		return RepositoryUtils.getTable(dataspaceName, datasetName, tablePathString);
	}

	/**
	 * Getter for the tablePath parameter.
	 *
	 * @return the path of the table node.
	 * @since 1.8.0
	 */
	public Path getTablePath() {
		return this.tablePath;
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();

		AdaptationTable table = this.getTable(pContext);
		if (table == null) {
			return values;
		}

		RequestResult requestResult = table.createRequestResult(this.xpathFilter);

		try {
			Adaptation record = null;
			while ((record = requestResult.nextAdaptation()) != null) {
				values.add(record.getOccurrencePrimaryKey().format());
			}
		} finally {
			requestResult.close();
		}

		return values;
	}

	/**
	 * Getter for the xpathFilter parameter.
	 *
	 * @return the xpath filter to apply on the records.
	 * @since 1.8.0
	 */
	public String getXpathFilter() {
		return this.xpathFilter;
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
	 * Getter for the restrictToThis parameter.
	 *
	 * @return true if the enumeration is restricted to "This", false if not.
	 * @since 1.8.0
	 */
	public boolean isRestrictToThis() {
		return this.restrictToThis;
	}

	/**
	 * Setter for the datasetPath parameter. Can be null.
	 *
	 * @param pDatasetPath the path of the dataset node.
	 * @since 1.8.0
	 */
	public void setDatasetPath(final Path pDatasetPath) {
		this.datasetPath = pDatasetPath;
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
	 * Setter for the includeThis parameter.
	 *
	 * @param pIncludeThis true if "This" shall be included, false if not.
	 * @since 1.8.0
	 */
	public void setIncludeThis(final boolean pIncludeThis) {
		this.includeThis = pIncludeThis;
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

	/**
	 * Setter for the tablePath parameter.
	 *
	 * @param pTablePath the path of the table node.
	 * @since 1.8.0
	 */
	public void setTablePath(final Path pTablePath) {
		this.tablePath = pTablePath;
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

		if (this.datasetPath != null) {
			SchemaNode datasetNode = schemaNode.getNode(this.datasetPath);
			if (datasetNode != null) {
				pContext.addDependencyToModify(datasetNode);
			} else {
				pContext.addError("The path [" + this.datasetPath.format() + "] does not exist in the table");
			}
		}

		if (this.tablePath != null) {
			SchemaNode tableNode = schemaNode.getNode(this.tablePath);
			if (tableNode != null) {
				pContext.addDependencyToModify(tableNode);
			} else {
				pContext.addError("The path [" + this.tablePath.format() + "] does not exist in the table");
			}
		} else {
			pContext.addError("The tablePath parameter shall not be null");
		}
	}

	/**
	 * Setter for the xpathFilter parameter.
	 *
	 * @param pXpathFilter the xpath filter to apply on the records.
	 * @since 1.8.0
	 */
	public void setXpathFilter(final String pXpathFilter) {
		this.xpathFilter = pXpathFilter;
	}

	@Override
	public String toUserDocumentation(final Locale pLocale, final ValueContext pContext) throws InvalidSchemaException {
		return Messages.get(this.getClass(), pLocale, "RecordConstraintEnumeration.userDocumentation");
	}
}
