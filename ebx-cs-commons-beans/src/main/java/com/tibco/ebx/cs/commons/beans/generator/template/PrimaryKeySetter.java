/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.generator.template;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Object representing a setter method to set a primary key in a bean. It references the method name and the potential bean class to be used as parameter in case the PK if an FK.
 *
 * @author Mickaël Chevalier
 * @since 2.0.12
 */
public class PrimaryKeySetter {

	private final Method setter;
	private final Optional<Class<? extends TableBean>> bean;

	/**
	 * Only constructor setting final variables
	 *
	 * @param pSetter Method to set a primary key
	 * @param pBean   Bean class to pass as parameter in case PK is an FK.
	 */
	public PrimaryKeySetter(final Method pSetter, final Optional<Class<? extends TableBean>> pBean) {
		super();
		this.setter = pSetter;
		this.bean = pBean;
	}

	public Method getSetter() {
		return this.setter;
	}

	public Optional<Class<? extends TableBean>> getBean() {
		return this.bean;
	}

}
