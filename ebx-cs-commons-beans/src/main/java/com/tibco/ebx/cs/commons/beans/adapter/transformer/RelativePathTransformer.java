
package com.tibco.ebx.cs.commons.beans.adapter.transformer;

import java.util.function.Function;

import com.orchestranetworks.schema.Path;

/**
 * Transformer function from {@code String} to {@code Path} which prefixes the <br>
 * returned path with {@link Path#SELF} to make it relative.
 * <p>
 * <b>Note: </b> the default (implicit) transformer from {@code String} to <br>
 * {@code Path} just calls {@link Path#parse(String)}
 * </p>
 * 
 * 
 * @author Gilles Mayer
 */
public class RelativePathTransformer implements Function<String, Path> {
	@Override
	public Path apply(final String t) {
		return t == null ? null : Path.SELF.add(t);
	}
}
