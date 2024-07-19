/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.widget;

import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.service.UserReference;
import com.orchestranetworks.service.directory.DirectoryHandler;
import com.orchestranetworks.ui.form.widget.UICustomWidget;
import com.orchestranetworks.ui.form.widget.UIWidgetFactory;
import com.orchestranetworks.ui.form.widget.WidgetFactoryContext;
import com.orchestranetworks.ui.form.widget.WidgetFactorySetupContext;
import com.tibco.ebx.cs.commons.lib.utils.CommonsConstants;

/**
 * A base widget factory that other widget factories can extend from in order to inherit some commonly used functionality. Currently this includes the ability to make the editor read only, and allow
 * for certain roles to ignore the read only setting. It also allows you to specify that it never validates the input.
 *
 * By default, this is editable and does not offer any different behavior unless the various parameters are set.
 * 
 * @author Lionel Luquet
 *
 * @param <T> UI custom Widget
 */
public class BaseUICustomWidgetFactory<T extends UICustomWidget> implements UIWidgetFactory<T> {
	private static final String EDITOR_ROLES_SEPARATOR = ",";

	private boolean readOnly;
	private boolean neverEditable;
	private String editorRoles = this.getDefaultEditorRoles();
	private boolean neverValidate;

	protected boolean listWidget;

	/**
	 * Constructor
	 */
	public BaseUICustomWidgetFactory() {
		super();
	}

	@Override
	public T newInstance(final WidgetFactoryContext context) {
		// Rather than have two separate factories, this is one factory that will return
		// either a list widget or simple widget based on this flag.
		if (this.listWidget) {
			return (T) new BaseUIListCustomWidget(context, this);
		}
		return (T) new BaseUISimpleCustomWidget(context, this);
	}

	@Override
	public void setup(final WidgetFactorySetupContext context) {
		if (!this.readOnly && this.neverEditable) {
			context.addError("neverEditable can only be true when the editor is read only.");
		}
		this.listWidget = context.getSchemaNode().getMaxOccurs() > 1;
	}

	/**
	 * Get whether the user belongs to one of the editor roles specified
	 *
	 * @param session the session
	 * @return whether the user belongs to any of the roles
	 */
	protected boolean isUserInEditorRoles(final Session session) {
		if (this.editorRoles != null) {
			DirectoryHandler dirHandler = session.getDirectory();
			UserReference user = session.getUserReference();
			String[] editorRolesToUseArr = this.editorRoles.split(EDITOR_ROLES_SEPARATOR);
			for (String editorRole : editorRolesToUseArr) {
				String roleName = editorRole.trim();
				if (!"".equals(roleName)) {
					Role role = "administrator".equals(roleName) ? Profile.ADMINISTRATOR : Profile.forSpecificRole(roleName);
					if (dirHandler.isUserInRole(user, role)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Define the default roles to use if none are configured on the bean. By default, it is Tech Admin but this can be overwritten by subclasses.
	 */
	protected String getDefaultEditorRoles() {
		return CommonsConstants.ROLE_TECH_ADMIN;
	}

	/**
	 * Whether to really validate the widget. This is called by both the list and simple widgets.
	 *
	 * @return whether to validate
	 */
	protected boolean shouldValidateWidget() {
		// Only validate if it's not set to never validate and it's not never editable
		return !this.isNeverValidate() && !this.isNeverEditable();
	}

	/**
	 * @see {@link #setReadOnly(boolean)}
	 */
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * Set whether the widget is in read only mode
	 *
	 * @param readOnly whether the widget is in read only mode
	 */
	public void setReadOnly(final boolean readOnly) {
		this.readOnly = readOnly;
	}

	/**
	 * @see {@link #setNeverEditable(boolean)}
	 */
	public boolean isNeverEditable() {
		return this.neverEditable;
	}

	/**
	 * Set whether this is never editable. If never editable, no one can edit it even if their roles are specified as editable. Only applicable when <code>readOnly</code> is <code>true</code>.
	 *
	 * @param neverEditable whether the widget is never editable
	 */
	public void setNeverEditable(final boolean neverEditable) {
		this.neverEditable = neverEditable;
	}

	/**
	 * @see {@link #setEditorRoles(String)}
	 */
	public String getEditorRoles() {
		return this.editorRoles;
	}

	/**
	 * Set a comma-separated list of roles that can edit, if the <code>readOnly</code> is <code>true</code> and <code>neverEditable</code> is <code>false</code>.
	 *
	 * @param editorRoles the roles
	 */
	public void setEditorRoles(final String editorRoles) {
		this.editorRoles = editorRoles;
	}

	/**
	 * @see {@link #setNeverValidate(boolean)}
	 */
	public boolean isNeverValidate() {
		return this.neverValidate;
	}

	/**
	 * Set whether this never validates. By default, the editor doesn't validate when it's read only and otherwise it validates. This will allow you to specify that it should never validate regardless
	 * of if it's editable.
	 *
	 * @param neverValidate whether this never validates
	 */
	public void setNeverValidate(final boolean neverValidate) {
		this.neverValidate = neverValidate;
	}
}
