package com.tibco.ebx.cs.commons.ui.form.dynamic;

/**
 * Dynamic attribute
 * 
 * @author Mickaël Chevalier
 */
public class DynamicAttribute {

	private String field;
	private Boolean mandatory;

	/**
	 * Constructor
	 */
	public DynamicAttribute() {
		super();
	}

	public String getField() {
		return this.field;
	}

	public void setField(final String field) {
		this.field = field;
	}

	public Boolean getMandatory() {
		return this.mandatory;
	}

	public void setMandatory(final Boolean mandatory) {
		this.mandatory = mandatory;
	}

}
