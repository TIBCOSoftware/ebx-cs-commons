package com.tibco.ebx.cs.commons.ui.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIRequestContext;
import com.orchestranetworks.ui.UIResponseContext;

/**
 * Fix a multi-valued field to a given number of elements.
 * 
 * <pre>
 * {@code
 *  	<osd:uiBean class="com.orchestranetworks.ps.ui.bean.FixedListEditor">
 *         	<numberOfElements>...</numberOfElements>
 *  	</osd:uiBean>
 *  }
 * </pre>
 * 
 * @author Mickaël Chevalier
 * 
 */
public class FixedListEditor extends UIBeanEditor {

	/**
	 * Constructor
	 */
	public FixedListEditor() {
		super();
	}

	private int numberOfElements;

	@Override
	public void addForDisplay(final UIResponseContext pContext) {
		pContext.addUIBestMatchingComponent(Path.SELF, "");
	}

	@Override
	public void addForEdit(final UIResponseContext pContext) {
		pContext.addUIBestMatchingComponent(Path.SELF, "");
	}

	@Override
	public void addList(final UIResponseContext pContext) {
		for (int i = 0; i < this.numberOfElements; i++) {
			pContext.addUIBestMatchingComponent(Path.PARENT.add(pContext.getPathInAdaptation().addIndex(i)), "");
		}
	}

	@Override
	public void validateInputList(final UIRequestContext pContext) {
		List<Object> list = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			Object value = pContext.getValue(Path.PARENT.add(pContext.getPathInAdaptation().addIndex(i)));
			list.add(value);
		}
		Collections.reverse(list);
		pContext.getValueContext().setNewValue(list);
	}
}
