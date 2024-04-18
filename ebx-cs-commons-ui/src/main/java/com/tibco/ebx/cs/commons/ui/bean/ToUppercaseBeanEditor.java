package com.tibco.ebx.cs.commons.ui.bean;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIResponseContext;
import com.orchestranetworks.ui.base.JsFunctionCall;
import com.orchestranetworks.ui.form.widget.UITextBox;
import com.orchestranetworks.ui.form.widget.UIWidget;

/**
 * UI Bean to upper case value of a text field when the focus gets out of the field. <br>
 * The js function uses the path field so it can be used in several field of a single table without problem.<br>
 * <br>
 * Methods {@link #addForDisplay(UIResponseContext)} and {@link #addForDisplayInTable(UIResponseContext)} are overriden to add the best matching widget with
 * {@link UIResponseContext#newBestMatching(Path)} <br>
 * <br>
 * Methods {@link #addForEdit(UIResponseContext)} and {@link #addForEditInTable(UIResponseContext)} are overriden to implement the ToUpperCase if datatype is {@link SchemaTypeName#XS_STRING} or
 * {@link SchemaTypeName#OSD_TEXT}; else the best matching is used.
 *
 * @author Lionel Luquet
 * @since 1.5.0
 */
@SuppressWarnings({ "deprecation", "javadoc" }) // TODO Update code to non-deprecated stuff
public class ToUppercaseBeanEditor extends UIBeanEditor {

	private static final String JS_FUNCNAME = "toUpperCaseBeanEditorFunction";

	@Override
	public void addForDisplay(final UIResponseContext response) {
		UIWidget newBestMatching = response.newBestMatching(Path.SELF);
		response.addWidget(newBestMatching);
	}

	@Override
	public void addForDisplayInTable(final UIResponseContext response) {
		this.addForDisplay(response);
	}

	@Override
	public void addForEdit(final UIResponseContext response) {
		SchemaNode node = response.getNode();

		if (node.getXsTypeName().equals(SchemaTypeName.OSD_TEXT) || node.getXsTypeName().equals(SchemaTypeName.XS_STRING)) {

			UITextBox widget = response.newTextBox(Path.SELF);

			response.addJS_cr();
			response.addJS_cr("function " + ToUppercaseBeanEditor.JS_FUNCNAME + "(myValue, pathField){");
			response.addJS_cr();
			response.addJS_cr("   var myValueUppercase = myValue.toUpperCase();");
			response.addJS_cr("   if (myValueUppercase !== myValue){");
			response.addJS_cr("      ebx_form_setValue(pathField, myValueUppercase);");
			response.addJS_cr("   };");
			response.addJS_cr("}");
			response.addJS_cr();

			widget.setActionOnAfterValueChanged(JsFunctionCall.on(ToUppercaseBeanEditor.JS_FUNCNAME, node.getPathInAdaptation().format()));
			response.addWidget(widget);
		} else {
			this.addForDisplay(response);
		}

	}

	@Override
	public void addForEditInTable(final UIResponseContext response) {
		this.addForEdit(response);
	}

}
