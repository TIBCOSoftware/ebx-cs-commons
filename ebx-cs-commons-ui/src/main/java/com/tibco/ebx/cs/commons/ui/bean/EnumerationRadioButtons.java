package com.tibco.ebx.cs.commons.ui.bean;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIResponseContext;

/**
 * UI Bean to display radio buttons for enumerations.<br>
 *
 * @deprecated Use the native RadioButton widget available in the DMA.
 * @author Aurélien Ticot
 * @since 1.0.0
 */
@Deprecated
public final class EnumerationRadioButtons extends UIBeanEditor {
	@Override
	public void addForDisplay(final UIResponseContext pContext) {
		pContext.addWidget(Path.SELF);
	}

	@Override
	public void addForDisplayInTable(final UIResponseContext pContext) {
		pContext.addWidget(Path.SELF);
	}

	@Override
	public void addForEdit(final UIResponseContext pContext) {
		pContext.addWidget(pContext.newRadioButtonGroup(Path.SELF));
	}
}