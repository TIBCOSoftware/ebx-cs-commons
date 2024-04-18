package com.tibco.ebx.cs.commons.ui.userservice.recordscomparison;

import java.util.List;

import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaNode;

/**
 * This bean is related to the record comparison class. Its aims to store all information of a particular data model field.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class RecordComparisonItemField {
	private final SchemaNode node;
	private Path path = null;
	private String label = "";
	private List<RecordComparisonItemValue> itemValues = null;
	private boolean compared = true;
	private boolean equal = true;
	private boolean group = false;
	private boolean picture = false;
	private boolean IDAMPicture = false;
	private boolean association = false;
	private boolean multiOccurence = false;
	private boolean foreignKey = false;

	/**
	 * Instantiates a new record comparison item field.
	 *
	 * @param node the node
	 * @since 1.0.0
	 */
	protected RecordComparisonItemField(final SchemaNode node) {
		this.node = node;
		if (node != null) {
			this.path = node.getPathInAdaptation();
		}
	}

	/**
	 * Instantiates a new record comparison item field.
	 *
	 * @param node     the node
	 * @param compared a boolean defining this field is compared or not
	 * @since 1.0.0
	 */
	protected RecordComparisonItemField(final SchemaNode node, final boolean compared) {
		this.node = node;
		if (node != null) {
			this.path = node.getPathInAdaptation();
		}
		this.compared = compared;
	}

	/**
	 * Gets the item values.
	 *
	 * @return a list of the compared values of all items.
	 * @since 1.0.0
	 */
	public List<RecordComparisonItemValue> getItemValues() {
		return this.itemValues;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label of the field. Defined in the data model.
	 * @since 1.0.0
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * Gets the node.
	 *
	 * @return the schema node of the field.
	 * @since 1.0.0
	 */
	public SchemaNode getNode() {
		return this.node;
	}

	/**
	 * Gets the path.
	 *
	 * @return the path of the field.
	 * @since 1.0.0
	 */
	public Path getPath() {
		return this.path;
	}

	/**
	 * Checks if is association.
	 *
	 * @return true if the field is an association node.
	 * @since 1.0.0
	 */
	public boolean isAssociation() {
		return this.association;
	}

	/**
	 * Checks if is compared.
	 *
	 * @return true if the field is compared.
	 * @since 1.0.0
	 */
	public boolean isCompared() {
		return this.compared;
	}

	/**
	 * Checks if is equal.
	 *
	 * @return true if items are equal for this field.
	 * @since 1.0.0
	 */
	public boolean isEqual() {
		return this.equal;
	}

	/**
	 * Checks if is foreign key.
	 *
	 * @return true if the field is a foreign key.
	 * @since 1.0.0
	 */
	public boolean isForeignKey() {
		return this.foreignKey;
	}

	/**
	 * Checks if is group.
	 *
	 * @return true if the field is considered as a group.
	 * @since 1.0.0
	 */
	public boolean isGroup() {
		return this.group;
	}

	/**
	 * Checks if is IDAM picture.
	 *
	 * @return true if the field is a picture managed by IDAM module.
	 * @since 1.0.0
	 */
	public boolean isIDAMPicture() {
		return this.IDAMPicture;
	}

	/**
	 * Checks if is multi occurence.
	 *
	 * @return true is the field is multi-occurenced.
	 * @since 1.0.0
	 */
	public boolean isMultiOccurence() {
		return this.multiOccurence;
	}

	/**
	 * Checks if is picture.
	 *
	 * @return true if the field is a picture.
	 * @since 1.0.0
	 */
	public boolean isPicture() {
		return this.picture;
	}

	/**
	 * Set whether the field is an association node.
	 *
	 * @param association a boolean defining the field is an association node
	 * @since 1.0.0
	 */
	protected void setAssociation(final boolean association) {
		this.association = association;
	}

	/**
	 * Set whether the field shall be compared.
	 *
	 * @param compared a boolean defining the field must be compared or not
	 * @since 1.0.0
	 */
	protected void setCompared(final boolean compared) {
		this.compared = compared;
	}

	/**
	 * Set whether the items are equal for this field.
	 *
	 * @param equal a boolean defining the items are equal (true) or not (false)
	 * @since 1.0.0
	 */
	protected void setEqual(final boolean equal) {
		this.equal = equal;
	}

	/**
	 * Set whether the field is a foreign key.
	 *
	 * @param foreignKey a boolean defining the field is a foreign key
	 * @since 1.0.0
	 */
	protected void setForeignKey(final boolean foreignKey) {
		this.foreignKey = foreignKey;
	}

	/**
	 * Set whether the field is a group.
	 *
	 * @param group a boolean defining this field is a group
	 * @since 1.0.0
	 */
	protected void setGroup(final boolean group) {
		this.group = group;
	}

	/**
	 * Set whether the field is a picture managed by the IDAM module.
	 *
	 * @param IDAMPicture a boolean defining the field is an IDAM picture
	 * @since 1.0.0
	 */
	protected void setIDAMPicture(final boolean IDAMPicture) {
		this.IDAMPicture = IDAMPicture;
	}

	/**
	 * Set the items values of the field.
	 *
	 * @param itemValues a list of the field value of all items
	 * @since 1.0.0
	 */
	protected void setItemValues(final List<RecordComparisonItemValue> itemValues) {
		this.itemValues = itemValues;
	}

	/**
	 * Set the label of the field.
	 *
	 * @param label the label of the field
	 * @since 1.0.0
	 */
	protected void setLabel(final String label) {
		this.label = label;
	}

	/**
	 * Set whether the field is a multi-occurence.
	 *
	 * @param multiOccurence a boolean defining the field is multi-occurenced
	 * @since 1.0.0
	 */
	protected void setMultiOccurence(final boolean multiOccurence) {
		this.multiOccurence = multiOccurence;
	}

	/**
	 * Set the path of the field.
	 *
	 * @param path the path of the field
	 * @since 1.0.0
	 */
	protected void setPath(final Path path) {
		this.path = path;
	}

	/**
	 * Set whether the field is a picture.
	 *
	 * @param picture a boolean defining the field is a picture
	 * @since 1.0.0
	 */
	protected void setPicture(final boolean picture) {
		this.picture = picture;
	}

}
