/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;

/**
 * Factory of {@link PercentageWidget}
 *
 * @author Mickaël Chevalier
 * @since 2.0.6
 */
public class PercentageWidgetFactory implements UIWidgetFactory<PercentageWidget> {

	@Override
	public PercentageWidget newInstance(final WidgetFactoryContext pContext) {
		return new PercentageWidget(pContext);
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		// nothing to setup
	}

}
