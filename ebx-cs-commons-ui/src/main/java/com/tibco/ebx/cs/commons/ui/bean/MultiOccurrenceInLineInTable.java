package com.tibco.ebx.cs.commons.ui.bean;

import java.util.List;

import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.ui.UIBeanEditor;
import com.orchestranetworks.ui.UIResponseContext;

/**
 * UI Bean to display the values of a multi-occurence field in table. The separator can be defined as parameter in the data model.<br>
 *
 * @author Aurélien Ticot
 * @since 1.3.0
 */
@SuppressWarnings("deprecation") // TODO Update code to non-deprecated stuff
public class MultiOccurrenceInLineInTable extends UIBeanEditor {
	private String separator = ",";

	@Override
	public void addForDisplay(final UIResponseContext pContext) {
		pContext.addWidget(Path.SELF);
	}

	@Override
	public void addForDisplayInTable(final UIResponseContext pContext) {
		ValueContext valueContext = pContext.getValueContext();

		@SuppressWarnings("unchecked")
		List<String> values = (List<String>) valueContext.getValue(Path.SELF);

		StringBuilder concatValue = new StringBuilder("");
		if (values != null) {
			int nbValues = values.size();

			for (int i = 0; i < nbValues; i++) {
				if (i != 0) {
					concatValue.append(this.separator + " ");
				}
				concatValue.append(values.get(i));
			}
		}
		pContext.add(concatValue.toString());
	}

	@Override
	public void addForEdit(final UIResponseContext pContext) {
		pContext.addWidget(Path.SELF);
	}

	/**
	 * Get the separator character
	 *
	 * @return the separator character.
	 * @since 1.3.0
	 */
	public String getSeparator() {
		return this.separator;
	}

	/**
	 * Set the separator character
	 *
	 * @param separator the separator character.
	 * @since 1.3.0
	 */
	public void setSeparator(final String separator) {
		this.separator = separator;
	}
}
