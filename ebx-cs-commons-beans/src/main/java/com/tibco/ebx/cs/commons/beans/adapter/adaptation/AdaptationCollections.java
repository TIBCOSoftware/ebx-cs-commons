/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.beans.adapter.adaptation;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;

/**
 * 
 * @author Gilles Mayer
 */
public final class AdaptationCollections {
	public static final AdaptationCollection EMPTY = new EmptyAdaptations();

	private static final class EmptyAdaptations extends AbstractCollection<Adaptation> implements AdaptationCollection {
		@Override
		public List<Adaptation> toList() {
			return Collections.emptyList();
		}

		@Override
		public Set<Adaptation> toSet() {
			return Collections.emptySet();
		}

		@Override
		public Adaptation[] toArray() {
			return new Adaptation[0];
		}

		@Override
		public Iterator<Adaptation> iterator() {
			return Collections.emptyListIterator();
		}

		@Override
		public int size() {
			return 0;
		}
	}

	private static class CollectionWrapper implements AdaptationCollection {
		private static final Adaptation[] EMPTY_ARRAY = new Adaptation[0];
		private final Collection<Adaptation> col;

		public CollectionWrapper(final Collection<Adaptation> col) {
			this.col = col;
		}

		@Override
		public int size() {
			return this.col.size();
		}

		@Override
		public boolean isEmpty() {
			return this.col.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return this.col.contains(o);
		}

		@Override
		public Iterator<Adaptation> iterator() {
			return this.col.iterator();
		}

		@Override
		public Adaptation[] toArray() {
			return this.col.toArray(EMPTY_ARRAY);
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			return this.col.toArray(a);
		}

		@Override
		public boolean add(final Adaptation e) {
			return this.col.add(e);
		}

		@Override
		public boolean remove(final Object o) {
			return this.col.remove(o);
		}

		@Override
		public boolean containsAll(final Collection<?> c) {
			return this.col.containsAll(c);
		}

		@Override
		public boolean addAll(final Collection<? extends Adaptation> c) {
			return this.col.addAll(c);
		}

		@Override
		public boolean removeAll(final Collection<?> c) {
			return this.col.removeAll(c);
		}

		@Override
		public boolean retainAll(final Collection<?> c) {
			return this.col.retainAll(c);
		}

		@Override
		public void clear() {
			this.col.clear();
		}

		@Override
		public boolean equals(final Object o) {
			return this.col.equals(o);
		}

		@Override
		public int hashCode() {
			return this.col.hashCode();
		}

		@Override
		public List<Adaptation> toList() {
			return Collections.unmodifiableList(new ArrayList<>(this.col));
		}

		@Override
		public Set<Adaptation> toSet() {
			return Collections.unmodifiableSet(new HashSet<>(this.col));
		}
	}

	public static AdaptationCollection of(final Adaptation[] array) {
		return new CollectionWrapper(Arrays.asList(array));
	}

	public static AdaptationCollection of(final List<Adaptation> list) {
		return new CollectionWrapper(list) {
			@Override
			public List<Adaptation> toList() {
				return Collections.unmodifiableList(list);
			}
		};
	}

	public static AdaptationCollection of(final Set<Adaptation> set) {
		return new CollectionWrapper(set) {
			@Override
			public Set<Adaptation> toSet() {
				return Collections.unmodifiableSet(set);
			}
		};
	}

	/**
	 * The instance of RequestResult in parameter is not closed. Its closing must be ensured by the caller.
	 */
	public static AdaptationCollection of(final RequestResult results) {
		if (!results.isSizeGreaterOrEqual(1)) {
			return EMPTY;
		}

		List<Adaptation> records = new ArrayList<>();
		Adaptation record = null;
		while ((record = results.nextAdaptation()) != null) {
			records.add(record);
		}
		return AdaptationCollections.of(records);
	}

	private AdaptationCollections() {
		throw new AssertionError();
	}
}
