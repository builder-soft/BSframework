package cl.buildersoft.framework.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import cl.buildersoft.framework.beans.DomainAttribute;
import cl.buildersoft.framework.exception.BSConfigurationException;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSDataUtils {
	private static final Logger LOG = Logger.getLogger(BSDataUtils.class.getName());
	PreparedStatement preparedStatement = null;

	public void closeSQL(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
	}

	public void closeSQL() {
		if (this.preparedStatement != null) {
			try {
				this.preparedStatement.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
	}

	public Integer update(Connection conn, String sql, Object parameter) {
		List<Object> prms = new ArrayList<Object>();
		prms.add(parameter);

		return update(conn, sql, prms);
	}

	public Integer update(Connection conn, String sql, List<Object> parameter) {
		int rowsAffected = 0;
		try {
			preparedStatement = conn.prepareStatement(sql);
			parametersToStatement(parameter, preparedStatement);
			rowsAffected = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		return rowsAffected;
	}

	public Long insert(Connection conn, String sql, List<Object> parameter) {
		Long newKey = 0L;

		try {
			preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			parametersToStatement(parameter, preparedStatement);
			newKey = (long) preparedStatement.executeUpdate();

			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				newKey = rs.getLong(1);
			}

			closeSQL(rs);
			// rs.close();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		return newKey;
	}

	public String queryField(Connection conn, String sql, Object oneParam) {
		List<Object> prms = new ArrayList<Object>();
		prms.add(oneParam);
		return queryField(conn, sql, prms);
	}

	public String queryField(Connection conn, String sql, List<Object> parameter) {
		String out = null;
		ResultSet rs = queryResultSet(conn, sql, parameter);
		try {
			if (rs.next()) {
				out = rs.getString(1);
			}
			rs.close();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
		return out;
	}

	public ResultSet queryResultSet(Connection conn, String sql, Object parameter) {
		List<Object> prms = new ArrayList<Object>();
		prms.add(parameter);

		return queryResultSet(conn, sql, prms);
	}

	public ResultSet queryResultSet(Connection conn, String sql, List<Object> parameters) {
		ResultSet out = null;

		try {
			preparedStatement = conn.prepareStatement(sql);
			parametersToStatement(parameters, preparedStatement);
			out = preparedStatement.executeQuery();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		return out;
	}

	protected void parametersToStatement(List<Object> parameters, PreparedStatement preparedStatement) {
		parametersToStatement(parameters, preparedStatement, 0);

	}

	protected void parametersToStatement(List<Object> parameters, PreparedStatement preparedStatement, Integer initIndex) {
		try {
			if (parameters != null) {
				for (Object param : parameters) {
					if (param instanceof String) {
						preparedStatement.setString(initIndex + 1, (String) param);
					} else if (param instanceof Integer) {
						preparedStatement.setInt(initIndex + 1, ((Integer) param).intValue());
					} else if (param instanceof Double) {
						preparedStatement.setDouble(initIndex + 1, ((Double) param).doubleValue());

					} else if (param instanceof BigDecimal) {
						preparedStatement.setBigDecimal(initIndex + 1, (BigDecimal) param);

					} else if (param instanceof Long) {
						preparedStatement.setLong(initIndex + 1, ((Long) param).longValue());
					} else if (param instanceof Boolean) {
						preparedStatement.setBoolean(initIndex + 1, ((Boolean) param).booleanValue());
					} else if (param instanceof java.util.Calendar || param instanceof java.util.GregorianCalendar) {
						java.sql.Timestamp time = new java.sql.Timestamp(((java.util.Calendar) param).getTimeInMillis());
						preparedStatement.setTimestamp(initIndex + 1, time);
					} else if (param instanceof java.util.Date) {
						java.sql.Timestamp time = new java.sql.Timestamp(((java.util.Date) param).getTime());
						preparedStatement.setTimestamp(initIndex + 1, time);

					} else if (param == null) {
						preparedStatement.setNull(initIndex + 1, java.sql.Types.NULL);
					} else {
						LOG.logp(Level.WARNING, BSDataUtils.class.getName(), "parametersToStatement",
								"Object type not cataloged for type {0}. Will be like a Object class", param.getClass().getName());
						preparedStatement.setObject(initIndex + 1, param);
					}

					initIndex++;
				}
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
	}

	/**
	 * <code>
	 * 
	 * @Deprecated public Connection getConnection(ServletContext context) {
	 *             return getConnection(context, "cosoav"); }
	 * @Deprecated public Connection getConnection(ServletContext context,
	 *             String prefix) { String driverName =
	 *             context.getInitParameter(prefix + ".database.driver"); String
	 *             serverName = context.getInitParameter(prefix +
	 *             ".database.server"); String database =
	 *             context.getInitParameter(prefix + ".database.database");
	 *             String username = context.getInitParameter(prefix +
	 *             ".database.username"); String password =
	 *             context.getInitParameter(prefix + ".database.password");
	 *             return getConnection(driverName, serverName, database,
	 *             password, username); } </code>
	 */
	public Connection getConnection2(ServletContext context) {
		String datasource = context.getInitParameter("bsframework.database.datasource");
		LOG.log(Level.CONFIG, "Datasource for bsframework database is {0}", datasource);
		String driverName = null;
		String serverName = null;
		String database = null;
		String username = null;
		String password = null;
		Connection conn = null;
		if (datasource == null) {
			driverName = context.getInitParameter("bsframework.database.driver");
			serverName = context.getInitParameter("bsframework.database.server");
			database = context.getInitParameter("bsframework.database.database");
			username = context.getInitParameter("bsframework.database.username");
			password = context.getInitParameter("bsframework.database.password");
			conn = getConnection(driverName, serverName, database, password, username);
		} else {
			conn = getConnection2(datasource);
		}
		return conn;
	}

	private Connection getConnection(Map<String, DomainAttribute> domainAttribute) {
		String driverName = domainAttribute.get("database.driver").getValue();
		String serverName = domainAttribute.get("database.server").getValue();
		String database = domainAttribute.get("database.database").getValue();
		String username = domainAttribute.get("database.username").getValue();
		String password = domainAttribute.get("database.password").getValue();

		return getConnection(driverName, serverName, database, password, username);
	}

	public Connection getConnection(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Map<String, DomainAttribute> domainAttribute = null;
		synchronized (session) {
			domainAttribute = (Map<String, DomainAttribute>) session.getAttribute("DomainAttribute");
		}

		return getConnection2(domainAttribute);
	}

	public Connection getConnection2(String dataSourceName) {
		Connection conn = null;

		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource) envContext.lookup(dataSourceName);
			conn = ds.getConnection();
		} catch (SQLException e) {
			LOG.logp(Level.SEVERE, BSDataUtils.class.getName(), "getConnection2",
					"Error connecting width DataSource (SQLException)", e);
			throw new BSConfigurationException(e);
		} catch (NamingException e) {
			LOG.logp(Level.SEVERE, BSDataUtils.class.getName(), "getConnection2",
					"Error connecting width DataSource (NamingException)", e);
			throw new BSConfigurationException(e);
		}

		return conn;
	}

	public Connection getConnection2(Map<String, DomainAttribute> domainAttribute) {
		DomainAttribute dataSource = domainAttribute.get("database.datasource");
		Connection conn = null;
		if (dataSource != null) {
			conn = getConnection2(dataSource.getValue());
		} else {
			conn = getConnection(domainAttribute);
		}

		return conn;
	}

	public Connection getConnection(String driverName, String serverName, String database, String password, String username) {
		Connection connection = null;
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			throw new BSConfigurationException(e);
		}
		String url = "jdbc:mysql://" + serverName + "/" + database;

		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error connecting to database using MySQL", e);
			throw new BSDataBaseException(e);
		}

		return connection;
	}

	public List<Object[]> resultSet2Matrix(ResultSet rs) {
		return resultSet2Matrix(rs, false);
	}

	public List<Object[]> resultSet2Matrix(ResultSet rs, Boolean includeColumns) {
		List<Object[]> out = new ArrayList<Object[]>();

		Integer i = 0;
		Integer colCount = 0;
		String[] colNames = null;
		Object[] innerArray = null;
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			colCount = metaData.getColumnCount();
			colNames = new String[colCount];
			innerArray = new Object[colCount];
			for (i = 1; i <= colCount; i++) {
				colNames[i - 1] = metaData.getColumnName(i);
				innerArray[i - 1] = metaData.getColumnLabel(i);
			}
			if (includeColumns) {
				out.add(innerArray);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		try {
			while (rs.next()) {

				innerArray = new Object[colCount];
				for (i = 1; i <= colCount; i++) {
					innerArray[i - 1] = rs.getObject(i);
				}
				out.add(innerArray);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
		// this.closeSQL(rs);

		return out;
	}

	public void setAutoCommit(Connection conn, Boolean status) {
		try {
			conn.setAutoCommit(status);
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
	}

	public void commit(Connection conn) {
		try {
			conn.commit();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
	}

	public void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
	}
}
