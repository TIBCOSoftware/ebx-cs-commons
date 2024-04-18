package com.tibco.ebx.cs.commons.ui.form.dynamic;

import com.orchestranetworks.schema.Path;
/**
 * @author MCH
 *
 * Set on a table, this form will then hide or show fields according to one or many types.
 *
 * A field belongs to one or many types as soon as they are listed in the field "information" of the data model element.
 * Types are here listed separated by ";" (e.g : customer;supplier).
 * A field can be made mandatory for a given type adding ",true" to the type name (e.g : customer,true;supplier,false)
 *
 * Types are specified by one or many field of the current object. The list of paths leading to types must be entered separated by ";" in the parameter pathToTypes.
 * Also, widgets must be set on these fields to trigger the refresh of the form. It can be set to a combo box, a boolean, or a complex made of booleans.
 *
 * Types can also be specified by a collection stored in a referenced record (FK).
 * In this case pathToTypeConfigurator and pathToCollectionInConfigurator must be set and pathToTypes will be ignored.
 * The collection must be a list of string representing field paths.
 * The parameter ignoreFieldsWithInformationEquals allow to specify a information that will make nodes carrying it always accessible.
 *
 * @see TypeAsComboBoxWidgetFactory
 * @see TypeAsBooleansWidgetFactory
 *
 * @See DynamicAttribute
 * @See DynamicAttributesEnumeration
 * @See DynamicAttributeWidget
 *
 */
import com.orchestranetworks.ui.selection.RecordEntitySelection;
import com.orchestranetworks.ui.selection.TableViewEntitySelection;
import com.orchestranetworks.userservice.UserService;
import com.orchestranetworks.userservice.schema.UserServiceRecordFormContext;
import com.orchestranetworks.userservice.schema.UserServiceRecordFormFactory;
import com.orchestranetworks.userservice.schema.UserServiceRecordFormFactoryContext;

/**
 * Dynamic service form
 * 
 * @author Mickaël Chevalier
 */
public class DynamicServiceForm implements UserServiceRecordFormFactory {

	private Path pathToTypeConfigurator;
	private Path pathToCollectionInConfigurator;
	private String ignoreFieldsWithInformationEquals;
	private String pathToTypes;

	/**
	 * Constructor
	 */
	public DynamicServiceForm() {
		super();
	}

	public void setPathToTypeConfigurator(final Path pathToTypeConfigurator) {
		this.pathToTypeConfigurator = pathToTypeConfigurator;
	}

	public void setPathToCollectionInConfigurator(final Path pathToCollectionInConfigurator) {
		this.pathToCollectionInConfigurator = pathToCollectionInConfigurator;
	}

	public void setIgnoreFieldsWithInformationEquals(final String ignoreFieldsWithInformationEquals) {
		this.ignoreFieldsWithInformationEquals = ignoreFieldsWithInformationEquals;
	}

	public void setPathToTypes(final String pathToTypes) {
		this.pathToTypes = pathToTypes;
	}

	@Override
	public UserService<TableViewEntitySelection> newUserServiceForCreate(final UserServiceRecordFormContext.ForCreate forCreate) {
		DynamicTableServiceForm service = new DynamicTableServiceForm(forCreate.getEntitySelection().getTable(), null, false);
		service.setPathToTypes(this.pathToTypes);
		service.setPathToTypeConfigurator(this.pathToTypeConfigurator);
		service.setPathToCollectionInConfigurator(this.pathToCollectionInConfigurator);
		service.setIgnoreFieldsWithInformationEquals(this.ignoreFieldsWithInformationEquals);
		return service;
	}

	@Override
	public UserService<RecordEntitySelection> newUserServiceForDefault(final UserServiceRecordFormContext.ForDefault forDefault) {
		DynamicRecordServiceForm service = new DynamicRecordServiceForm(forDefault.getEntitySelection().getTable(), forDefault.getEntitySelection().getRecord(), false);
		service.setPathToTypes(this.pathToTypes);
		service.setPathToTypeConfigurator(this.pathToTypeConfigurator);
		service.setPathToCollectionInConfigurator(this.pathToCollectionInConfigurator);
		service.setIgnoreFieldsWithInformationEquals(this.ignoreFieldsWithInformationEquals);
		return service;
	}

	@Override
	public UserService<RecordEntitySelection> newUserServiceForDuplicate(final UserServiceRecordFormContext.ForDuplicate forDuplicate) {
		DynamicRecordServiceForm service = new DynamicRecordServiceForm(forDuplicate.getEntitySelection().getTable(), forDuplicate.getEntitySelection().getRecord(), true);
		service.setPathToTypes(this.pathToTypes);
		service.setPathToTypeConfigurator(this.pathToTypeConfigurator);
		service.setPathToCollectionInConfigurator(this.pathToCollectionInConfigurator);
		service.setIgnoreFieldsWithInformationEquals(this.ignoreFieldsWithInformationEquals);
		return service;
	}

	@Override
	public void setup(final UserServiceRecordFormFactoryContext aContext) {
		// nothing to setup
	}
}
