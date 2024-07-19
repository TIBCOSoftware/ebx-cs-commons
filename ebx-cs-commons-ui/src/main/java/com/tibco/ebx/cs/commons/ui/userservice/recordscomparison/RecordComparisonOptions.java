/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.ui.userservice.recordscomparison;

import java.awt.Color;
import java.util.List;

import com.orchestranetworks.schema.Path;

/**
 * Define the options of the record comparison.
 *
 * @author Aurélien Ticot
 * @since 1.0.0
 */
public final class RecordComparisonOptions {
	private List<Path> notComparedPaths;
	private List<Path> excludedPaths;
	private List<Path> picturePaths;
	private List<Path> idamPicturePaths;

	private String notComparedNodeInformation;
	private String excludedNodeInformation;
	private String pictureNodeInformation;
	private String idamPictureNodeInformation;

	private int categoryColumnWidth = 200;
	private int itemColumnWidth = 250;
	private int tableHeaderHeight = 30;

	private int pictureSize = 150;

	private Color differenceColor = Color.getHSBColor(0.14f, 0.2f, 1.0f);

	private boolean displayFunctionsMenu = true;

	/**
	 * Constructor
	 */
	public RecordComparisonOptions() {
		super();
	}

	/**
	 * Display functions menu.
	 *
	 * @return the boolean defining the function menu is displayed or not
	 * @since 1.0.0
	 */
	public boolean displayFunctionsMenu() {
		return this.displayFunctionsMenu;
	}

	/**
	 * Gets the category column width.
	 *
	 * @return the width of the category column, in pixel
	 * @since 1.0.0
	 */
	public int getCategoryColumnWidth() {
		return this.categoryColumnWidth;
	}

	/**
	 * Gets the difference color.
	 *
	 * @return the color edfined to highlight the differences
	 * @since 1.0.0
	 */
	public Color getDifferenceColor() {
		return this.differenceColor;
	}

	/**
	 * Gets the excluded node information.
	 *
	 * @return the string to find in the node information of a node, defining the node as excluded
	 * @since 1.0.0
	 */
	public String getExcludedNodeInformation() {
		return this.excludedNodeInformation;
	}

	/**
	 * Gets the excluded paths.
	 *
	 * @return the excluded paths
	 * @since 1.0.0
	 */
	public List<Path> getExcludedPaths() {
		return this.excludedPaths;
	}

	/**
	 * Gets the idam picture node information.
	 *
	 * @return the string to find in the node information of a node, defining the node as an IDAM picture
	 * @since 1.0.0
	 */
	public String getIdamPictureNodeInformation() {
		return this.idamPictureNodeInformation;
	}

	/**
	 * Gets the idam picture paths.
	 *
	 * @return the list of paths defined as IDAM pictures
	 * @since 1.0.0
	 */
	public List<Path> getIdamPicturePaths() {
		return this.idamPicturePaths;
	}

	/**
	 * Gets the item column width.
	 *
	 * @return the wdith of the item column, in pixel
	 * @since 1.0.0
	 */
	public int getItemColumnWidth() {
		return this.itemColumnWidth;
	}

	/**
	 * Gets the not compared node information.
	 *
	 * @return the string to find in the node information of a node, defining the node as not compared
	 * @since 1.0.0
	 */
	public String getNotComparedNodeInformation() {
		return this.notComparedNodeInformation;
	}

	/**
	 * Gets the not compared paths.
	 *
	 * @return the list of paths defined as not compared
	 * @since 1.0.0
	 */
	public List<Path> getNotComparedPaths() {
		return this.notComparedPaths;
	}

	/**
	 * Gets the picture node information.
	 *
	 * @return the string to find in the node information of a node, defining the node as a picture
	 * @since 1.0.0
	 */
	public String getPictureNodeInformation() {
		return this.pictureNodeInformation;
	}

	/**
	 * Gets the picture paths.
	 *
	 * @return the list of paths defined as a picture
	 * @since 1.0.0
	 */
	public List<Path> getPicturePaths() {
		return this.picturePaths;
	}

	/**
	 * Gets the picture size.
	 *
	 * @return the size of the pictures, in pixel
	 * @since 1.0.0
	 */
	public int getPictureSize() {
		return this.pictureSize;
	}

	/**
	 * Gets the table header height.
	 *
	 * @return the height of the table header in pixel
	 * @since 1.0.0
	 */
	public int getTableHeaderHeight() {
		return this.tableHeaderHeight;
	}

	/**
	 * Set the width of the category column (first column). Default is 200.
	 *
	 * @param categoryColumnWidth the width in pixel
	 * @since 1.0.0
	 */
	public void setCategoryColumnWidth(final int categoryColumnWidth) {
		this.categoryColumnWidth = categoryColumnWidth;
	}

	/**
	 * Set the color of the differences in the comparison result table. Default is Color(255, 0, 0): red.
	 *
	 * @param differenceColor the color to set
	 * @since 1.0.0
	 */
	public void setDifferenceColor(final Color differenceColor) {
		this.differenceColor = differenceColor;
	}

	/**
	 * Set whether the menu is displayed or not. the menu allows to hide/show differences and similarities. Default is true.
	 *
	 * @param displayFunctionsMenu the boolean to display or not the functions menu
	 * @since 1.0.0
	 */
	public void setDisplayFunctionsMenu(final boolean displayFunctionsMenu) {
		this.displayFunctionsMenu = displayFunctionsMenu;
	}

	/**
	 * Set the string to find in the node information of a node, to define the node as excluded.
	 *
	 * @param excludedNodeInformation the string to look for
	 * @since 1.0.0
	 */
	public void setExcludedNodeInformation(final String excludedNodeInformation) {
		this.excludedNodeInformation = excludedNodeInformation;
	}

	/**
	 * Set the paths defined as excluded.
	 *
	 * @param excludedPaths the list of paths
	 * @since 1.0.0
	 */
	public void setExcludedPaths(final List<Path> excludedPaths) {
		this.excludedPaths = excludedPaths;
	}

	/**
	 * Set the string to find in the node information of a node, to define the node as an IDAM picture.
	 *
	 * @param idamPictureNodeInformation the string to look for
	 * @since 1.0.0
	 */
	public void setIdamPictureNodeInformation(final String idamPictureNodeInformation) {
		this.idamPictureNodeInformation = idamPictureNodeInformation;
	}

	/**
	 * Set the paths defined as IDAM pictures.
	 *
	 * @param idamPicturePaths the list of paths
	 * @since 1.0.0
	 */
	public void setIdamPicturePaths(final List<Path> idamPicturePaths) {
		this.idamPicturePaths = idamPicturePaths;
	}

	/**
	 * Set the width of the item columns. Default is 250.
	 *
	 * @param itemColumnWidth the width in pixel
	 * @since 1.0.0
	 */
	public void setItemColumnWidth(final int itemColumnWidth) {
		this.itemColumnWidth = itemColumnWidth;
	}

	/**
	 * Set the string to find in the node information of a node, to define the node as not compared.
	 *
	 * @param notComparedNodeInformation the string to look for
	 * @since 1.0.0
	 */
	public void setNotComparedNodeInformation(final String notComparedNodeInformation) {
		this.notComparedNodeInformation = notComparedNodeInformation;
	}

	/**
	 * Set the paths defined as not compared.
	 *
	 * @param notComparedPaths the list of paths
	 * @since 1.0.0
	 */
	public void setNotComparedPaths(final List<Path> notComparedPaths) {
		this.notComparedPaths = notComparedPaths;
	}

	/**
	 * Set the string to find in the node information of a node, to define the node as a picture.
	 *
	 * @param pictureNodeInformation the string to look for
	 * @since 1.0.0
	 */
	public void setPictureNodeInformation(final String pictureNodeInformation) {
		this.pictureNodeInformation = pictureNodeInformation;
	}

	/**
	 * Set the paths defined as picture.
	 *
	 * @param picturePaths the list of paths defined as a picture
	 * @since 1.0.0
	 */
	public void setPicturePaths(final List<Path> picturePaths) {
		this.picturePaths = picturePaths;
	}

	/**
	 * Set the size (width) of the pictures. Default is 150.
	 *
	 * @param pictureSize the size in pixel
	 * @since 1.0.0
	 */
	public void setPictureSize(final int pictureSize) {
		this.pictureSize = pictureSize;
	}

	/**
	 * Set the height of the table header. Default is 30.
	 *
	 * @param tableHeaderHeight the height of the table header in pixel
	 * @since 1.0.0
	 */
	public void setTableHeaderHeight(final int tableHeaderHeight) {
		this.tableHeaderHeight = tableHeaderHeight;
	}

}
