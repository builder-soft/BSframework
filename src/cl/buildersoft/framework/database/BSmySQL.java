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

import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.util.BSDataUtils;
import cl.buildersoft.framework.util.crud.BSPaging;
import cl.buildersoft.framework.util.crud.BSTableConfig;

public class BSmySQL extends BSDataUtils {
	CallableStatement callableStatement;

	@Override
	public void closeSQL() {
		if (this.callableStatement != null) {
			try {
				this.callableStatement.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}
		super.closeSQL();
	}

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

		try {
			this.callableStatement = conn.prepareCall(sqlStatement);
			parametersToStatement(parameter, callableStatement);

			// this.callableStatement.execute();
			// this.callableStatement = conn.prepareCall(sqlStatement);

			Boolean moreResults = Boolean.TRUE;

			Boolean isResultSet = this.callableStatement.execute();

			ResultSet rs = this.callableStatement.getResultSet();
			// out.add(rs);
			// out.add(resultSet2Matrix(rs));

			while (moreResults) {
				if (isResultSet) {
					rs = this.callableStatement.getResultSet();
					// out.add(rs);
					out.add(resultSet2Matrix(rs, includeColumns));
					rs.close();
				} else {
					Integer rowsAffected = this.callableStatement.getUpdateCount();
					if (rowsAffected == -1) {
						moreResults = Boolean.FALSE;
					}
				}
				if (moreResults) {
					isResultSet = this.callableStatement.getMoreResults();
				}
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
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
		try {
			this.callableStatement = conn.prepareCall(sql);
			this.callableStatement.registerOutParameter(1, Types.OTHER);
			parametersToStatement(parameter, this.callableStatement, 1);

			this.callableStatement.execute();

			out = this.callableStatement.getString(1);

		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		/*
		 * or even "{? = function_1 (?, ?, ?, ?, ?)}"
		 * 
		 * and I have the following code:
		 * 
		 * CallableStatement cs = conn.prepareCall(sql);
		 * cs.registerOutParameter(1, Types.BIGINT);
		 * 
		 * cs.setLong(2,long1); cs.setString(3,string1);
		 * cs.setString(4,string2); cs.setString(5,string3); cs.setInt(6,int1);
		 */

		return out;
	}

	public ResultSet callSingleSP(Connection conn, String name, List<Object> parameter) {
		String sqlStatement = getSQL4SP(name, parameter);

		ResultSet out = null;

		try {
			this.callableStatement = conn.prepareCall(sqlStatement);
			parametersToStatement(parameter, callableStatement);

			Boolean moreResults = Boolean.TRUE;
			Boolean isResultSet = this.callableStatement.execute();

			while (moreResults) {
				if (isResultSet) {
					out = this.callableStatement.getResultSet();
					moreResults = Boolean.FALSE;
				} else {
					Integer rowsAffected = this.callableStatement.getUpdateCount();
					if (rowsAffected == -1) {
						moreResults = Boolean.FALSE;
					}
				}
				if (moreResults) {
					isResultSet = this.callableStatement.getMoreResults();
				}
			}
		} catch (SQLException e) {
			System.out.println("Error on '" + name + "' width " + parameter.toString());
			throw new BSDataBaseException(e);
		}
		return out;
	}

	private String getSQL4SP(String name, List<Object> parameter) {
		String questionMarks = getQuestionMarks(parameter);
		String sqlStatement = null;
		if (questionMarks != null) {
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

	public List<Object[]> resultSet2Matrix2(ResultSet rs) {
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
}
