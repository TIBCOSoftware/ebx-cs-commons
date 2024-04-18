package com.tibco.ebx.cs.commons.component.enumeration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.service.LoggingCategory;

/**
 * A constraint enumeration of the schemas of JDBC {@link Connection}. See {@link JdbcAbstractConstraint} for constraint parameters.
 * 
 * @author Mickaël Chevalier
 *
 */
public class JdbcSchemaListConstraintEnumeration extends JdbcAbstractConstraint<String> implements ConstraintEnumeration<String> {

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
			ResultSet schemas = connection.getMetaData().getSchemas();
			while (schemas.next()) {
				values.add(schemas.getString("TABLE_SCHEM"));
			}
			return values;
		} catch (Exception ex) {
			LoggingCategory.getKernel().debug(ex.getMessage());
			return Collections.emptyList();
		}
	}

	@Override
	public String toUserDocumentation(final Locale userLocale, final ValueContext aContext) throws InvalidSchemaException {
		return null;
	}
}
