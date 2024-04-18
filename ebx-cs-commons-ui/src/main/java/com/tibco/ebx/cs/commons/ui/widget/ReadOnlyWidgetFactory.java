package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Widget setting the editor as disabled. Helpful when used programatically.
 *
 * @author Aurélien Ticot
 * @since 1.8.0
 */
public class ReadOnlyWidgetFactory implements UIWidgetFactory<UISimpleCustomWidget> {
	@Override
	public UISimpleCustomWidget newInstance(final WidgetFactoryContext pWidgetFactoryContext) {
		return new UISimpleCustomWidget(pWidgetFactoryContext) {
			@Override
			public void write(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
				UIWidget widget = pWriter.newBestMatching(Path.SELF);
				widget.setEditorDisabled(true);
				pWriter.addWidget(widget);
			}
		};
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		// Nothing to setup
	}
}