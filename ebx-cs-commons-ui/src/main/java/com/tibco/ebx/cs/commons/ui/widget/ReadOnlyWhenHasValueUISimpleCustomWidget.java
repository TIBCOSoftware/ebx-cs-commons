package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Widget to define a read-only only if a value is set
 * 
 * @see ReadOnlyWhenHasValueUIWidgetFactory
 * @author Mickaël Chevalier
 */
public class ReadOnlyWhenHasValueUISimpleCustomWidget extends BaseUISimpleCustomWidget {
	/**
	 * Constructor
	 * 
	 * @param context WidgetFactoryContext
	 * @param factory ReadOnlyWhenHasValueUIWidgetFactory
	 */
	public ReadOnlyWhenHasValueUISimpleCustomWidget(final WidgetFactoryContext context, final ReadOnlyWhenHasValueUIWidgetFactory<?> factory) {
		super(context, factory);
	}

	@Override
	protected boolean isReadOnly(final WidgetWriter writer, final WidgetDisplayContext context) {
		boolean readOnly = super.isReadOnly(writer, context);
		// If the parent implementation says it's read only
		if (readOnly) {
			readOnly = ((ReadOnlyWhenHasValueUIWidgetFactory<?>) factory).isReadOnlyWhenHasValue(context);
		}
		return readOnly;
	}
}
