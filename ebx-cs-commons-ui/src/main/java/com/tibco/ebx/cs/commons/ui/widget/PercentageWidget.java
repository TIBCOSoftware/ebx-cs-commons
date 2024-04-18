package com.tibco.ebx.cs.commons.ui.widget;

import java.math.BigDecimal;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Widget displaying the default widget for edition and a percentage for display.
 *
 * @author Mickaël Chevalier
 * @since 2.0.6
 */
public class PercentageWidget extends UISimpleCustomWidget {

	/**
	 * Constructor
	 * 
	 * @param pContext WidgetFactoryContext
	 */
	public PercentageWidget(final WidgetFactoryContext pContext) {
		super(pContext);
	}

	@Override
	public void write(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
		if (this.isEditorDisabled() && pContext.getValueContext().getValue() != null) {
			double percentage = ((BigDecimal) pContext.getValueContext().getValue()).doubleValue() * 100;
			pWriter.add("<label>" + Math.round(percentage) + " %</label>");
		} else {
			pWriter.addWidget(Path.SELF);
		}
	}
}
