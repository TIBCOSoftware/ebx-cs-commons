/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2017. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Suffixe Value Widget
 * 
 * @see SuffixeValueWidgetFactory
 * @author Lionel Luquet
 */

public class SuffixeValueWidget extends UISimpleCustomWidget {
	private final String attributeSuffix;
	private final String styleOnSpanSuffix;

	/**
	 * Constructor
	 * 
	 * @param aContext          the context
	 * @param attributeSuffixe  the constant value of suffix
	 * @param styleOnSpanSuffix the html style applied on suffix
	 * @since 1.5.0
	 */
	public SuffixeValueWidget(final WidgetFactoryContext aContext, final String attributeSuffixe, final String styleOnSpanSuffix) {
		super(aContext);
		this.attributeSuffix = attributeSuffixe;
		this.styleOnSpanSuffix = styleOnSpanSuffix;
	}

	@Override
	public void write(final WidgetWriter aWriter, final WidgetDisplayContext aContext) {
		aWriter.add_cr("<div>");
		aWriter.addWidget(Path.SELF);

		aWriter.add("<span ");
		aWriter.addSafeAttribute("style", this.styleOnSpanSuffix);
		aWriter.add_cr(">" + this.attributeSuffix + "</span>");

		aWriter.add_cr("<div>");
	}

}
