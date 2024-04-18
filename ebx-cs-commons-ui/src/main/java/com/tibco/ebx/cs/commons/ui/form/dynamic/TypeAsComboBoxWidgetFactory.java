package com.tibco.ebx.cs.commons.ui.form.dynamic;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.base.JsFunctionCall;
import com.orchestranetworks.ui.form.widget.UIComboBox;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Type as a combo WidgetFactory
 * 
 * @author Mickaël Chevalier
 */
public class TypeAsComboBoxWidgetFactory implements UIWidgetFactory<UIWidget> {

	/**
	 * Type as a combo Widget
	 * 
	 * @author Mickaël Chevalier
	 */
	public class ComboBoxTypeWidget extends UISimpleCustomWidget {

		/**
		 * Constructor
		 * 
		 * @param pContext WidgetFactoryContext
		 */
		public ComboBoxTypeWidget(final WidgetFactoryContext pContext) {
			super(pContext);
		}

		@Override
		public void write(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
			UIComboBox widget = pWriter.newComboBox(Path.SELF);
			widget.setActionOnAfterValueChanged(JsFunctionCall.on("refreshForm"));
			pWriter.addWidget(widget);
		}
	}

	@Override
	public UIWidget newInstance(final WidgetFactoryContext pContext) {
		return new ComboBoxTypeWidget(pContext);
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		// nothing to setup
	}
}
