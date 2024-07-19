/* Copyright © 2024. Cloud Software Group, Inc. This file is subject to the license terms contained in the license file that is distributed with this file. */
package com.tibco.ebx.cs.commons.component.enumeration;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.onwbp.base.text.UserMessage;
import com.orchestranetworks.instance.ValueContext;
import com.orchestranetworks.schema.Constraint;
import com.orchestranetworks.schema.ConstraintContext;
import com.orchestranetworks.schema.Path;
import com.orchestranetworks.schema.SchemaTypeName;
import com.orchestranetworks.service.LoggingCategory;

/**
 * Abstract constraint which can be configured to retrieve a JDBC {@link Connection}.
 * <h3>Parameters</h3>
 * <p>
 * One of the 4 parameters group must be specified, depending on how the connection can be retrieved.
 * </p>
 * <ul>
 * <li><b>Groups 1 and 2</b> are used for a static configuration
 * <li><b>Groups 3 and 4</b> are used for a dynamic configuration based on values of other fields in the adaptation
 * <li><b>Groups 1 and 3</b> are used to retrieve the Connection through a JNDI DataSource
 * <li><b>Groups 2 and 4</b> are used to retrieve the Connection using the {@link DriverManager} class
 * </ul>
 * 
 * @author Mickaël Chevalier
 *
 */
public abstract class JdbcAbstractConstraint<X> implements Constraint<X> {

	// Parameter group 1
	private String datasourceName;

	// Parameter group 2
	private String driverClass;
	private String connectionUrl;

	// Parameter group 3
	private Path relativePathToDatasourceName;

	// Parameter group 4
	private Path relativePathToDriverClass;
	private Path relativePathToConnectionUrl;
	private Path relativePathToConnectionUser;
	private Path relativePathToConnectionPassword;

	public void setG1_DatasourceName(final String datasourceName) {
		this.datasourceName = datasourceName;
	}

	public void setG2_DriverClass(final String driverClass) {
		this.driverClass = driverClass;
	}

	public void setG2_ConnectionUrl(final String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}

	public void setG3_RelativePathToDatasourceName(final Path relativePathToDatasourceName) {
		this.relativePathToDatasourceName = relativePathToDatasourceName;
	}

	public void setG4_RelativePathToDriverClass(final Path relativePathToDriverClass) {
		this.relativePathToDriverClass = relativePathToDriverClass;
	}

	public void setG4_RelativePathToConnectionUrl(final Path relativePathToConnectionUrl) {
		this.relativePathToConnectionUrl = relativePathToConnectionUrl;
	}

	public void setG4_RelativePathToConnectionUser(final Path relativePathToConnectionUser) {
		this.relativePathToConnectionUser = relativePathToConnectionUser;
	}

	public void setG4_RelativePathToConnectionPassword(final Path relativePathToConnectionPassword) {
		this.relativePathToConnectionPassword = relativePathToConnectionPassword;
	}

	protected String getDatasourceName() {
		return datasourceName;
	}

	protected String getDriverClass() {
		return driverClass;
	}

	protected String getConnectionUrl() {
		return connectionUrl;
	}

	protected Path getRelativePathToDatasourceName() {
		return relativePathToDatasourceName;
	}

	protected Path getRelativePathToDriverClass() {
		return relativePathToDriverClass;
	}

	protected Path getRelativePathToConnectionUrl() {
		return relativePathToConnectionUrl;
	}

	protected Path getRelativePathToConnectionUser() {
		return relativePathToConnectionUser;
	}

	protected Path getRelativePathToConnectionPassword() {
		return relativePathToConnectionPassword;
	}

	@Override
	public void setup(final ConstraintContext aContext) {
		if (datasourceName != null) {
			try {
				if (!(getInitialContext().lookup(datasourceName) instanceof DataSource)) {
					aContext.addWarning("No datasource at " + datasourceName);
				}
			} catch (NamingException ex) {
				aContext.addWarning(ex.toString());
			}
		} else if (connectionUrl != null) {
			try (Connection c = connect(driverClass, connectionUrl, null, null)) {

			} catch (SQLException e) {
				aContext.addError("Cannot connect to " + connectionUrl, e);
			}
		} else if (relativePathToDatasourceName != null) {
			if (!SchemaTypeName.XS_STRING.equals(aContext.getSchemaNode().getNode(relativePathToDatasourceName, false, false).getXsTypeName())) {
				aContext.addError(relativePathToDatasourceName.format() + " is not a path to a String field");
			}
		} else if (relativePathToConnectionUrl != null) {
			if (!SchemaTypeName.XS_STRING.equals(aContext.getSchemaNode().getNode(relativePathToConnectionUrl, false, false).getXsTypeName())) {
				aContext.addError(relativePathToConnectionUrl.format() + " is not a path to a String field");
			}
		} else {
			aContext.addError(
					"No connection information provided. One of {G1_datasourceName, G2_connectionUrl, G3_relativePathToDatasourceName, G4_relativePathToConnectionUrl} parameter must be set");
		}
	}

	/**
	 * Connect using the {@link DriverManager}
	 */
	protected Connection connect(final String driverClass, final String connectionUrl, final String user, final String password) throws SQLException {
		if (driverClass != null) {
			Driver driverInstance = null;
			try {
				driverInstance = JdbcAbstractConstraint.<Driver>loadClass(driverClass).getDeclaredConstructor().newInstance();
				DriverManager.registerDriver(driverInstance);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException
					| ClassNotFoundException | ClassCastException e) {
				/**
				 * Driver implementations usually register themselves in a static init block and/or provide a safe default constructor
				 */
				LoggingCategory.getKernel().debug(UserMessage.createError("Cannot load driver", e));
			}
			for (Driver driver : Collections.list(DriverManager.getDrivers())) {
				if (driver.getClass().getName().contentEquals(driverClass)) {
					Properties info = new Properties();
					if (user != null) {
						info.put("user", user);
					}
					if (password != null) {
						info.put("password", password);
					}
					return driver.connect(connectionUrl, info);
				}
			}

		}
		return DriverManager.getConnection(connectionUrl, user, password);
	}

	@SuppressWarnings("unchecked")
	private static <X> Class<X> loadClass(final String name) throws ClassNotFoundException, ClassCastException {
		return (Class<X>) Thread.currentThread().getContextClassLoader().loadClass(name);
	}

	/**
	 * Gets the connection, according to the constraint parameters
	 */
	protected Connection getConnection(final ValueContext aContext) throws Exception {
		if (datasourceName != null) {
			return lookupDatasource(datasourceName).getConnection();

		} else if (connectionUrl != null) {
			return connect(driverClass, connectionUrl, null, null);

		} else if (relativePathToDatasourceName != null) {
			String name = (String) aContext.getValue(relativePathToDatasourceName);
			if (name == null) {
				return null;
			}
			return lookupDatasource(name).getConnection();

		} else {
			String url = (String) aContext.getValue(relativePathToConnectionUrl);
			if (url == null) {
				return null;
			}
			String driver = relativePathToDriverClass == null ? null : (String) aContext.getValue(relativePathToDriverClass);
			String user = relativePathToConnectionUser == null ? null : (String) aContext.getValue(relativePathToConnectionUser);
			String password = relativePathToConnectionPassword == null ? null : (String) aContext.getValue(relativePathToConnectionPassword);
			return connect(driver, url, user, password);
		}

	}

	/**
	 * Lookup the data source in the JNDI contexts
	 */
	protected DataSource lookupDatasource(final String name) throws Exception {
		Object object = getInitialContext().lookup(name);
		if (!(object instanceof DataSource)) {
			throw new Exception("Object at " + name + " is not a DataSource");
		}
		return (DataSource) object;
	}

	/**
	 * Gets the {@link InitialContext} used to lookup the data source
	 */
	protected Context getInitialContext() throws NamingException {
		return new InitialContext();
	}

}
