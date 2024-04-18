package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.instance.HomeKey;

/**
 * Transformer function from {@code String} to {@code HomeKey} when the input<br>
 * value is a {@link HomeKey#forVersionName(String) snapshot name}.
 * <p>
 * <b>Note: </b> the default (implicit) transformer from {@code String} to <br>
 * {@code HomeKey} is {@link HomeKeyTransformer}
 * </p>
 * 
 * @see BranchNameTransformer
 * 
 * @author Gilles Mayer
 */
public class VersionNameTransformer implements Function<String, HomeKey> {
	@Override
	public HomeKey apply(final String t) {
		return t == null ? null : HomeKey.forVersionName(t);
	}
}
