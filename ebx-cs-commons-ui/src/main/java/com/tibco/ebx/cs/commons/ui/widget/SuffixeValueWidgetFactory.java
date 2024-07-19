/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2017. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;

/**
 * Factory to add a suffix to a field. Parameters allow to define the constant suffix and an HTML style properties to display the suffix. The default style value is {@code margin-left:5px;}
 * 
 * @author Lionel Luquet
 */
public class SuffixeValueWidgetFactory implements UIWidgetFactory<SuffixeValueWidget> {
	private static final String DEFAULT_STYLE = "margin-left:5px;";
	private String attributeSuffix;

	private String styleOnSpanSuffix;

	/**
	 * Constructor
	 */
	public SuffixeValueWidgetFactory() {
		super();
	}

	/**
	 * @return the attribute suffix
	 * @since 1.5.0
	 */
	public String getAttributeSuffix() {
		return this.attributeSuffix;
	}

	/**
	 * @return the html style property applied on suffix
	 * @since 1.5.0
	 */
	public String getStyleOnSpanSuffix() {
		return this.styleOnSpanSuffix;
	}

	@Override
	public SuffixeValueWidget newInstance(final WidgetFactoryContext aContext) {
		return new SuffixeValueWidget(aContext, this.attributeSuffix, this.styleOnSpanSuffix);
	}

	/**
	 * @param attributeSuffix the attribute suffix
	 * @since 1.5.0
	 */
	public void setAttributeSuffix(final String attributeSuffix) {
		this.attributeSuffix = attributeSuffix;
	}

	/**
	 * @param styleOnSpanSuffix the html style property applied on suffix
	 * @since 1.5.0
	 */
	public void setStyleOnSpanSuffix(final String styleOnSpanSuffix) {
		this.styleOnSpanSuffix = styleOnSpanSuffix;
	}

	@Override
	public void setup(final WidgetFactorySetupContext aContext) {
		if (this.styleOnSpanSuffix == null) {
			this.styleOnSpanSuffix = SuffixeValueWidgetFactory.DEFAULT_STYLE;
		}
	}

}
