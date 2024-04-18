package com.tibco.ebx.cs.commons.beans.adapter.adaptation;

import java.util.function.Function;

import com.onwbp.adaptation.Adaptation;
import com.orchestranetworks.schema.Path;

public final class Mappers {
	@SuppressWarnings("unchecked")
	public static <T> Function<Adaptation, T> select(final Path path) {
		return a -> (T) a.get(path);
	}

	public static Function<Adaptation, Object[]> select(final Path... paths) {
		return a -> {
			Object[] o = new Object[paths.length];
			for (int i = 0; i < paths.length; i++) {
				o[i] = a.get(paths[i]);
			}
			return o;
		};
	}

	private Mappers() {
		throw new AssertionError();
	}
}
