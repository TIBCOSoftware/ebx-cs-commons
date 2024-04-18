package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.tibco.ebx.cs.commons.lib.exception.EBXResourceNotFoundException;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;
import com.tibco.ebx.cs.commons.lib.utils.SchemaUtils;

/**
 * Dynamic attributes enumeration
 * 
 * @author Mickaël Chevalier
 */
public class DynamicAttributesEnumeration implements ConstraintEnumeration<String> {

	private String dataspace;
	private String dataset;
	private Path table;
	private String ignoreFieldsWithInformationEquals;

	/**
	 * Constructor
	 */
	public DynamicAttributesEnumeration() {
		super();
	}

	public void setIgnoreFieldsWithInformationEquals(final String ignoreFieldsWithInformationEquals) {
		this.ignoreFieldsWithInformationEquals = ignoreFieldsWithInformationEquals;
	}

	public void setDataspace(final String dataspace) {
		this.dataspace = dataspace;
	}

	public void setDataset(final String dataset) {
		this.dataset = dataset;
	}

	public void setTable(final Path table) {
		this.table = table;
	}

	@Override
	public void checkOccurrence(final String pValue, final ValueContextForValidation pContext) throws InvalidSchemaException {
		try {
			AdaptationTable table = AdaptationUtils.getTable(pContext, Optional.ofNullable(this.dataspace), Optional.ofNullable(this.dataset), this.table);
			SchemaNode node = table.getTableOccurrenceRootNode().getNode(Path.SELF.add(Path.parse(pValue)));
			if (node == null) {
				pContext.addError("No node at path '" + pValue + "'.");
			} else if (node.getInformation() != null && !StringUtils.isBlank(this.ignoreFieldsWithInformationEquals)
					&& this.ignoreFieldsWithInformationEquals.equals(node.getInformation().getInformation())) {
				pContext.addError("Node at path '" + pValue + "' is out of scope as its information contains the token to be ignored");
			}
		} catch (EBXResourceNotFoundException ex) {
			throw new InvalidSchemaException(ex);
		}
	}

	@Override
	public void setup(final ConstraintContext pContext) {
		// no implementation
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}

	@Override
	public String displayOccurrence(final String pValue, final ValueContext pContext, final Locale pLocale) throws InvalidSchemaException {
		try {
			AdaptationTable table = AdaptationUtils.getTable(pContext, Optional.ofNullable(this.dataspace), Optional.ofNullable(this.dataset), this.table);
			SchemaNode node = table.getTableOccurrenceRootNode().getNode(Path.SELF.add(Path.parse(pValue)));
			if (node == null) {
				return pValue;
			}
			return node.getLabel(pLocale);
		} catch (EBXResourceNotFoundException ex) {
			throw new InvalidSchemaException(ex);
		}
	}

	@Override
	public List<String> getValues(final ValueContext pContext) throws InvalidSchemaException {
		List<String> values = new ArrayList<>();
		try {
			AdaptationTable table = AdaptationUtils.getTable(pContext, Optional.ofNullable(this.dataspace), Optional.ofNullable(this.dataset), this.table);
			List<SchemaNode> nodes = SchemaUtils.getTerminalNodes(table.getTableOccurrenceRootNode(), this.ignoreFieldsWithInformationEquals);
			for (SchemaNode node : nodes) {
				values.add(node.getPathInAdaptation().format());
			}
		} catch (EBXResourceNotFoundException ex) {
			throw new InvalidSchemaException(ex);
		}

		return values;
	}
}
