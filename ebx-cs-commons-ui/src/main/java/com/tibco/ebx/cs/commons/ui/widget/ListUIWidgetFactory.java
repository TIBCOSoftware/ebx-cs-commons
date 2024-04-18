/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;

/**
 * This is an editor so that list-fields can be displayed in a table as a comma-separated string (or some separator other than comma).
 * 
 * @author Mickaël Chevalier
 */
public class ListUIWidgetFactory extends BaseUICustomWidgetFactory<ListUICustomWidget> {
	private static final String DEFAULT_LIST_VALUES_SEPARATOR = ", ";

	private String separator = DEFAULT_LIST_VALUES_SEPARATOR;

	@Override
	public ListUICustomWidget newInstance(final WidgetFactoryContext aContext) {
		return new ListUICustomWidget(aContext, this);
	}

	@Override
	public void setup(final WidgetFactorySetupContext aContext) {
		if (separator == null) {
			aContext.addError("separator parameter must be specified.");
		}
		if (aContext.getSchemaNode().getMaxOccurs() == 1) {
			aContext.addError("This widget must be used with a multi-occurring field.");
		}
		super.setup(aContext);
	}

	/**
	 * See {@link #setSeparator()}
	 */
	public String getSeparator() {
		return separator;
	}

	/**
	 * Set the separator for the list of values
	 * 
	 * @param separator the separator
	 */
	public void setSeparator(final String separator) {
		this.separator = separator;
	}
}
