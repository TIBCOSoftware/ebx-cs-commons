/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.ui.form.widget.UIListCustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidget;
import com.orchestranetworks.ui.form.widget.WidgetDisplayContext;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetListValidationContext;
import com.orchestranetworks.ui.form.widget.WidgetWriter;
import com.orchestranetworks.ui.form.widget.WidgetWriterForList;

/**
 * A base list widget that can be put on multi-occurring fields that want to potentially use some of the features of the {@link BaseUICustomWidgetFactory}.
 * 
 * @author Lionel Luquet
 */
public class BaseUIListCustomWidget extends UIListCustomWidget {
	protected final BaseUICustomWidgetFactory<?> factory;

	/**
	 * Create the widget
	 * 
	 * @param context the context
	 * @param factory the factory that's creating it
	 */
	public BaseUIListCustomWidget(final WidgetFactoryContext context, final BaseUICustomWidgetFactory<?> factory) {
		super(context);
		this.factory = factory;
	}

	@Override
	public void validate(final WidgetListValidationContext validationContext) {
		if (factory.shouldValidateWidget()) {
			super.validate(validationContext);
		}
	}

	/**
	 * Determine if the editor should be read-only
	 * 
	 * @param writer  the writer
	 * @param context the context
	 * @return whether the editor should be read-only
	 */
	protected boolean isReadOnly(final WidgetWriter writer, final WidgetDisplayContext context) {
		if (!factory.isReadOnly()) {
			return false;
		}
		return factory.isNeverEditable() || !context.getPermission().isReadWrite() || !isUserAlwaysReadWrite(writer.getSession());
	}

	/**
	 * Checks if a user is always read/write, based on the specified <code>editorRoles</code>
	 *
	 * @param session the session
	 * @return whether the user is always read/write
	 */
	protected boolean isUserAlwaysReadWrite(final Session session) {
		return factory.isUserInEditorRoles(session);
	}

	/**
	 * Adds the editor when it's for display or when it's read-only. Adds the default editor in disabled mode unless overridden.
	 */
	protected void addForDisplayOrReadOnly(final WidgetWriterForList writer) {
		UIWidget widget = writer.newBestMatching(Path.SELF);
		widget.setEditorDisabled(true);
		writer.addWidget(widget);
	}

	/**
	 * Adds the editor when it's editable. Adds the default editor unless overridden.
	 */
	protected void addForEdit(final WidgetWriterForList writer, final WidgetDisplayContext context) {
		writer.addWidget(Path.SELF);
	}

	@Override
	public void write(final WidgetWriterForList writer, final WidgetDisplayContext context) {
		if (context.isDisplayedInTable() || isReadOnly(writer, context)) {
			addForDisplayOrReadOnly(writer);
		} else {
			addForEdit(writer, context);
		}
	}
}
