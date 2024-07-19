/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.form;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;
import com.orchestranetworks.ui.form.UIFormContext;
import com.orchestranetworks.ui.form.UIFormPane;
import com.orchestranetworks.ui.form.UIFormPaneWriter;

/**
 * Define a UIFormPane with an association table.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class UIFormAssociationPane implements UIFormPane {
	private final Path path;

	/**
	 * Constructor
	 * 
	 * @param path the path of the association node.
	 * @since 1.0.0
	 */
	public UIFormAssociationPane(final Path path) {
		super();
		this.path = path;
	}

	/**
	 * Constructor
	 * 
	 * @param node the node of the association.
	 * @since 1.0.0
	 */
	public UIFormAssociationPane(final SchemaNode node) {
		super();
		if (node != null) {
			this.path = Path.SELF.add(node.getPathInAdaptation());
		} else {
			this.path = null;
		}
	}

	@Override
	public void writePane(final UIFormPaneWriter pWriter, final UIFormContext pContext) {
		if (this.path != null) {
			pWriter.addWidget(this.path);
		}
	}
}
