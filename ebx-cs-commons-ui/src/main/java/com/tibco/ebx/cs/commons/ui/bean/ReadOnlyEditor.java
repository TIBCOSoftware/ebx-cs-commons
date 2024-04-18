package com.tibco.ebx.cs.commons.ui.bean;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIResponseContext;

/**
 * @author Mickaël Chevalier
 * 
 *         Replace the editor by the default component of display.
 * 
 *         <pre>
 * {@code
 *  	<osd:uiBean class="com.orchestranetworks.ps.ui.bean.ReadOnlyEditor">
 *         	<permissiveRole>...</permissiveRole>
 *  	</osd:uiBean>
 *  }
 * </pre>
 */
public class ReadOnlyEditor extends UIBeanEditor {

	/** The role giving permission despite the rule */
	private String permissiveRole;

	/*
	 * @see com.orchestranetworks.ui.UIBeanEditor#addForDisplay(com.orchestranetworks.ui.UIResponseContext)
	 */
	@Override
	public void addForDisplay(final UIResponseContext context) {
		context.addUIBestMatchingComponent(Path.SELF, "");
	}

	/*
	 * @see com.orchestranetworks.ui.UIBeanEditor#addForDisplayInTable(com.orchestranetworks.ui.UIResponseContext)
	 */
	@Override
	public void addForDisplayInTable(final UIResponseContext context) {
		context.addUIBestMatchingComponent(Path.SELF, "");
	}

	/*
	 * @see com.orchestranetworks.ui.UIBeanEditor#addForEdit(com.orchestranetworks.ui.UIResponseContext)
	 */
	@Override
	public void addForEdit(final UIResponseContext pContext) {
		if (pContext.getSession().isUserInRole(Profile.forSpecificRole(this.permissiveRole))) {
			pContext.addUIBestMatchingComponent(Path.SELF, "");
			return;
		}
		pContext.addUIBestMatchingDisplay(Path.SELF, "");
	}

	/**
	 * Gets the permissive role.
	 *
	 * @return the permissive role
	 */
	public String getPermissiveRole() {
		return this.permissiveRole;
	}

	/**
	 * Sets the permissive role.
	 *
	 * @param permissiveRole the new permissive role
	 */
	public void setPermissiveRole(final String permissiveRole) {
		this.permissiveRole = permissiveRole;
	}

}
