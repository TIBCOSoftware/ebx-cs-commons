/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2017. All rights reserved.
 */
package com.tibco.ebx.cs.commons.ui.widget;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;

/**
 * Widget to display a list of possible values as clickable labels. Values have to be defined in the enumeration property of the field in the datamodel. Widget is authorized for maximum 5 values to
 * display. Colors of borders and fonts when value is selected and unselected can be configured with HTML style value compliant (red, green, #C0C0C0...)
 *
 * @author Lionel Luquet
 * @since 1.9.0
 */
public class BarSelectorMonoValueWidgetFactory implements UIWidgetFactory<UISimpleCustomWidget> {
	/**
	 * Get object value
	 * 
	 * @param pXSTypeName  the current node xs type name
	 * @param pValueString the value as string
	 * @return the object value
	 * @since 1.9.0
	 */
	public static Object getObjectValue(final SchemaTypeName pXSTypeName, String pValueString) {
		Object value = null;
		if (pXSTypeName.equals(SchemaTypeName.XS_BOOLEAN)) {
			value = Boolean.valueOf(pValueString);
		} else if (pXSTypeName.equals(SchemaTypeName.XS_DATE)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			try {
				value = format.parse(pValueString);
			} catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		} else if (pXSTypeName.equals(SchemaTypeName.XS_DATETIME)) {
			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
			LocalDateTime ldt = LocalDateTime.from(timeFormatter.parse(pValueString));
			value = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		} else if (pXSTypeName.equals(SchemaTypeName.XS_DECIMAL)) {
			value = new BigDecimal(pValueString);
		} else if (pXSTypeName.equals(SchemaTypeName.XS_INT) || pXSTypeName.equals(SchemaTypeName.XS_INTEGER)) {
			value = Integer.valueOf(pValueString);
		} else if (pXSTypeName.equals(SchemaTypeName.XS_TIME)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			pValueString = dateFormat.format(new Date()) + "T" + pValueString;

			DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-ddTHH:mm:ss");
			LocalDateTime ldt = LocalDateTime.from(timeFormatter.parse(pValueString));
			value = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
		} else {
			value = pValueString;
		}
		return value;
	}

	/**
	 * Constructor
	 */
	public BarSelectorMonoValueWidgetFactory() {
		super();
	}

	private String unSelectedBorderColor;

	private String selectedBorderColor;
	private String unSelectedFontColor;

	private String selectedFontColor;
	private static final String DEFAULT_UNSELECTED_BORDER_COLOR = "#C0C0C0";

	private static final String DEFAULT_SELECTED_BORDER_COLOR = "#4767D2";
	private static final String DEFAULT_SELECTED_FONT_COLOR = "#1E1E1E";

	private static final String DEFAULT_UNSELECTED_FONT_COLOR = "#888";

	/**
	 * @return the html style property border color of a selected value
	 * @since 1.9.0
	 */
	public String getSelectedBorderColor() {
		return this.selectedBorderColor;
	}

	/**
	 * @return the html style property font color of a selected value
	 * @since 1.9.0
	 */
	public String getSelectedFontColor() {
		return this.selectedFontColor;
	}

	/**
	 * @return the html style property border color of an unselected value
	 * @since 1.9.0
	 */
	public String getUnSelectedBorderColor() {
		return this.unSelectedBorderColor;
	}

	/**
	 * @return the html style property font color of an unselected value
	 * @since 1.9.0
	 */
	public String getUnSelectedFontColor() {
		return this.unSelectedFontColor;
	}

	@Override
	public UISimpleCustomWidget newInstance(final WidgetFactoryContext pContext) {
		return new BarSelectorMonoValueWidget(pContext, this.unSelectedBorderColor, this.selectedBorderColor, this.unSelectedFontColor, this.selectedFontColor);
	}

	/**
	 * @param pSelectedBorderColor the html style property border color of a selected value
	 * @since 1.9.0
	 */
	public void setSelectedBorderColor(final String pSelectedBorderColor) {
		this.selectedBorderColor = pSelectedBorderColor;
	}

	/**
	 * @param pSelectedFontColor the html style property font color of a selected value
	 * @since 1.9.0
	 */
	public void setSelectedFontColor(final String pSelectedFontColor) {
		this.selectedFontColor = pSelectedFontColor;
	}

	/**
	 * @param pUnSelectedBorderColor the html style property border color of an unselected value
	 * @since 1.9.0
	 */
	public void setUnSelectedBorderColor(final String pUnSelectedBorderColor) {
		this.unSelectedBorderColor = pUnSelectedBorderColor;
	}

	/**
	 * @param pUnSelectedFontColor the html style property font color of an unselected value
	 * @since 1.9.0
	 */
	public void setUnSelectedFontColor(final String pUnSelectedFontColor) {
		this.unSelectedFontColor = pUnSelectedFontColor;
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		SchemaNode schemaNode = pContext.getSchemaNode();
		List<Object> values = schemaNode.getFacetEnumeration().getValues();
		if (values == null || values.isEmpty() || values.size() > 5) {
			pContext.addError(schemaNode.getPathInSchema() + ": in order to use the widget, there should be an enum defined in the model with max 5 values.");
		}

		if (this.unSelectedBorderColor == null) {
			this.unSelectedBorderColor = BarSelectorMonoValueWidgetFactory.DEFAULT_UNSELECTED_BORDER_COLOR;
		}

		if (this.selectedBorderColor == null) {
			this.selectedBorderColor = BarSelectorMonoValueWidgetFactory.DEFAULT_SELECTED_BORDER_COLOR;
		}

		if (this.selectedFontColor == null) {
			this.selectedFontColor = BarSelectorMonoValueWidgetFactory.DEFAULT_SELECTED_FONT_COLOR;
		}

		if (this.unSelectedFontColor == null) {
			this.unSelectedFontColor = BarSelectorMonoValueWidgetFactory.DEFAULT_UNSELECTED_FONT_COLOR;
		}
	}

}
