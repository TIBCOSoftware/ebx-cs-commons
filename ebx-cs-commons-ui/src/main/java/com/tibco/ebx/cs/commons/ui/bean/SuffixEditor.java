package com.tibco.ebx.cs.commons.ui.bean;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIResponseContext;

/**
 * Add a suffix to value display and edition.
 * 
 * <pre>
 * {@code
 *         <osd:uiBean class="com.orchestranetworks.ps.ui.bean.SetValueEditor">
 *         	<suffix>...</suffix>
 *         </osd:uiBean>
 *}
 * 
 * @author Mickaël Chevalier
 */
public class SuffixEditor extends UIBeanEditor {
	private String suffix;

	/**
	 * Constructor
	 */
	public SuffixEditor() {
		super();
	}

	@Override
	public void addForDisplay(final UIResponseContext pContext) {
		pContext.addUIBestMatchingComponent(Path.SELF, "");
		this.addSuffix(pContext);
	}

	@Override
	public void addForEdit(final UIResponseContext pContext) {
		pContext.addUIBestMatchingComponent(Path.SELF, "");
		this.addSuffix(pContext);
	}

	private void addSuffix(final UIResponseContext pContext) {
		pContext.add("<span style=\"margin-left:5px;\">" + this.suffix + "</span>");
	}

	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(final String suffix) {
		this.suffix = suffix;
	}

}
