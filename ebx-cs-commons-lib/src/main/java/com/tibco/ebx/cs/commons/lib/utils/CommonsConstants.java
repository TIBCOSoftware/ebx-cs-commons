/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
/*
 * Copyright Orchestra Networks 2000-2012. All rights reserved.
 */
package com.tibco.ebx.cs.commons.lib.utils;

import java.io.IOException;
import java.util.Properties;

import com.onwbp.base.misc.StringUtils;
import com.orchestranetworks.service.LoggingCategory;
import com.orchestranetworks.service.Profile;
import com.orchestranetworks.service.Role;
import com.orchestranetworks.service.ServiceKey;

/**
 * Some common constants used in other ps-library classes, such as tech-admin user role, date/time formats, etc.
 */
public final class CommonsConstants {

	public static final String MODULE_NAME = "ebx-cs-commons";

	public static final ServiceKey GENERATE_JAVA_ACCESSERS_SERVICE = ServiceKey.forModuleServiceName(MODULE_NAME, "GenerateJavaAccessers");
	public static final ServiceKey GENERATE_DATA_MODEL_DOCUMENTATION_SERVICE = ServiceKey.forModuleServiceName(MODULE_NAME, "GenerateDataModelDocumentation");

	private static final String EBX_HOME_PROPERTY_NAME = "ebx.home";
	private static final String DEFAULT_EBX_PROPERTIES_FILE_NAME = System.getProperty(EBX_HOME_PROPERTY_NAME) + "/ebx.properties";
	private static final String EBX_PROPERTIES_SYSTEM_PROPERTY_NAME = "ebx.properties";

	private static final String TECH_ADMIN_ROLE_PROPERTY_NAME = "tech.admin.role";
	private static final String DEFAULT_TECH_ADMIN_ROLE_NAME = "Tech Admin";

	/**
	 * The role name used for Tech Admin functionality. Tech Admins typically are made owner of data spaces, data sets, data models, and workflow models, and have full access to change data outside of
	 * a workflow, administer workflows, validate data, and so on.
	 *
	 * This checks for a <code>tech.admin.role</code> property in <code>ebx.properties</code>, and if not found, uses the default role of "Tech Admin". Note that this does not necessarily mean that
	 * the role is defined in the directory. Therefore if you don't wish to use a Tech Admin role, you don't have to as long as your code doesn't rely on this role existing.
	 *
	 * To indicate that the built-in administrator role acts as Tech Admin, you would specify "administrator" for the value.
	 *
	 * The role is read from the properties file at startup so if it's changed in <code>ebx.properties</code> after startup, it won't take effect until EBX is restarted.
	 */
	public static final String ROLE_TECH_ADMIN = initTechAdminRoleName();

	/**
	 * The role used for Tech Admin functionality. When the Tech Admin role name specified is "administrator", this will be the built-in administrator role. Otherwise, it will be the "specific" role
	 * for the specified name (@see {@link Role#forSpecificRole(String)}).
	 *
	 * @see #ROLE_TECH_ADMIN
	 */
	public static final Role TECH_ADMIN = initTechAdminRole();

	public static final String EBX_DATE_FORMAT = "yyyy-MM-dd";
	public static final String EBX_TIME_FORMAT = "HH:mm:ss";
	public static final String EBX_DATE_TIME_FORMAT = EBX_DATE_FORMAT + "'T'" + EBX_TIME_FORMAT;

	public static final String DATA_SPACE_NAME_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	public static final String SHOW_OCCULTED_RECORDS_SESSION_ATTRIBUTE = "showOccultedRecords";

	// For now, we only need local but we could add environments for development, etc
	public static final String ENVIRONMENT_MODE_LOCAL = "local";

	// The name used by the built-in Configuration workflow model (used for message templates)
	public static final String WORKFLOW_MODEL_CONFIGURATION = "configuration";

	// The possible backend modes used by EBX in the ebx.properties file
	public static final String BACKEND_MODE_DEVELOPMENT = "development";
	public static final String BACKEND_MODE_INTEGRATION = "integration";
	public static final String BACKEND_MODE_PRODUCTION = "production";

	// Used to indicate a current context that can be the current dataspace, dataset or table
	public static final String THIS = "This";

	/**
	 * Read the properties from the <code>ebx.properties</code> file. This checks for the <code>ebx.properties</code> system property, and if not found, looks for a file called
	 * <code>ebx.properties</code> in the EBX Home directory.
	 *
	 * @return a {@link Properties} object containing the properties
	 * @throws IOException if there was an error reading the file
	 */
	public static Properties getEBXProperties() throws IOException {
		String propertiesFile = System.getProperty(EBX_PROPERTIES_SYSTEM_PROPERTY_NAME, DEFAULT_EBX_PROPERTIES_FILE_NAME);

		PropertyFileHelper propertyHelper = new PropertyFileHelper(propertiesFile);
		return propertyHelper.getProperties();
	}

	/**
	 * Get the EBX Home path by first checking for a the <code>ebx.home</code> system property, and if not found, by reading it from the <code>ebx.properties</code> file.
	 *
	 * @return the EBX Home path
	 * @throws IOException if an error occurred finding the EBX Home
	 */
	public static String getEBXHome() throws IOException {
		String ebxHome = System.getProperty(CommonsConstants.EBX_HOME_PROPERTY_NAME);
		if (StringUtils.isEmpty(ebxHome)) {
			ebxHome = getEBXProperties().getProperty(CommonsConstants.EBX_HOME_PROPERTY_NAME);
		}
		return ebxHome;
	}

	// This does the actual initialization of the constant from the properties file
	private static String initTechAdminRoleName() {
		Properties props;
		try {
			props = getEBXProperties();
		} catch (IOException ex) {
			LoggingCategory.getKernel().error("Error reading ebx.properties.", ex);
			return DEFAULT_TECH_ADMIN_ROLE_NAME;
		}

		String roleName = props.getProperty(TECH_ADMIN_ROLE_PROPERTY_NAME);
		if (roleName == null) {
			return DEFAULT_TECH_ADMIN_ROLE_NAME;
		}
		roleName = roleName.trim();
		if ("".equals(roleName)) {
			return DEFAULT_TECH_ADMIN_ROLE_NAME;
		}
		return roleName;
	}

	// This initializes the role constant from the name
	private static Role initTechAdminRole() {
		if (Profile.ADMINISTRATOR.getRoleName().equals(ROLE_TECH_ADMIN)) {
			return Profile.ADMINISTRATOR;
		}
		return Profile.forSpecificRole(ROLE_TECH_ADMIN);
	}

	protected CommonsConstants() {
	}
}
