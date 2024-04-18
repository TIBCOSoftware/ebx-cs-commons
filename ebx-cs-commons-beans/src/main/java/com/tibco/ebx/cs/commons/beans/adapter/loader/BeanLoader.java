package com.tibco.ebx.cs.commons.beans.adapter.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.RequestResult;
import com.tibco.ebx.cs.commons.beans.adapter.loader.impl.ModelException;

/**
 * A bean loader is responsible for instantiating beans from one or several {@link Adaptation adaptations}. <br>
 * A bean implementation (which is hidden to the client code) uses conventions and {@link com.tibco.ebx.cs.commons.model.annotations annotations} <br>
 * to compute the return values of the getters method, using the underlying adaptation.
 * <p>
 * A bean loader is the entry point for client code.
 * </p>
 * <p>
 * Loader implementations are available through static factory methods on this interface.
 * </p>
 * 
 * @author Gilles Mayer
 */
public interface BeanLoader {

	/**
	 * Loads a bean from the given adaptation.
	 *
	 * @param <T>        the type of the bean
	 * @param beanType   the interface class of the bean
	 * @param adaptation an adaptation, may be {@code null}
	 * @return a bean of type {@code T} mapped the specified adaptation or {@code null} is the adaptation is {@code null}
	 * @throws ModelException if the adaptation cannot be mapped to an instance of the bean type
	 */
	<T> T load(Class<T> beanType, Adaptation adaptation) throws ModelException;

	/**
	 * Convenience method to load multiple beans of the same type from the adaptations returned by an {@link Iterable}.
	 */
	default <T> List<T> load(final Class<T> beanType, final Iterable<Adaptation> adaptations) throws ModelException {
		ArrayList<T> list;
		if (adaptations instanceof Collection) {
			list = new ArrayList<>(((Collection<Adaptation>) adaptations).size());
		} else {
			list = new ArrayList<>();
		}
		for (Adaptation a : adaptations) {
			list.add(this.load(beanType, a));
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * Convenience method to load multiple beans of the same type from the adaptations returned by a {@link RequestResult} .
	 *
	 * The instance of RequestResult in parameter is not closed. Its closing must be ensured by the caller.
	 */
	default <T> List<T> load(final Class<T> beanType, final RequestResult result) throws ModelException {
		Adaptation record;
		ArrayList<T> list = new ArrayList<>();
		while ((record = result.nextAdaptation()) != null) {
			list.add(this.load(beanType, record));
		}
		return Collections.unmodifiableList(list);
	}

	/**
	 * Static factory method that returns a {@link BeanLoader loader} implementation whose beans keep a direct reference to the underlying {@link Adaptation adaptation}. Values and records are lazily
	 * fetched when the bean methods are called.
	 *
	 * <p>
	 * Since beans loaded through the returned loader keep a reference to an adaptation and use it to read values or request other adaptations, all the rules and recommendations in usage of
	 * adaptations also apply to these beans.
	 * </p>
	 *
	 * @return a model loader instance
	 */
	static BeanLoader getAdaptationBackedBeanLoader() {
		return new AdaptationBackedBeanLoader();
	}

	/**
	 * Static factory method that returns a {@link BeanLoader loader} implementation whose beans hold only the values and links to other beans. All the values are eagerly read before the bean instance
	 * (respectively beans instances) are returned by {@link #load(Class, Adaptation)} (respectively {@link #load(Class, Iterable)} or {@link #load(Class, RequestResult)}).
	 *
	 * <p>
	 * Beans loaded through the returned loader are immutable (unless the value returned by a custom {@link Field#transformer() transformer } or {@link Field#keyExtractor() key extractor} is not) and
	 * do not keep any reference to any {@link Adaptation}. Hence they can be stored for a long period of time and can be accessed from multiple threads.
	 * </p>
	 *
	 * @return a model loader instance
	 */
	static BeanLoader getEagerReadBeanLoader() {
		return new EagerReadBeanLoader();
	}
}
