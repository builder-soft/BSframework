package cl.buildersoft.framework.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.util.BSDataUtils;
import cl.buildersoft.framework.util.crud.BSPaging;
import cl.buildersoft.framework.util.crud.BSTableConfig;

public class BSmySQL extends BSDataUtils {
	CallableStatement callableStatement;

	private final static Logger LOG = Logger.getLogger(BSmySQL.class.getName());

	/**
	 * <code>
	 * 
	 * @Override public void closeSQL() { if (this.callableStatement != null) {
	 *           try { this.callableStatement.close(); } catch (SQLException e)
	 *           { throw new BSDataBaseException(e); } } super.closeSQL(); }
	 *           </code>
	 */
	public ResultSet queryResultSet(Connection conn, BSTableConfig table, BSPaging paging) {
		String sql = paging.getSQL(table);
		List<Object> prms = paging.getParams();

		if (paging.getRequiresPaging()) {
			sql += " LIMIT " + paging.getFirstRecord() + "," + paging.getRecordPerPage();
		}

		ResultSet rs = queryResultSet(conn, sql, prms);

		return rs;
	}

	public List<List<Object[]>> callComplexSP(Connection conn, String name, List<Object> parameter) {
		return callComplexSP(conn, name, parameter, false);
	}

	public List<List<Object[]>> callComplexSP(Connection conn, String name, List<Object> parameter, Boolean includeColumns) {
		String sqlStatement = getSQL4SP(name, parameter);

		List<List<Object[]>> out = new ArrayList<List<Object[]>>();
		CallableStatement localCallableStatement = null;
		ResultSet rs = null;
		try {
			localCallableStatement = conn.prepareCall(sqlStatement);
			parametersToStatement(parameter, localCallableStatement);

			// this.callableStatement.execute();
			// this.callableStatement = conn.prepareCall(sqlStatement);

			Boolean moreResults = Boolean.TRUE;

			Boolean isResultSet = localCallableStatement.execute();

			rs = localCallableStatement.getResultSet();
			// out.add(rs);
			// out.add(resultSet2Matrix(rs));

			while (moreResults) {
				if (isResultSet) {
					rs = localCallableStatement.getResultSet();
					// out.add(rs);
					out.add(resultSet2Matrix(rs, includeColumns));
					rs.close();
				} else {
					Integer rowsAffected = localCallableStatement.getUpdateCount();
					if (rowsAffected == -1) {
						moreResults = Boolean.FALSE;
					}
				}
				if (moreResults) {
					isResultSet = localCallableStatement.getMoreResults();
				}
			}
		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error executing query in callComplexSP, the commans name is '" + name + "', parameters are "
					+ breakDown(parameter), e);
			throw new BSDataBaseException(e);
		} finally {
			if (localCallableStatement != null) {
				try {
					localCallableStatement.close();
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}

		}
		return out;

	}

	private String breakDown(List<Object> parameter) {
		String out = "";
		Integer i = 0;
		if (parameter != null) {
			for (Object o : parameter) {
				out += "Parameter(" + ++i + ") : " + o.toString() + "\n";
			}
		}
		return out;
	}

	public ResultSet callSingleSP(Connection conn, String name, Object oneParameter) {
		List<Object> prms = new ArrayList<Object>();
		prms.add(oneParameter);
		return callSingleSP(conn, name, prms);
	}

	public String callFunction(Connection conn, String name, Object oneParameter) {
		List<Object> prms = new ArrayList<Object>();
		prms.add(oneParameter);
		return callFunction(conn, name, prms);

	}

	public String callFunction(Connection conn, String name, List<Object> parameter) {
		String sql = "{ ? = call " + name + " (" + getQuestionMarks(parameter) + ")}";
		String out = null;
		CallableStatement localCallableStatement = null;
		try {
			localCallableStatement = conn.prepareCall(sql);
			localCallableStatement.registerOutParameter(1, Types.OTHER);
			parametersToStatement(parameter, localCallableStatement, 1);

			localCallableStatement.execute();

			out = localCallableStatement.getString(1);

		} catch (SQLException e) {
			LOG.log(Level.SEVERE, "Error executing function en callFunction", e);
			throw new BSDataBaseException(e);
		} finally {
			if (localCallableStatement != null) {
				try {
					localCallableStatement.close();
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}
		}

		return out;
	}

	public ResultSet callSingleSP(Connection conn, String name, List<Object> parameter) {
		String sqlStatement = getSQL4SP(name, parameter);

		ResultSet out = null;
		// callableStatement = null;
		try {
			callableStatement = conn.prepareCall(sqlStatement);
			parametersToStatement(parameter, callableStatement);

			Boolean moreResults = Boolean.TRUE;
			Boolean isResultSet = callableStatement.execute();

			while (moreResults) {
				if (isResultSet) {
					out = callableStatement.getResultSet();
					moreResults = Boolean.FALSE;
				} else {
					Integer rowsAffected = callableStatement.getUpdateCount();
					if (rowsAffected == -1) {
						moreResults = Boolean.FALSE;
					}
				}
				if (moreResults) {
					isResultSet = callableStatement.getMoreResults();
				}
			}
		} catch (SQLException e) {
			if (parameter == null) {
				LOG.log(Level.SEVERE, "Error on '" + name + "' widthout parameters", e);
			} else {
				LOG.log(Level.SEVERE, "Error on '" + name + "' width " + parameter.toString(), e);
			}
			throw new BSDataBaseException(e);
		}
		return out;
	}

	private String getSQL4SP(String name, List<Object> parameter) {
		String questionMarks = getQuestionMarks(parameter);
		String sqlStatement = null;
		if (questionMarks != null /** && questionMarks.length() > 0 */
		) {
			sqlStatement = "{call " + name + "(" + questionMarks + ")}";
		} else {
			sqlStatement = "{call " + name + "}";
		}
		return sqlStatement;
	}

	private String getQuestionMarks(List<Object> prms) {
		String out = "";

		if (prms != null && prms.size() > 0) {
			out = "";

			Iterator<Object> iter = prms.iterator();
			while (iter.hasNext()) {
				out += "?,";
				iter.next();
			}
			out = out.substring(0, out.length() - 1);
		}
		return out;
	}

	public Map<Long, Map<String, Object>> resultSet2Map(ResultSet rs) {
		Map<Long, Map<String, Object>> out = new HashMap<Long, Map<String, Object>>();

		Integer i = 0;
		Integer colCount = 0;
		String[] colNames = null;
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			colCount = metaData.getColumnCount();
			colNames = new String[colCount - 1];
			String colName = null;

			for (i = 2; i <= colCount; i++) {
				colName = metaData.getColumnName(i);
				colNames[i - 2] = colName;
			}
		} catch (SQLException e) {
			throw new BSDataBaseException("Error al trabajar con la definicion de una tabla " + e.getMessage());
		}

		Map<String, Object> innerData = null;
		try {
			while (rs.next()) {
				Long key = rs.getLong(1);
				for (String colName : colNames) {
					innerData = new HashMap<String, Object>();
					innerData.put(colName, rs.getObject(colName));
					out.put(key, innerData);
				}
			}
		} catch (SQLException e) {
			throw new BSDataBaseException("Error al recorrer un ResultSet " + e.getMessage() + " " + e.getLocalizedMessage());
		}
		this.closeSQL(rs);

		return out;
	}

	public void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
	}

	private List<Object[]> resultSet2Matrix2(ResultSet rs) {
		List<Object[]> out = new ArrayList<Object[]>();

		Integer colCount = 0;
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			colCount = metaData.getColumnCount();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		Object[] innerArray = null;
		try {
			while (rs.next()) {
				innerArray = new Object[colCount];
				for (Integer i = 1; i <= colCount; i++) {
					innerArray[i - 1] = rs.getObject(i);
				}
				out.add(innerArray);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
		this.closeSQL(rs);

		return out;
	}

	public void closeSQL() {
		super.closeSQL();
		if (this.callableStatement != null) {
			try {
				this.callableStatement.close();
				this.callableStatement = null;
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
	}
}
