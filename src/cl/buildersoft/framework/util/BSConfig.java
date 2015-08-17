package cl.buildersoft.framework.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class BSConfig extends BSDataUtils {
	public String getString(Connection conn, String key) {
		return getValue(conn, key);
	}

	public void setString(Connection conn, String key, String value) {
		setValue(conn, key, value);
	}

	public Integer getInteger(Connection conn, String key) {
		return Integer.parseInt(getValue(conn, key));
	}

	public void setInteger(Connection conn, String key, Integer value) {
		setValue(conn, key, value.toString());
	}

	public Double getDouble(Connection conn, String key) {
		return Double.parseDouble(getValue(conn, key));
	}

	public void setDouble(Connection conn, String key, Double value) {
		setValue(conn, key, value.toString());
	}

	public Boolean getBoolean(Connection conn, String key) {
		return Boolean.parseBoolean(getValue(conn, key));
	}

	public void setBoolean(Connection conn, String key, Boolean value) {
		setValue(conn, key, value.toString());
	}

	private String getValue(Connection conn, String key) {
		String sql = "SELECT cValue FROM tParameter WHERE cKey=?";
		return super.queryField(conn, sql, key);
	}

	private void setValue(Connection conn, String key, String value) {
		String sql = "UPDATE tParameter SET cValue=? WHERE cKey=?";
		List<Object> parameter = new ArrayList<Object>();
		parameter.add(value);
		parameter.add(key);
		super.update(conn, sql, parameter);
	}

	public char getCSVSeparator(Connection conn) {
		String seperator = getString(conn, "CSV_SEPARATOR");
		return seperator.toCharArray()[0];
	}

	public Integer getRecordsPerPage(Connection conn) {
		return getInteger(conn, "RECORDS_PER_PAGE");
	}

	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	public String getFilePath(Connection conn) {
		return fixPath(getString(conn, "EMPLOYEE_FILES"));
	}

	public String fixPath(String path) {
		String fileSeparator = BSConfig.getFileSeparator();
		if (path.lastIndexOf(fileSeparator) < path.length()) {
			path += fileSeparator;
		}
		return path;
	}
}
