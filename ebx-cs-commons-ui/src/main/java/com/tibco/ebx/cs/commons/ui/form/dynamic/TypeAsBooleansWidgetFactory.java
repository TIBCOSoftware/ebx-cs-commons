package com.tibco.ebx.cs.commons.ui.form.dynamic;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.ui.form.widget.UISimpleCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.orchestranetworks.ui.form.widget.WidgetValidationContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;

/**
 * Type as boolean WidgetFactory
 * 
 * @author Mickaël Chevalier
 */
public class TypeAsBooleansWidgetFactory implements UIWidgetFactory<UIWidget> {

	/**
	 * Type as boolean Widget
	 * 
	 * @author Mickaël Chevalier
	 *
	 */
	public class TypeAsBooleansWidget extends UISimpleCustomWidget {
		private static final String ILLEGAL_STATE_MESSAGE = "Type should be a complex type composed by boolean fields";

		/**
		 * Constructor
		 * 
		 * @param pContext WidgetFactoryContext
		 */
		public TypeAsBooleansWidget(final WidgetFactoryContext pContext) {
			super(pContext);
		}

		@Override
		public void validate(final WidgetValidationContext pContext) {
			SchemaNode[] nodes = pContext.getNode(Path.SELF).getNodeChildren();
			if (nodes.length == 0) {
				throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
			}

			for (SchemaNode node : nodes) {
				if (!node.isTerminalValueDescendant() || !node.getXsTypeName().equals(SchemaTypeName.XS_BOOLEAN)) {
					throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
				}

				String value = pContext.getOptionalRequestParameterValue(node.getPathInAdaptation().getLastStep().format());
				if (value == null || "null".equals(value)) {
					pContext.getValueContext(Path.SELF.add(node.getPathInAdaptation().getLastStep())).setNewValue(Boolean.FALSE);
				} else {
					pContext.getValueContext(Path.SELF.add(node.getPathInAdaptation().getLastStep())).setNewValue(Boolean.TRUE);
				}

			}
		}

		@Override
		public void write(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
			if (pContext.isDisplayedInTable()) {
				this.writeInTable(pWriter, pContext);
			}
			SchemaNode[] nodes = pContext.getNode().getNodeChildren();
			if (nodes.length == 0) {
				throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
			}

			pWriter.add("<ul style='padding:0'>");

			for (SchemaNode node : nodes) {
				if (!node.isTerminalValueDescendant() || !SchemaTypeName.XS_BOOLEAN.equals(node.getXsTypeName())) {
					throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
				}

				String name = node.getPathInSchema().getLastStep().format();
				pWriter.add("<li style='list-style-type:none;'>");
				pWriter.add("<input onchange='refreshForm();' type='checkbox' name='" + name + "' id='" + name + "'");
				if (((Boolean) pContext.getValueContext().getValue(Path.SELF.add(node.getPathInSchema().getLastStep()))).booleanValue()) {
					pWriter.add(" checked");
				}
				pWriter.add(">");
				pWriter.add("<label for='" + name + "'>" + node.getLabel(pWriter.getLocale()) + "</label>");
				pWriter.add("</li>");
			}

			pWriter.add("</ul>");

		}

		private void writeInTable(final WidgetWriter pWriter, final WidgetDisplayContext pContext) {
			StringBuilder display = new StringBuilder();
			SchemaNode[] nodes = pContext.getNode().getNodeChildren();
			if (nodes.length == 0) {
				throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
			}

			for (SchemaNode node : nodes) {
				if (!node.isTerminalValueDescendant() || !SchemaTypeName.XS_BOOLEAN.equals(node.getXsTypeName())) {
					throw new IllegalStateException(ILLEGAL_STATE_MESSAGE);
				}

				if (((Boolean) pContext.getValueContext().getValue(Path.SELF.add(node.getPathInSchema().getLastStep()))).booleanValue()) {
					display.append(node.getLabel(pWriter.getLocale()) + ", ");
				}
			}

			if (display.length() != 0) {
				display.delete(display.length() - 2, display.length());
			}
			pWriter.add(display.toString());

		}

	}

	@Override
	public UIWidget newInstance(final WidgetFactoryContext pContext) {
		return new TypeAsBooleansWidget(pContext);
	}

	@Override
	public void setup(final WidgetFactorySetupContext pContext) {
		// nothing to setup
	}

}
