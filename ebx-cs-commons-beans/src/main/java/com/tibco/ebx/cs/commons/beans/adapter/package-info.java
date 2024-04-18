/**
 * This package and sub-packages contain types to map
 * {@link com.onwbp.adaptation.Adaptation adaptations} to read-only <b>beans</b>
 * represented by Java interfaces.
 * 
 * <h3>Beans definition</h3>
 * <p>
 * Beans must be defined by interface classes. The implementation of the bean
 * interface methods is inferred when possible (see examples below) from the
 * interface names and their method names and return types. When automatic
 * inference is not possible, this behavior can be overridden by using a
 * annotations of the {@link com.tibco.ebx.cs.commons.model.annotations
 * annotation sub-package} on the bean interfaces and methods.
 * </p>
 * 
 * <h4>Transformers</h4>
 * <p>
 * Transformers (a.k.a. transformation functions) can be specified when the
 * return type of the getter function is different from the underlying EBX field
 * type.
 * </p>
 * <p>
 * Some transformations happen automatically (transformer does not need to be
 * specified):
 * </p>
 * <table border="1">
 * <tr>
 * <th>Input type</th>
 * <th>Output type</th>
 * <th>Implementation (null safe)</th>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.onwbp.adaptation.AdaptationName}</td>
 * <td>{@link com.onwbp.adaptation.AdaptationReference#forPersistentName(String)}</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.onwbp.adaptation.AdaptationReference}</td>
 * <td>{@link com.onwbp.adaptation.AdaptationReference#forName(String)}</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.orchestranetworks.schema.SchemaLocation}</td>
 * <td>{@link com.orchestranetworks.schema.SchemaLocation#parse(String)}</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.orchestranetworks.schema.Path}</td>
 * <td>{@link com.orchestranetworks.schema.Path#parse(String)}</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.orchestranetworks.instance.HomeKey}</td>
 * <td>{@link com.orchestranetworks.instance.HomeKey#parse(String)}</td>
 * </tr>
 * <tr>
 * <td>String</td>
 * <td>{@link com.orchestranetworks.service.AccessPermission}</td>
 * <td>{@link com.orchestranetworks.service.AccessPermission#parseFlag(String)}</td>
 * </tr>
 * </table>
 * 
 * <p>
 * Other non-automatic transformers are available for convenience in the
 * {@link com.tibco.ebx.cs.commons.model.transformers transformers sub-package}.
 * </p>
 *
 * <h4>Example</h4>
 * 
 * <pre>
 * // Automatically mapped to the records of a table at path '/root/Book'
 * public interface Book {
 * 
 * 	// Automatically mapped to the String field at path '/root/Book/title'
 * 	String getTitle();
 * 
 * 	// Automatically mapped to the element at path '/root/Book/authors' which can
 * 	// either be:
 * 	// - an association
 * 	// - a multi-occurenced foreign key
 * 	// to the Author table.
 * 	//
 * 	// The beans in the returned list are mapped to the Author table (defined
 * 	// below)
 * 	List<Author> getAuthors();
 * 
 * }
 * 
 * // Explicitly mapped to the records of a table at path '/root/Writer'
 * // @Table annotation is required since the name of the interface does not match the name 
 * // of the table in the data model
 * &#64;Table(pathInSchema = "/root/Writer")
 * public interface Author {
 * 
 * 	// Automatically mapped to the Date field at '/root/Writer/dateOfBirth' but a
 * 	// transformer class is specified to convert from 'java.util.Date' values
 * 	// returned by EBX API to 'java.time.LocalDate' returned by this getter
 * 	&#64;Field(transformer = LocaDateTransformer.class)
 * 	LocalDate getDateOfBirth();
 * 
 * 	// the transformation function
 * 	public class LocaDateTransformer implements Function<Date, LocalDate> {
 * 		&#64;Override
 * 		public LocalDate apply(Date d) {
 * 			return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
 * 		}
 * 	}
 * 
 * 	// Default methods are not be mapped to an element of the data model but
 * 	// defines an implementation to compute a custom value
 * 	default int getAgeInYears() {
 * 		return Period.between(getDateOfBirth(), LocalDate.now()).getYears();
 * 	}
 * 
 * }
 * </pre>
 * 
 * <h3>Beans loading</h3>
 * <p>
 * Loading (instantiation) of the beans can be performed using instances of
 * {@link com.tibco.ebx.cs.commons.model.loaders.BeanLoader} (check the static
 * factory methods).
 * </p>
 * 
 * 
 * <h3>Performance considerations</h3>
 * <p>
 * Since these classes add an additional layer on top of EBX API classes they
 * may also add an overhead. Hence, they must be used with caution when
 * performance is important especially with large volume of data or frequent
 * calls.
 * </p>
 */
package com.tibco.ebx.cs.commons.beans.adapter;
