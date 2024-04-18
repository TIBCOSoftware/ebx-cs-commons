package com.tibco.ebx.cs.commons.ui.widget;

import java.util.Locale;

import com.onwbp.base.text.Nomenclature;
import com.onwbp.base.text.NomenclatureItem;
import com.orchestranetworks.instance.ValueContextForInputValidation;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetValidationContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Widget to display a list of possible values as clickable labels. Values have to be defined in the enumeration property of the field in the datamodel. Widget is authorized for maximum 5 values to
 * display. Colors of borders and fonts when value is selected and unselected can be configured with HTML style value compliant (red, green, #C0C0C0...)
 *
 * @author Lionel Luquet
 * @since 1.9.0
 */
public class BarSelectorMonoValueWidget extends UISimpleCustomWidget {
	private static final String ID_BAR_SELECTOR = "bar-selector-";

	private static final String OPTION_ELEMENT_NAME = "input-field-id";
	private static final String ON_CHANGE_FUNCTION_NAME = "onGroupValueChange";

	private static final String STYLE_BAR_SELECTOR_LABEL_FIRST = "border-top-left-radius: 6px; border-bottom-left-radius: 6px;";
	private static final String STYLE_BAR_SELECTOR_LABEL_LAST = "border-top-right-radius: 6px; border-bottom-right-radius: 6px; border-right-width:1px;";
	private final static String STYLE_BAR_SELECTOR_INPUT = "display: none;";
	private int listSize;
	private final String unSelectedBorderColor;

	private final String selectedBorderColor;
	private final String unSelectedFontColor;
	private final String selectedFontColor;
	private String styleBarSelector = "";

	private String styleBarSelectorLabel = "display: inline-block; background: white; padding: 2px 5px;cursor: pointer;";

	private String styleBarSelectorLabelSelected = "background-origin: width; padding: 2px 4px;";

	private String styleBarSelectorLabelPreviousSelected = "";
	private String styleBarSelectorLabelNextSelected = "padding-left: 7px;";

	/**
	 * @param pContext               the WidgetFactoryContext
	 * @param pSelectedBorderColor   the selected border color
	 * @param pUnSelectedBorderColor the unselected border color
	 * @param pUnSelectedFontColor   the un selected font color
	 * @param pSelectedFontColor     the selected font color
	 * @since TOCO since
	 */
	public BarSelectorMonoValueWidget(final WidgetFactoryContext pContext, final String pUnSelectedBorderColor, final String pSelectedBorderColor, final String pUnSelectedFontColor,
			final String pSelectedFontColor) {
		super(pContext);
		this.selectedBorderColor = pSelectedBorderColor;
		this.unSelectedBorderColor = pUnSelectedBorderColor;

		this.unSelectedFontColor = pUnSelectedFontColor;
		this.selectedFontColor = pSelectedFontColor;

		this.styleBarSelectorLabel += " border: 1px solid " + this.unSelectedBorderColor + "; border-right-width: 0;";

		this.styleBarSelectorLabelSelected += " border-color: " + this.selectedBorderColor + "; border-width: 2px; color: " + this.selectedFontColor + ";";

		this.styleBarSelectorLabelPreviousSelected = " border-right-color: " + this.selectedBorderColor + ";";
		this.styleBarSelectorLabelNextSelected += "border-left-color: " + this.selectedBorderColor + "; border-left-width: 0;";

		this.styleBarSelector = "color: " + this.unSelectedFontColor + ";";
	}

	@Override
	public void validate(final WidgetValidationContext pContext) {
		ValueContextForInputValidation valueContext = pContext.getValueContext();
		SchemaTypeName xsTypeName = valueContext.getNode().getXsTypeName();
		String valueString = pContext.getOptionalRequestParameterValue(BarSelectorMonoValueWidget.OPTION_ELEMENT_NAME);

		Object value = BarSelectorMonoValueWidgetFactory.getObjectValue(xsTypeName, valueString);
		valueContext.setNewValue(value);
	}

	@Override
	public void write(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
		if (pContext.isDisplayedInTable() || !pContext.getPermission(Path.SELF).isReadWrite()) {
			pWriter.addWidget(Path.SELF);
			return;
		}

		this.defineJS(pWriter, pContext);

		Locale locale = pWriter.getLocale();

		Nomenclature<Object> nomenclature = pContext.getNode().getEnumerationNomenclature(pContext.getValueContext());

		String idBarSelector = BarSelectorMonoValueWidget.generateHtmlWidgetId(pContext.getNode().getPathInAdaptation().format());

		pWriter.add("<div").addSafeAttribute("style", this.styleBarSelector).addSafeAttribute("id", idBarSelector).add(">");

		this.listSize = nomenclature.getSize();
		Object valueSelected = pContext.getValueContext().getValue();
		Integer itemSelected = null;
		for (int i = 0; i <= this.listSize - 1; i++) {
			if (nomenclature.getItems(i).getValue().equals(valueSelected)) {
				itemSelected = i;
				break;
			}
		}

		for (int i = 0; i <= this.listSize - 1; i++) {
			String additionalStyle = "";
			if (i == 0) {
				additionalStyle += BarSelectorMonoValueWidget.STYLE_BAR_SELECTOR_LABEL_FIRST + " ";
			} else if (i == this.listSize - 1) {
				additionalStyle += BarSelectorMonoValueWidget.STYLE_BAR_SELECTOR_LABEL_LAST + " ";
			}

			if (itemSelected != null && i == itemSelected.intValue() - 1) {
				additionalStyle += this.styleBarSelectorLabelPreviousSelected;
			} else if (itemSelected != null && i == itemSelected.intValue() + 1) {
				additionalStyle += this.styleBarSelectorLabelNextSelected;
			} else if (itemSelected != null && i == itemSelected.intValue()) {
				additionalStyle += this.styleBarSelectorLabelSelected;
			}

			NomenclatureItem<Object> item = nomenclature.getItems(i);
			String label = item.getLabel(locale);
			if (label == null) {
				label = item.getDefaultLabel();
			}

			String stringValue = item.getValue().toString();

			pWriter.add("<input").addSafeAttribute("onchange", BarSelectorMonoValueWidget.ON_CHANGE_FUNCTION_NAME + "('" + idBarSelector + "', this)")
					.addSafeAttribute("style", BarSelectorMonoValueWidget.STYLE_BAR_SELECTOR_INPUT).addSafeAttribute("id", "option-" + i).addSafeAttribute("type", "checkbox")
					.addSafeAttribute("name", BarSelectorMonoValueWidget.OPTION_ELEMENT_NAME).addSafeAttribute("value", stringValue).add(">");
			pWriter.add("<label").addSafeAttribute("style", this.styleBarSelectorLabel + " " + additionalStyle).addSafeAttribute("for", "option-" + i).add(">");
			pWriter.add(label);
			pWriter.add("</label>");
		}

		pWriter.add("</div>");
	}

	private void defineJS(final WidgetWriter writer, final WidgetDisplayContext context) {
		writer.addJS("function onGroupValueChange(idBarSelector, valueClicked) {");
		writer.addJS("  var barSelector = document.getElementById(").addJS("idBarSelector").addJS(");");
		writer.addJS("  var radios = barSelector.getElementsByTagName('input');");
		writer.addJS("  var radioSelected = null;");
		writer.addJS("  for (var i = 0; i < radios.length; i++) {");
		writer.addJS("    var label = radios[i].nextSibling;");
		writer.addJS("    var additionalStyle = '';");
		writer.addJS("    if(i === 0) {");
		writer.addJS("      additionalStyle= '" + BarSelectorMonoValueWidget.STYLE_BAR_SELECTOR_LABEL_FIRST + "';");
		writer.addJS("    } else if (i === radios.length - 1) {");
		writer.addJS("      additionalStyle= '" + BarSelectorMonoValueWidget.STYLE_BAR_SELECTOR_LABEL_LAST + "';");
		writer.addJS("    }");
		writer.addJS("    label.setAttribute('style', '" + this.styleBarSelectorLabel + " '+additionalStyle);");
		writer.addJS("    if (radios[i].checked) {");
		writer.addJS("      if (radios[i].getAttribute('id') == valueClicked.getAttribute('id')){");
		writer.addJS("        radioSelected = radios[i];");
		writer.addJS("        var selectedValue =radios[i].value;");
		writer.addJS("      } else {");
		writer.addJS("        radios[i].checked= false;");
		writer.addJS("      }");
		writer.addJS("    }");
		writer.addJS("  }");

		writer.addJS("  if (radioSelected) {");
		writer.addJS("    radioSelected.nextSibling.setAttribute('style', radioSelected.nextSibling.getAttribute('style')+' " + this.styleBarSelectorLabelSelected + "');");
		writer.addJS("    if (radioSelected.previousSibling) {");
		writer.addJS("      radioSelected.previousSibling.setAttribute('style', radioSelected.previousSibling.getAttribute('style')+' " + this.styleBarSelectorLabelPreviousSelected + "');");
		writer.addJS("    }");
		writer.addJS("  if (radioSelected.nextSibling.nextSibling) {");
		writer.addJS("    radioSelected.nextSibling.nextSibling.nextSibling.setAttribute('style', radioSelected.nextSibling.nextSibling.nextSibling.getAttribute('style')+ ' "
				+ this.styleBarSelectorLabelNextSelected + "');");
		writer.addJS("    }");
		writer.addJS("  }");
		writer.addJS("};");
	}

	private static String generateHtmlWidgetId(final String path) {
		return BarSelectorMonoValueWidget.ID_BAR_SELECTOR + path.replace(Path.ROOT.format(), "_");
	}
}
