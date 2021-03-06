package cl.buildersoft.framework.util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.buildersoft.framework.exception.BSDataBaseException;

public class BSDataUtils {
	private static final Logger LOG = LogManager.getLogger(BSDataUtils.class);

	PreparedStatement preparedStatement = null;

	public void closeSQL(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
	}

	public void closeSQL() {
		if (this.preparedStatement != null) {
			try {
				this.preparedStatement.close();
				this.preparedStatement = null;
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
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = conn.prepareStatement(sql);
			parametersToStatement(parameter, preparedStatement);
			rowsAffected = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			LOG.error(String.format("SQL was: %s and parameters were %s", sql, parameter.toString()));
			throw new BSDataBaseException(e);
		} finally {
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}
		}

		return rowsAffected;
	}

	public Long insert(Connection conn, String sql, List<Object> parameter) {
		Long newKey = 0L;
		PreparedStatement preparedStatement = null;
		ResultSet rs = null;
		try {
			preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			parametersToStatement(parameter, preparedStatement);
			newKey = (long) preparedStatement.executeUpdate();

			rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				newKey = rs.getLong(1);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		} finally {
			closeSQL(rs);
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}
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
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		} finally {
			closeSQL(rs);
			closeSQL();
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
		} catch (Exception e) {
			LOG.error(String.format("SQL was: %s and parameters were %s", sql, parameters.toString()));
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
						LOG.warn(String.format("Object type not cataloged for type %s. Will be like a Object class", param
								.getClass().getName()));
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
	public Connection getConnection2() {
		BSConnectionFactory cf = new BSConnectionFactory();
		return cf.getConnection();

		
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
		BSConnectionFactory cf = new BSConnectionFactory();
		return cf.getConnection(request);
	}
 
	public Connection getConnection2(String dsName) {
		BSConnectionFactory cf = new BSConnectionFactory();
		return cf.getConnection(dsName);
		 	
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
	</code>
	 */

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
