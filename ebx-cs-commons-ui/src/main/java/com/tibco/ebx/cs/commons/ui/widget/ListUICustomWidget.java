package com.tibco.ebx.cs.commons.ui.widget;

import java.util.List;
import java.util.Locale;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetWriterForList;

/**
 * This is an editor so that list-fields can be displayed in a table.
 * 
 * @author Mickaël Chevalier
 */
public class ListUICustomWidget extends BaseUIListCustomWidget {
	/**
	 * Constructor
	 * 
	 * @param aContext WidgetFactoryContext
	 * @param factory  ListUIWidgetFactory
	 */
	public ListUICustomWidget(final WidgetFactoryContext aContext, final ListUIWidgetFactory factory) {
		super(aContext, factory);
	}

	private void addForDisplayInTable(final WidgetWriterForList aWriter, final WidgetDisplayContext aContext) {
		SchemaNode node = aContext.getNode();
		Path path = node.getPathInAdaptation();
		if (path.isIndexed()) {
			addForDisplayOrReadOnly(aWriter);
			return;
		}
		ValueContext vc = aContext.getValueContext();
		List<?> values = (List<?>) vc.getValue();
		StringBuilder sb = new StringBuilder();
		Locale locale = aWriter.getLocale();
		String separator = ((ListUIWidgetFactory) factory).getSeparator();

		boolean first = true;
		for (Object value : values) {
			value = node.displayOccurrence(value, true, vc, locale);
			if (!first) {
				sb.append(separator);
			} else {
				first = false;
			}
			sb.append(value);
		}
		aWriter.add(sb.toString());
	}

	@Override
	public void write(final WidgetWriterForList aWriter, final WidgetDisplayContext aContext) {
		if (aContext.isDisplayedInTable()) {
			addForDisplayInTable(aWriter, aContext);
		} else {
			super.write(aWriter, aContext);
		}
	}

}
