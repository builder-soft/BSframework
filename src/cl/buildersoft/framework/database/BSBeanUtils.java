package cl.buildersoft.framework.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cl.buildersoft.framework.beans.BSBean;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.exception.BSSystemException;
import cl.buildersoft.framework.util.BSDataUtils;
import cl.buildersoft.framework.util.BSUtils;

//import static cl.buildersoft.framework.util.BSUtils.array2List;

public class BSBeanUtils extends BSDataUtils {

	public Long insert(Connection conn, BSBean bean) {
		Class<? extends BSBean> theClass = bean.getClass();

		String[] tableFields = getTableFields(theClass);
		String[] objectFields = getObjectFields(theClass);

		tableFields = deleteId(tableFields);
		objectFields = deleteId(objectFields);

		String sql = buildInsertSQLString(theClass, tableFields, bean);
		Object[] params = getValues4Insert(theClass, objectFields, bean);

		Long newId;
		newId = insert(conn, sql, BSUtils.array2List(params));

		String idField = getIdField(getObjectFields(theClass));
		fillField(theClass, idField, newId, bean);

		return newId;
	}

	public Integer update(Connection conn, BSBean bean) {
		Class<? extends BSBean> theClass = bean.getClass();
		String[] tableFields = getTableFields(theClass);
		String[] objectFields = getObjectFields(theClass);

		String sql = buildUpdateSQLString(theClass, tableFields, bean);

		Object[] params = getValues4Update(theClass, objectFields, bean);

		Integer out;
		out = update(conn, sql, BSUtils.array2List(params));

		return out;

	}

	public void save(Connection conn, BSBean bean) {
		if (update(conn, bean) == 0) {
			insert(conn, bean);
		}
	}

	public void delete(Connection conn, BSBean bean) {
		Class<? extends BSBean> c = bean.getClass();

		String[] objectFields = getObjectFields(c);
		Long idValue = (Long) getMethodValue(c, "get" + getIdField(objectFields), bean);

		String[] tableFields = getTableFields(c);
		String sql = buildDeleteSQLString(c, tableFields, bean);

		update(conn, sql, BSUtils.array2List(idValue));

		Object[] prms = new Object[objectFields.length];

		fillObject(c, objectFields, prms, bean);
	}

	public Boolean search(Connection conn, BSBean bean) {
		return search(conn, bean, null, new ArrayList<Object>());
	}

	public Boolean search(Connection conn, BSBean bean, String where, Object... parameters) {
		return search(conn, bean, where, BSUtils.array2List(parameters));
	}

	public List<? extends BSBean> listAll(Connection conn, BSBean bean) {
		return listAll(conn, bean, null);
	}

	public List<? extends BSBean> listAll(Connection conn, BSBean bean, String order) {
		Class<? extends BSBean> theClass = bean.getClass();
		List out = new ArrayList();

		String[] tableFields = getTableFields(theClass);
		String[] objectFields = getObjectFields(theClass);
		// String[] tableFieldsWithOutId = deleteId(tableFields);
		String tableName = getTableName(theClass, bean);

		String sql = buildSelectAllString(tableName, tableFields);
		if (order != null) {
			sql += buildOrderString(order);
		}

		ResultSet rs = queryResultSet(conn, sql, null);

		Object value = null;

		try {
			while (rs.next()) {
				Object object = bean.getClass().newInstance();
				for (String f : tableFields) {
					value = rs.getObject(f);
					fillField(theClass, f.substring(1, f.length()), value, object);
				}
				out.add(object);
			}
		} catch (Exception e) {
			throw new BSDataBaseException(e);
		}
		return out;
	}

	private String buildOrderString(String order) {
		return " ORDER BY " + order;
	}

	public List<? extends BSBean> list(Connection conn, BSBean bean, String where, Object... parameters) {
		Class<? extends BSBean> theClass = bean.getClass();
		List out = new ArrayList();

		String[] tableFields = getTableFields(theClass);
		// String[] objectFields = getObjectFields(theClass);
		// String[] tableFieldsWithOutId = deleteId(tableFields);
		String tableName = getTableName(theClass, bean);

		String sql = buildSelectSQLString(tableName, tableFields, tableFields, where);

		ResultSet rs = queryResultSet(conn, sql, BSUtils.array2List(parameters));

		Object value = null;

		try {
			while (rs.next()) {
				Object object = bean.getClass().newInstance();
				for (String f : tableFields) {
					value = rs.getObject(f);
					fillField(theClass, f.substring(1, f.length()), value, object);
				}
				out.add(object);
			}
		} catch (Exception e) {
			throw new BSDataBaseException(e);
		}
		return out;
	}

	public Boolean search(Connection conn, BSBean bean, String where, List<Object> parameters) {
		Class<? extends BSBean> theClass = bean.getClass();
		Boolean out = Boolean.FALSE;

		String[] tableFields = getTableFields(theClass);
		String[] objectFields = getObjectFields(theClass);
		String[] tableFieldsWithOutId = deleteId(tableFields);
		String[] fields = null;
		String tableName = getTableName(theClass, bean);

		String sql = null;
		if (where == null) {
			sql = buildSelectSQLString(tableName, tableFields, tableFieldsWithOutId);
			fields = tableFieldsWithOutId;
		} else {
			sql = buildSelectSQLString(tableName, tableFields, tableFields, where);
			fields = tableFields;
		}

		ResultSet rs;
		if (parameters == null || parameters.size() == 0) {
			Long idValue = getIdValue(theClass, "get" + getIdField(objectFields), bean);
			rs = queryResultSet(conn, sql, idValue);
		} else {
			rs = queryResultSet(conn, sql, parameters);
		}
		Object value = null;

		try {
			if (rs.next()) {
				for (String f : fields) {
					value = rs.getObject(f);
					// value = convertToCalendar(value);
					fillField(theClass, f.substring(1, f.length()), value, bean);
				}
				out = Boolean.TRUE;
			}
		} catch (SQLException e) {
			throw new BSDataBaseException("0300", e.getMessage());
		}
		return out;
	}

	// private Object convertToCalendar(Object value) {
	// Object out = null;
	// if (value instanceof java.util.Date || value instanceof java.sql.Date ||
	// value instanceof Timestamp) {
	// Calendar temp = Calendar.getInstance();
	// temp.setTimeInMillis(((java.util.Date) value).getTime());
	// out = temp;
	// } else {
	// out = value;
	// }
	// return out;
	// }

	private void fillObject(Class<? extends BSBean> c, String[] objectFields, Object[] params, BSBean bean) {
		Class objectClass = null;
		String fieldName = null;
		Object value = null;
		for (int i = 0; i < objectFields.length; i++) {
			objectClass = params[i] == null ? null : params[i].getClass();
			fieldName = objectFields[i];
			value = params[i];

			fillField(c, fieldName, value, bean);
		}
	}

	private void fillField(Class<? extends BSBean> c, String fieldName, Object value, Object bean) {
		/**
		 * <code>
		  http://www.roseindia.net/jdbc/jdbc-mysql/mapping-mysql-data-types-in-java.shtml
		 </code>
		 */
		Class<?> type = getTypeMethod(c, fieldName);
		Class[] paramTypes = new Class[] { type };
		Method method;
		try {
			method = c.getMethod("set" + fieldName, paramTypes);
			method.invoke(bean, value);
		} catch (Exception e) {
			throw new BSProgrammerException(e);
		}

	}

	private Class<?> getTypeMethod(Class<? extends BSBean> c, String methodName) {
		Method m;
		try {
			m = c.getMethod("get" + methodName, null);
		} catch (SecurityException e) {
			throw new BSSystemException(e);
		} catch (NoSuchMethodException e) {
			throw new BSProgrammerException(e);
		}
		Type type = m.getGenericReturnType();
		String typeString = type.toString();
		Class<?> out = null;

		if (typeString.indexOf("String") > -1) {
			out = "".getClass();
		} else if (typeString.indexOf("Integer") > -1) {
			out = Integer.class;
		} else if (typeString.indexOf("Double") > -1) {
			out = Double.class;
		} else if (typeString.indexOf("BigDecimal") > -1) {
			out = BigDecimal.class;
		} else if (typeString.indexOf("Long") > -1) {
			out = Long.class;
		} else if (typeString.indexOf("Boolean") > -1) {
			out = Boolean.class;
		} else if (typeString.indexOf("Timestamp") > -1) {
			out = Timestamp.class;
		} else if (typeString.indexOf("Date") > -1) {
			out = Date.class;
		} else {
			throw new BSProgrammerException("0110", "El tipo de dato '" + type + "' que retorna el m�todo '" + methodName
					+ "()', no fue encontrado");
		}

		return out;
	}

	private String buildSelectAllString(String tableName, String[] tableFields) {
		String sql = "SELECT " + unSplit(tableFields, ",") + " FROM " + tableName;
		return sql;
	}

	private String buildInsertSQLString(Class<? extends BSBean> c, String[] tableFields, BSBean bean) {
		String sql = "INSERT INTO " + getTableName(c, bean) + "(";

		sql += unSplit(tableFields, ",") + ") VALUES(";
		sql += getCommas(tableFields) + ");";
		return sql;
	}

	private String buildUpdateSQLString(Class<? extends BSBean> c, String[] tableFields, BSBean bean) {

		String id = getIdField(tableFields);
		tableFields = deleteId(tableFields);

		String sql = "UPDATE " + getTableName(c, bean) + " SET ";
		sql += unSplit(tableFields, "=?,");
		sql += " WHERE " + id + "=?";
		return sql;
	}

	private String buildDeleteSQLString(Class<? extends BSBean> c, String[] tableFields, BSBean bean) {
		String sql = "DELETE FROM " + getTableName(c, bean) + " WHERE " + getIdField(tableFields) + "=?";
		return sql;
	}

	private String buildSelectSQLString(String tableName, String[] tableFields, String[] tableFieldsWithOutId, String where) {
		String sql = "SELECT " + unSplit(tableFieldsWithOutId, ",") + " FROM " + tableName + " WHERE " + where;
		return sql;
	}

	private String buildSelectSQLString(String tableName, String[] tableFields, String[] tableFieldsWithOutId) {
		String sql = "SELECT " + unSplit(tableFieldsWithOutId, ",") + " FROM " + tableName + " WHERE " + getIdField(tableFields)
				+ "=?";
		return sql;
	}

	private String getIdField(String[] tableFields) {
		String out = "";
		for (String s : tableFields) {
			if (isId(s)) {
				out = s;
				break;
			}
		}
		return out;
	}

	private Long getIdValue(Class<? extends BSBean> c, String idFieldName, BSBean bean) {
		return (Long) getMethodValue(c, idFieldName, bean);

	}

	private Object[] getValues4Insert(Class<? extends BSBean> c, String[] tableFields, BSBean bean) {
		Object[] out = new Object[tableFields.length];
		Method method = null;
		int i = 0;
		for (String name : tableFields) {
			name = "get" + name;

			try {
				method = c.getMethod(name, null);
				out[i++] = method.invoke(bean, null);
			} catch (SecurityException e) {
				throw new BSSystemException("0210", e.getMessage());
			} catch (Exception e) {
				throw new BSProgrammerException("0110", e.getMessage());
			}

		}
		return out;
	}

	private Object[] getValues4Update(Class<? extends BSBean> c, String[] objectFields, BSBean bean) {
		Object[] out = new Object[objectFields.length];
		Object aux = null;
		Object value = null;
		String methodName = "";
		// Method method = null;
		int i = 0;
		for (String name : objectFields) {
			methodName = "get" + name;

			value = getMethodValue(c, methodName, bean);
			if (isId(name)) {
				aux = value;
			} else {
				out[i++] = value;
			}
		}
		out[i] = aux;

		return out;
	}

	private Object getMethodValue(Class<? extends BSBean> c, String methodName, BSBean bean) {
		Object value;
		Method method;
		try {
			method = c.getMethod(methodName, null);
			value = method.invoke(bean, null);
		} catch (Exception e) {
			throw new BSProgrammerException("0110", e.getMessage());
		}

		return value;
	}

	private String getCommas(String[] tableFields) {
		String out = "";
		for (int i = 0; i < tableFields.length; i++) {
			out += "?,";
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	private String[] deleteId(String[] tableFields) {
		String[] out = new String[tableFields.length - 1];
		int i = 0;
		for (String s : tableFields) {
			if (!isId(s)) {
				out[i++] = s;
			}
		}
		return out;
	}

	private boolean isId(String s) {
		return "id".equalsIgnoreCase(s) || "cid".equalsIgnoreCase(s);
	}

	private String unSplit(String[] tableFields, String s) {
		String out = "";
		for (String f : tableFields) {
			out += f + s;
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	private String[] getTableFields(Class<? extends BSBean> theClass) {
		return getFields(theClass, true);
	}

	private String[] getObjectFields(Class<? extends BSBean> c) {
		return getFields(c, false);
	}

	private String[] getFields(Class<? extends BSBean> c, boolean asFields) {
		String[] out = null;
		List<String> list = new ArrayList<String>();

		String pre = asFields ? "c" : "";

		String name = null;
		Method[] methods = getMethods(c);
		for (Method method : methods) {

			name = method.getName();
			if (name.startsWith("get")) {
				name = name.substring(3);
				list.add(pre + name);
			}
		}

		out = new String[list.size()];
		int i = 0;
		for (String o : list) {
			out[i++] = o;
		}

		return out;
	}

	private Method[] getMethods(Class<?> c) {
		List<Method> out = new ArrayList<Method>();

		out = array2List(c.getDeclaredMethods());

		if (!c.equals(Object.class)) {

			// List<Method> parentMethods =
			// array2List(c.getSuperclass().getDeclaredMethods());
			List<Method> parentMethods = array2List(getMethods(c.getSuperclass()));

			for (Method m : parentMethods) {
				if (!m.getName().equals("getClass")) {
					out.add(m);
				}
			}
		}

		return list2Array(out);
	}

	protected List<Method> array2List(Method... prms) {
		List<Method> out = new ArrayList<Method>();

		for (Method o : prms) {
			out.add(o);
		}

		return out;
	}

	protected Method[] list2Array(List<Method> prms) {
		Method[] out = new Method[prms.size()];

		int i = 0;
		for (Method o : prms) {
			out[i++] = o;
		}

		return out;
	}

	public Method getMethod(Class<?> clazz, String name) {
		Method m;
		try {
			m = clazz.getDeclaredMethod(name);
		} catch (Exception e) {
			throw new BSProgrammerException("0110", e.getMessage());
		}

		if (m == null) {
			if (clazz.equals(Object.class)) {
				throw new BSProgrammerException("0110", "Method not found in any super class.");
			}
			return getMethod(clazz.getSuperclass(), name);
		}
		return m;
	}

	private String getTableName(Class<? extends BSBean> c, BSBean instance) {
		String out = null;
		Field privateStringField = null;
		try {
			privateStringField = c.getDeclaredField("TABLE");
			privateStringField.setAccessible(true);
			out = (String) privateStringField.get(instance);
		} catch (Exception e) {
			throw new BSProgrammerException("0110", e.getMessage());
		}

		return out;
	}

}
