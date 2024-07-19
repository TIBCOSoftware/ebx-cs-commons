/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */

package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.schema.SchemaLocation;

/**
 * 
 * @author Gilles Mayer
 */
public class SchemaLocationTransformer implements Function<String, SchemaLocation> {
	@Override
	public SchemaLocation apply(final String t) {
		return t == null ? null : SchemaLocation.parse(t);
	}
}
