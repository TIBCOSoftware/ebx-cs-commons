/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2017. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;

/**
 * Widget to display an integer value as a rating in stars. The maximum value has to be defined in the field properties. It is possible to define colors of starts (inside and border). Default values
 * are #383F9C (inside color) and #383F9C (border color).
 *
 * @author Lionel Luquet
 * @since 1.5.0
 */
public class StarRatingWidgetFactory implements UIWidgetFactory<StarRatingWidget> {

	private static final String STAR_STYLE = "width: 1em; height: 1em; line-height: 1em; vertical-align: bottom;";
	private static final String BORDER_STAR_COLOR = "#383F9C";

	private String borderStarColor;
	private String containerStyle = "display: inline-block; font-size: 1.3em; color:";

	private String fullStarStyle = "color:";
	private static final String INSIDE_STAR_COLOR = "#383F9C";

	private String insideStarColor;

	/**
	 * Constructor
	 */
	public StarRatingWidgetFactory() {
		super();
	}

	/**
	 * @return the border star color
	 * @since 1.5.0
	 */
	public String getBorderStarColor() {
		return this.borderStarColor;
	}

	/**
	 * @return the inside star color
	 * @since 1.5.0
	 */
	public String getInsideStarColor() {
		return this.insideStarColor;
	}

	@Override
	public StarRatingWidget newInstance(final WidgetFactoryContext context) {
		return new StarRatingWidget(context, this.containerStyle, this.fullStarStyle, StarRatingWidgetFactory.STAR_STYLE);
	}

	/**
	 * @param borderStarColor the border star color
	 * @since 1.5.0
	 */
	public void setBorderStarColor(final String borderStarColor) {
		this.borderStarColor = borderStarColor;
	}

	/**
	 * @param insideStarColor the inside star color
	 * @since 1.5.0
	 */
	public void setInsideStarColor(final String insideStarColor) {
		this.insideStarColor = insideStarColor;
	}

	@Override
	public void setup(final WidgetFactorySetupContext context) {
		if (this.insideStarColor == null) {
			this.insideStarColor = StarRatingWidgetFactory.INSIDE_STAR_COLOR;
		}

		if (this.borderStarColor == null) {
			this.borderStarColor = StarRatingWidgetFactory.BORDER_STAR_COLOR;
		}

		this.containerStyle += this.borderStarColor + ";";
		this.fullStarStyle += this.insideStarColor + ";";
	}

}
