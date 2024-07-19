/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */

package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.instance.HomeKey;

/**
 * Transformer function from {@code String} to {@code HomeKey} when the input<br>
 * value is a {@link HomeKey#parse(String) home identifier}.
 * <p>
 * <b>Note: </b> this is the default (implicit) transformer from {@code String}<br>
 * to {@code HomeKey} and should not be referenced from client code
 * </p>
 * 
 * @see BranchNameTransformer
 * @see VersionNameTransformer
 * 
 * @author Gilles Mayer
 */
public class HomeKeyTransformer implements Function<String, HomeKey> {
	@Override
	public HomeKey apply(final String t) {
		return t == null ? null : HomeKey.parse(t);
	}
}
