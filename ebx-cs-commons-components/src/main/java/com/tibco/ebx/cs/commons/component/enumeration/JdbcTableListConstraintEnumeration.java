/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaTypeName;

/**
 * A constraint enumeration of the schemas of JDBC {@link Connection}. See {@link JdbcAbstractConstraint} for constraint parameters.
 * <h3>Parameters</h3>
 * <p>
 * Also see {@link JdbcAbstractConstraint} for constraint parameters.
 * </p>
 * <ul>
 * <li><tt>schemaName</tt> constant schema name
 * <li><tt>relativePathSchemaName</tt> relative path to a field representing the schema name
 * </ul>
 * If no schema name is specified or available, there is no filtering on the schema of the table list.
 * 
 * @author Mickaël Chevalier
 *
 */
public class JdbcTableListConstraintEnumeration extends JdbcAbstractConstraint<String> implements ConstraintEnumeration<String> {

	private String schemaName;

	private Path relativePathSchemaName;

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	public void setRelativePathSchemaName(final Path relativePathSchemaName) {
		this.relativePathSchemaName = relativePathSchemaName;
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		super.setup(aContext);
		if (relativePathSchemaName != null && !SchemaTypeName.XS_STRING.equals(aContext.getSchemaNode().getNode(relativePathSchemaName, false, false).getXsTypeName())) {
			aContext.addError(relativePathSchemaName.format() + " is not a path to a String field");
		}
	}

	@Override
	public String displayOccurrence(final String aValue, final ValueContext aContext, final Locale aLocale) throws InvalidSchemaException {
		return aValue;
	}

	@Override
	public void checkOccurrence(final String aValue, final ValueContextForValidation aValidationContext) throws InvalidSchemaException {
		// nothing to check
	}

	@Override
	public List<String> getValues(final ValueContext aContext) throws InvalidSchemaException {
		try (Connection connection = getConnection(aContext)) {
			if (connection == null) {
				return Collections.emptyList();
			}
			List<String> values = new ArrayList<>();
			ResultSet schemas = connection.getMetaData().getTables(null, getSchema(aContext), null, null);
			while (schemas.next()) {
				values.add(schemas.getString("TABLE_NAME"));
			}
			return values;
		} catch (Exception ex) {
			return Collections.emptyList();
		}
	}

	protected String getSchema(final ValueContext aContext) {
		if (relativePathSchemaName != null) {
			return (String) aContext.getValue(relativePathSchemaName);
		}
		return schemaName;
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}
}
