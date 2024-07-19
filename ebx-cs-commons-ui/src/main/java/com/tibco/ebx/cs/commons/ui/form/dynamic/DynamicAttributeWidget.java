/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form.dynamic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.AccessPermission;
import com.orchestranetworks.ui.form.widget.UIListCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.orchestranetworks.ui.form.widget.WidgetListValidationContext;
import com.orchestranetworks.ui.form.widget.WidgetWriterForList;
import com.tibco.ebx.cs.commons.lib.utils.AdaptationUtils;

/**
 * Dynamic attributes widget
 * 
 * @author Mickaël Chevalier
 */
public class DynamicAttributeWidget implements UIWidgetFactory<UIListCustomWidget> {

	@Override
	public UIListCustomWidget newInstance(final WidgetFactoryContext pContext) {
		return new UIListCustomWidget(pContext) {

			private String getMandatoryCheckboxName(final String pWidgetName) {
				return "mandatory_" + pWidgetName;
			}

			private String getCheckboxName(final String pWidgetName) {
				return "checkbox_" + pWidgetName;
			}

			@Override
			public void write(final WidgetWriterForList pWriter, final WidgetDisplayContext pContext) {
				if (pContext.isDisplayedInTable()) {
					pWriter.add("[see details]");
				} else {
					Optional<Adaptation> record = AdaptationUtils.getRecordForValueContext(pContext.getValueContext());
					AccessPermission permission = null;
					if (!record.isPresent()) {
						permission = pWriter.getSession().getPermissions().getNodeAccessPermission(pContext.getNode(), pContext.getValueContext().getAdaptationInstance());
					} else {
						permission = pWriter.getSession().getPermissions().getNodeAccessPermission(pContext.getNode(), record.get());
					}
					boolean disabled = !permission.isReadWrite();

					List<DynamicAttribute> attributes = (List<DynamicAttribute>) pContext.getValueContext().getValue();
					List<String> includedPaths = new ArrayList<>();
					List<String> mandatoryPaths = new ArrayList<>();
					for (DynamicAttribute attribute : attributes) {
						includedPaths.add(attribute.getField());
						if (attribute.getMandatory() != null && attribute.getMandatory()) {
							mandatoryPaths.add(attribute.getField());
						}
					}
					pWriter.add("<table>");
					List<String> enumeration = pContext.getNode().getNode(Path.parse("./field")).getEnumerationList(pContext.getValueContext());
					pWriter.add("<tr>");
					pWriter.add("<td>");
					pWriter.add("</td>");
					pWriter.add("<td>");
					pWriter.add("&nbsp;Included&nbsp;");
					pWriter.add("</td>");
					pWriter.add("<td>");
					pWriter.add("&nbsp;Mandatory&nbsp;");
					pWriter.add("</td>");
					pWriter.add("</tr>");
					for (String field : enumeration) {
						pWriter.add("<tr>");
						pWriter.add("<td>");
						pWriter.add(pContext.getNode().getNode(Path.parse("./field")).displayOccurrence(field, true, pContext.getValueContext(), pWriter.getLocale()));
						pWriter.add("</td>");
						pWriter.add("<td align=\"center\">");
						pWriter.add("<input type=\"checkbox\" name=\"" + this.getCheckboxName(pWriter.getWidgetName()) + "\" value=\"" + field + "\" "
								+ (includedPaths.contains(field) ? "checked" : "") + (disabled ? " disabled" : "") + "/>");
						pWriter.add("</td>");
						pWriter.add("<td align=\"center\">");
						pWriter.add("<input type=\"checkbox\" name=\"" + this.getMandatoryCheckboxName(pWriter.getWidgetName()) + "\" value=\"" + field + "\" "
								+ (mandatoryPaths.contains(field) ? "checked" : "") + (disabled ? " disabled" : "") + "/>");
						pWriter.add("</td>");
						pWriter.add("</tr>");
					}
					pWriter.add("</table>");
				}

			}

			@Override
			public void validate(final WidgetListValidationContext pContext) {
				super.validate(pContext);
				List<DynamicAttribute> value = new ArrayList<>();
				String[] checkedPaths = pContext.getOptionalRequestParameterValues(this.getCheckboxName(pContext.getWidgetName()));
				String[] mandatoryPathsArray = pContext.getOptionalRequestParameterValues(this.getMandatoryCheckboxName(pContext.getWidgetName()));
				List<String> mandatoryPaths = new ArrayList<>();
				if (mandatoryPathsArray != null) {
					mandatoryPaths = Arrays.asList(mandatoryPathsArray);
				}
				if (checkedPaths != null) {
					for (String checkedPath : checkedPaths) {
						DynamicAttribute attribute = new DynamicAttribute();
						attribute.setField(checkedPath);
						attribute.setMandatory(mandatoryPaths.contains(checkedPath));
						value.add(attribute);
					}
				}
				pContext.getValueContext().setNewValue(value);
			}
		};
	}

	@Override
	public void setup(final WidgetFactorySetupContext aContext) {
		// nothing to setup
	}

}
