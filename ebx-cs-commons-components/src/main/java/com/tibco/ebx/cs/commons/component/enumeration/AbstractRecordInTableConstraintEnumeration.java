package com.tibco.ebx.cs.commons.component.enumeration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationTable;
import com.onwbp.adaptation.PrimaryKey;
import com.onwbp.adaptation.RequestResult;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.instance.ValueContextForValidation;
import com.orchestranetworks.schema.ConstraintEnumeration;
import com.orchestranetworks.schema.InvalidSchemaException;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.PathAccessException;
import com.orchestranetworks.service.OperationException;
import com.tibco.ebx.cs.commons.lib.utils.CommonsLogger;

public abstract class AbstractRecordInTableConstraintEnumeration implements ConstraintEnumeration<String> {

	public static final String MESSAGE = "{0} should specify a record in the {1} Table";
	public static final String GMESSAGE = "{0} should specify a record in the referenced table";

	@Override
	public void checkOccurrence(final String value, final ValueContextForValidation context) throws InvalidSchemaException {
		try {
			AdaptationTable table = this.getTable(context);
			if (table == null) {
				return;
			}
			Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(value));
			if (record == null) {
				context.addError(MessageFormat.format(MESSAGE, value, table.getTableNode().getLabel(Locale.getDefault())));
			}
		} catch (OperationException e) {
			context.addError("Failed to Reference Table for record lookup");
		}
	}

	public Adaptation getDataSet(final ValueContext context) {
		return context.getAdaptationTable().getContainerAdaptation();
	}

	protected AdaptationTable getTable(final ValueContext context) throws OperationException {
		Path tablePath = this.getTablePath(context);
		Adaptation dataSet = this.getDataSet(context);
		try {
			return dataSet.getTable(tablePath);
		} catch (PathAccessException e) {
			return null;
		}
	}

	protected abstract Path getTablePath(ValueContext context);

	@Override
	public String toUserDocumentation(final Locale locale, final ValueContext context) throws InvalidSchemaException {
		return MessageFormat.format(GMESSAGE, context.getNode().getLabel(locale));
	}

	@Override
	public String displayOccurrence(final String value, final ValueContext context, final Locale locale) throws InvalidSchemaException {
		return this.getLabelForRecord(context, value, locale);
	}

	public String getLabelForRecord(final ValueContext context, final String recordKey, final Locale locale) {
		if (StringUtils.isEmpty(recordKey)) {
			return null;
		}
		try {
			AdaptationTable table = this.getTable(context);
			if (table != null) {
				Adaptation record = table.lookupAdaptationByPrimaryKey(PrimaryKey.parseString(recordKey));
				if (record == null) {
					return recordKey;
				}
				return record.getLabel(locale);
			}
			return recordKey;
		} catch (OperationException e) {
			return recordKey + "(! error computing label)";
		}
	}

	@Override
	public List<String> getValues(final ValueContext context) throws InvalidSchemaException {
		RequestResult allRows = null;
		try {
			AdaptationTable table = this.getTable(context);
			allRows = table.createRequestResult(null);
			List<String> keys = new ArrayList<>();
			Adaptation next = allRows.nextAdaptation();
			while (next != null) {
				keys.add(next.getOccurrencePrimaryKey().format());
				next = allRows.nextAdaptation();
			}
			return keys;
		} catch (Exception e) {
			CommonsLogger.getLogger().error(e.getMessage(), e);
			return Collections.emptyList();
		} finally {
			if (allRows != null) {
				allRows.close();
			}
		}
	}

}
