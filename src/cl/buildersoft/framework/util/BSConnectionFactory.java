package cl.buildersoft.framework.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cl.buildersoft.framework.beans.Domain;
import cl.buildersoft.framework.exception.BSConfigurationException;
import cl.buildersoft.framework.exception.BSDataBaseException;

public class BSConnectionFactory {
	/**
	 * <code>
Hay que implementar un método en el cual se indique el módulo y el dominio de conección 
para saber a cual base de datos se debe conectar el datasource.

- getConnection(HttpServletRequest, String module)

</code>
	 */
	private static final Logger LOG = Logger.getLogger(BSConnectionFactory.class.getName());

	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, "Can't close connection", e);
				throw new BSDataBaseException(e);
			}
		}
	}

	public Connection getConnection(String dsName) {
		Connection out = null;
		try {
			out = getConnectionByDataSource(dsName);
		} catch (RuntimeException e) {
			// if (e.getCause() instanceof NoInitialContextException) {
			String[] params = getDataConnection(dsName);
			out = getConnectionJDBC(params[0], params[1], params[2], params[3]);
			// }
		}
		return out;
	}

	public Connection getConnection(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Domain domain = null;
		synchronized (session) {
			domain = (Domain) session.getAttribute("Domain");
		}

		return getConnectionByDataSource(domain.getDatabase());
	}

	public Connection getConnection() {
		return getConnection("bsframework");
	}

	private Connection getConnectionJDBC(String driverName, String url, String username, String password) {
		Connection conn = null;
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			LOG.log(Level.SEVERE, "Error to load driver {0} in class BSConnectionmanager", driverName);
			throw new BSConfigurationException(e);
		}
		// String url = "jdbc:mysql://" + serverName + "/" + database;

		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error connecting to database using MySQL driver={0} url={1} user={2} pass={3}",
					BSUtils.array2ObjectArray(driverName, url, username, password));
			throw new BSDataBaseException(e);
		}

		return conn;
	}

	private Connection getConnectionByDataSource(String dsName) {
		Connection conn = null;
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/" + dsName);
			conn = ds.getConnection();
		} catch (SQLException e) {
			LOG.logp(Level.SEVERE, BSDataUtils.class.getName(), "getConnectionByDataSource",
					"Error connecting width DataSource (SQLException)", e);
			throw new BSConfigurationException(e);
		} catch (NamingException e) {
			LOG.logp(Level.WARNING, BSDataUtils.class.getName(), "getConnectionByDataSource",
					"Error connecting width DataSource (NamingException)");
			throw new BSConfigurationException(e);
			// return getConnection(dsName);
		}

		return conn;
	}

	private String[] getDataConnection(String dsName) {
		String[] out = new String[4];

		String fileConfigPath = getContextPathFile();

		LOG.log(Level.CONFIG, "Configuration file is {0}", fileConfigPath);

		try {
			// Document contextFile = DocumentHelper.parseText(fileConfigPath);
			SAXReader reader = new SAXReader();
			Document contextFile = reader.read(fileConfigPath);

			Element resourceNode = (Element) contextFile.selectSingleNode("/Context/Resource[@name='jdbc/" + dsName + "']");
			if (resourceNode == null) {
				throw new BSConfigurationException("Configuration for '" + dsName + "' not found in file '" + fileConfigPath
						+ "'");
			}
			out[0] = resourceNode.attributeValue("driverClassName");
			out[1] = resourceNode.attributeValue("url");
			out[2] = resourceNode.attributeValue("username");
			out[3] = resourceNode.attributeValue("password");

		} catch (DocumentException e) {
			LOG.log(Level.SEVERE, "Cant parse XML file {0}", fileConfigPath);
			throw new BSConfigurationException(e);
		} catch (Error e) {
			LOG.log(Level.SEVERE, "Error reading configuration", e);
		}
		return out;
	}

	private String getContextPathFile() {
		return System.getenv("CATALINA_HOME") + File.separatorChar + "conf" + File.separatorChar + "context.xml";

		// return System.getenv("BS_PATH") + File.separatorChar + ".." +
		// File.separatorChar + "META-INF"
		// + File.separatorChar + "context.xml";
	}
}
