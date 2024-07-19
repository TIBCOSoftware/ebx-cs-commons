/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */

package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.instance.HomeKey;

/**
 * Transformer function from {@code String} to {@code HomeKey} when the input <br>
 * value is a {@link HomeKey#forVersionName(String) dataspace name}.<br>
 * <p>
 * <b>Note: </b> the default (implicit) transformer from {@code String} to {@code HomeKey} is {@link HomeKeyTransformer}
 * </p>
 * 
 * @see VersionNameTransformer
 * 
 * @author Gilles Mayer
 */
public class BranchNameTransformer implements Function<String, HomeKey> {
	@Override
	public HomeKey apply(final String t) {
		return HomeKey.forBranchName(t);
	}
}
