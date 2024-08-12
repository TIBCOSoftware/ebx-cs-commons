/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.lib.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.onwbp.adaptation.Adaptation;
import com.onwbp.adaptation.AdaptationHome;
import com.onwbp.adaptation.AdaptationName;
import com.onwbp.adaptation.AdaptationTable;
import com.orchestranetworks.instance.HomeKey;
import com.orchestranetworks.instance.Repository;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.service.Session;
import com.orchestranetworks.workflow.PublishedProcess;
import com.orchestranetworks.workflow.PublishedProcessKey;
import com.orchestranetworks.workflow.WorkflowEngine;

/**
 * A class to help read values from the properties file. It can be sub-classed
 * to handle specific properties.
 *
 * @author Mickaël Chevalier
 */
public class PropertyFileHelper {
	private static final String PROPERTY_VALUE_SEPARATOR = ",";
	private static final String PROPERTY_TOKEN_SEPARATOR = "\\|";

	protected static final int PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME = 0;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_SET_NAME = 1;
	protected static final int PROPERTY_TOKEN_INDEX_TABLE_NAME = 2;
	protected static final int PROPERTY_TOKEN_INDEX_DATA_MODEL_XSD = 2;

	protected Properties props;

	/**
	 * Create the helper
	 *
	 * @param propertiesFile a string representing the path to the properties file
	 * @throws IOException if an error occurred loading the properties file
	 */
	public PropertyFileHelper(final String propertiesFile) throws IOException {
		// Load the properties
		props = new Properties();
		try (InputStream in = new FileInputStream(propertiesFile)) {
			props.load(in);
		}
	}

	/**
	 * Get the properties object representing the properties
	 *
	 * @return the properties
	 */
	public Properties getProperties() {
		return props;
	}

	/**
	 * Get a property and throw an exception if it's not found or has no value
	 *
	 * @param propertyName the property name
	 * @return the property
	 * @throws IOException if the property couldn't be found or has no value
	 */
	public String getRequiredProperty(final String propertyName) throws IOException {
		String propertyValue = props.getProperty(propertyName);
		if (propertyValue == null || "".equals(propertyValue)) {
			throw new IOException("Value must be specified for property " + propertyName + ".");
		}
		return propertyValue;
	}

	/**
	 * Is a property defined
	 *
	 * @param propertyName the property name
	 * @return if it's defined
	 */
	public boolean isPropertyDefined(final String propertyName) {
		return props.getProperty(propertyName) != null;
	}

	/**
	 * Get a property as a boolean
	 *
	 * @param propertyName       the property name
	 * @param defaultWhenMissing the default value if the property is missing or has
	 *                           no value defined
	 * @return the boolean value
	 */
	public boolean getBooleanProperty(final String propertyName, final boolean defaultWhenMissing) {
		String strValue = String.valueOf(defaultWhenMissing);
		return Boolean.valueOf(props.getProperty(propertyName, strValue)).booleanValue();
	}

	/**
	 * Get a comma-separated property value as an array of strings. If the property
	 * is missing or has an empty value, an empty array will be returned.
	 *
	 * @param propertyName the property name
	 * @return an array of strings
	 */
	public String[] getPropertyAsArray(final String propertyName) {
		String propertyValue = props.getProperty(propertyName);
		if (propertyValue == null || "".equals(propertyValue)) {
			return new String[0];
		}
		return propertyValue.split(PROPERTY_VALUE_SEPARATOR);
	}

	/**
	 * Split a given pipe-delimited property value into an array of strings
	 *
	 * @param propertyValue the pipe-delimited property value
	 * @return an array of strings
	 */
	public static String[] getPropertyValueTokens(final String propertyValue) {
		return propertyValue.split(PROPERTY_TOKEN_SEPARATOR);
	}

	/**
	 * Get the data space represented by the given property value
	 *
	 * @param propertyValue a string representing the data space name
	 * @param repo          the repository
	 * @return the data space, or null if not found
	 */
	public static AdaptationHome getDataSpaceFromProperty(final String propertyValue, final Repository repo) {
		// Split the string since the property may contain other things,
		// but it should at least contain the data space
		String[] tokens = getPropertyValueTokens(propertyValue);
		// Look up the data space represented by the typical index used for data spaces
		return repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
	}

	/**
	 * Get the data set represented by the given property value
	 *
	 * @param propertyValue a string representing the data space name and data set
	 *                      name
	 * @param repo          the repository
	 * @return the data set, or null if not found
	 */
	public static Adaptation getDataSetFromProperty(final String propertyValue, final Repository repo) {
		// Split the string. It should at least contain the data space and data set.
		String[] tokens = getPropertyValueTokens(propertyValue);
		// Look up the data space represented by the typical index used for data spaces
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
		// Look up the data set represented by the typical index used for data sets,
		// if the data space was found
		return dataSpace == null ? null
				: dataSpace.findAdaptationOrNull(AdaptationName.forName(tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME]));
	}

	/**
	 * Get the table represented by the given property value
	 *
	 * @param propertyValue a string representing the data space name, data set
	 *                      name, and table path
	 * @param repo          the repository
	 * @return the table, or null if not found
	 */
	public static AdaptationTable getTableFromProperty(final String propertyValue, final Repository repo) {
		// Split the string. It should at least contain the data space, data set, and
		// table.
		String[] tokens = getPropertyValueTokens(propertyValue);
		// Look up the data space represented by the typical index used for data spaces
		AdaptationHome dataSpace = repo.lookupHome(HomeKey.forBranchName(tokens[PROPERTY_TOKEN_INDEX_DATA_SPACE_NAME]));
		if (dataSpace == null) {
			return null;
		}
		// Look up the data set represented by the typical index used for data sets
		Adaptation dataSet = dataSpace
				.findAdaptationOrNull(AdaptationName.forName(tokens[PROPERTY_TOKEN_INDEX_DATA_SET_NAME]));
		// Look up the table represented by the typical index used for tables,
		// if the data set was found
		return (dataSet == null || dataSet.hasSevereError()) ? null
				: dataSet.getTable(Path.parse(tokens[PROPERTY_TOKEN_INDEX_TABLE_NAME]));
	}

	/**
	 * Get the workflow publications represented by the given property value
	 *
	 * @param propertyValue a string representing the workflow model name
	 * @param repo          the repository
	 * @param session       the session
	 * @return the list of publications, or an empty list if none found
	 */
	public static List<PublishedProcess> getWorkflowPublicationsFromProperty(final String propertyValue,
			final Repository repo, final Session session) {
		WorkflowEngine wfEngine = WorkflowEngine.getFromRepository(repo, session);
		List<PublishedProcess> publishedProcesses = new ArrayList<>();

		// Loop through all published keys
		List<PublishedProcessKey> publishedKeys = wfEngine.getPublishedKeys(false);
		for (PublishedProcessKey publishedKey : publishedKeys) {
			// Get the published process for the key, and if its name is equal to
			// the workflow model name we're looking for, then add it to the list.
			PublishedProcess publishedProcess = wfEngine.getPublishedProcess(publishedKey);
			if (propertyValue.equals(publishedProcess.getAdaptationName().getStringName())) {
				publishedProcesses.add(publishedProcess);
			}
		}
		return publishedProcesses;
	}
}
