package cl.buildersoft.framework.database;

import static cl.buildersoft.framework.util.BSUtils.array2List;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSDataUtils;

public abstract class BSTableManager extends BSDataUtils {
	private Connection connection = null;
	private String tableName = null;
	Class<? extends BSTableManager> theClass = null;

	public BSTableManager() {
		this.theClass = this.getClass();
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected Connection getConnection() {
		return this.connection;
	}

	public Long insert() {
		this.theClass = this.getClass();

		String[] tableFields = getTableFields(theClass);
		String[] objectFields = getObjectFields(theClass);

		tableFields = deleteId(tableFields);
		objectFields = deleteId(objectFields);

		String sql = buildInsertSQLString(theClass, tableFields);
		Object[] params = getValues4Insert(theClass, objectFields);

		Long newId;
		newId = insert(this.connection, sql, array2List(params));

		String idField = getIdField(getObjectFields(theClass));
		fillField(theClass, idField, newId);

		return newId;
	}

	public Integer update() {
		String[] tableFields = getTableFields(this.theClass);
		String[] objectFields = getObjectFields(this.theClass);

		String sql = buildUpdateSQLString(this.theClass, tableFields);

		Object[] params = getValues4Update(this.theClass, objectFields);

		Integer out;
		out = update(this.connection, sql, array2List(params));

		return out;

	}

	public void save() {
		if (update() == 0) {
			insert();
		}

	}

	public void delete() {
		Class<? extends BSTableManager> c = this.getClass();

		String[] objectFields = getObjectFields(c);
		Integer idValue = (Integer) getMethodValue(c, "get" + getIdField(objectFields));

		String[] tableFields = getTableFields(c);
		String sql = buildDeleteSQLString(c, tableFields);

		update(this.connection, sql, array2List(idValue));

		Object[] prms = new Object[objectFields.length];

		fillObject(c, objectFields, prms);
	}

	public Boolean search() {
		Boolean out = Boolean.FALSE;

		String[] tableFields = getTableFields(this.theClass);
		String[] objectFields = getObjectFields(this.theClass);
		String[] tableFieldsWithOutId = deleteId(tableFields);

		String sql = buildSelectSQLString(tableFields, tableFieldsWithOutId);

		try {
			Long idValue = getIdValue(this.theClass, "get" + getIdField(objectFields));
			ResultSet rs;
			rs = this.queryResultSet(this.connection, sql, idValue);
			Object value = null;
			if (rs.next()) {

				for (String f : tableFieldsWithOutId) {
					value = rs.getObject(f);
					fillField(this.theClass, f.substring(1, f.length()), value);
				}
				out = Boolean.TRUE;
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		return out;
	}

	private void fillObject(Class<? extends BSTableManager> c, String[] objectFields, Object[] params) {
//		Class objectClass = null;
		String fieldName = null;
		Object value = null;
		for (int i = 0; i < objectFields.length; i++) {
//			objectClass = params[i] == null ? null : params[i].getClass();
			fieldName = objectFields[i];
			value = params[i];

			fillField(c, fieldName, value);

		}

	}

	private void fillField(Class<? extends BSTableManager> c, String fieldName, Object value) {
		Class<?> type = getTypeMethod(c, fieldName);
		@SuppressWarnings("rawtypes")
		Class[] paramTypes = new Class[] { type };
		Method method;
		try {
			method = c.getMethod("set" + fieldName, paramTypes);
			method.invoke(this, value);
		} catch (SecurityException e) {
			throw new BSProgrammerException(e);
		} catch (NoSuchMethodException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalArgumentException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalAccessException e) {
			throw new BSProgrammerException(e);
		} catch (InvocationTargetException e) {
			throw new BSProgrammerException(e);
		}

	}

	private Class<?> getTypeMethod(Class<? extends BSTableManager> c, String methodName) {

		Method m;
		try {
			m = c.getMethod("get" + methodName, null);
		} catch (SecurityException e) {
			throw new BSProgrammerException(e);
		} catch (NoSuchMethodException e) {
			throw new BSProgrammerException(e);
		}
		Type type = m.getGenericReturnType();

		Class<?> out = null;

		if (type.toString().indexOf("String") > -1) {
			out = "".getClass();
		} else if (type.toString().indexOf("Integer") > -1) {
			out = Integer.class;
		} else if (type.toString().indexOf("Double") > -1) {
			out = Double.class;
		} else if (type.toString().indexOf("Long") > -1) {
			out = Long.class;
		} else if (type.toString().indexOf("Boolean") > -1) {
			out = Boolean.class;
		} else if (type.toString().indexOf("Calendar") > -1) {
			out = Calendar.class;
		} else if (type.toString().equals("java.util.Date")) {
			out = Date.class;
		} else {
			throw new BSProgrammerException("El tipo de dato " + type.toString() + " no está soportado");
		}

		return out;
	}

	private String buildInsertSQLString(Class<? extends BSTableManager> c, String[] tableFields) {
		String sql = "INSERT INTO " + getTableName(c) + "(";

		sql += unSplit(tableFields, ",") + ") VALUES(";
		sql += getCommas(tableFields) + ");";
		return sql;
	}

	private String buildUpdateSQLString(Class<? extends BSTableManager> c, String[] tableFields) {

		String id = getIdField(tableFields);
		tableFields = deleteId(tableFields);

		String sql = "UPDATE " + getTableName(c) + " SET ";
		sql += unSplit(tableFields, "=?,");
		sql += " WHERE " + id + "=?";
		return sql;
	}

	private String buildDeleteSQLString(Class<? extends BSTableManager> c, String[] tableFields) {

		String sql = "DELETE FROM " + getTableName(c) + " WHERE " + getIdField(tableFields) + "=?";

		return sql;
	}

	private String buildSelectSQLString(String[] tableFields, String[] tableFieldsWithOutId) {

		String sql = "SELECT " + unSplit(tableFieldsWithOutId, ",") + " FROM " + getTableName(this.theClass) + " WHERE "
				+ getIdField(tableFields) + "=?";

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

	private Long getIdValue(Class<? extends BSTableManager> c, String idFieldName) {
		return (Long) getMethodValue(c, idFieldName);

	}

	private Object[] getValues4Insert(Class<? extends BSTableManager> c, String[] tableFields) {
		Object[] out = new Object[tableFields.length];
		Method method = null;
		int i = 0;
		for (String name : tableFields) {
			name = "get" + name;
			try {
				method = c.getMethod(name, null);
				out[i++] = method.invoke(this, null);
			} catch (SecurityException e) {
				throw new BSProgrammerException(e);
			} catch (NoSuchMethodException e) {
				throw new BSProgrammerException(e);
			} catch (IllegalArgumentException e) {
				throw new BSProgrammerException(e);
			} catch (IllegalAccessException e) {
				throw new BSProgrammerException(e);
			} catch (InvocationTargetException e) {
				throw new BSProgrammerException(e);
			}

		}
		return out;
	}

	private Object[] getValues4Update(Class<? extends BSTableManager> c, String[] objectFields) {

		Object[] out = new Object[objectFields.length];
		Object aux = null;
		Object value = null;
		String methodName = "";
		// Method method = null;
		int i = 0;
		for (String name : objectFields) {
			methodName = "get" + name;

			value = getMethodValue(c, methodName);
			if (isId(name)) {
				aux = value;
			} else {
				out[i++] = value;
			}
		}
		out[i] = aux;

		return out;
	}

	private Object getMethodValue(Class<? extends BSTableManager> c, String methodName) {
		Object value;
		Method method;
		try {
			method = c.getMethod(methodName, null);
			value = method.invoke(this, null);
		} catch (SecurityException e) {
			throw new BSProgrammerException(e);
		} catch (NoSuchMethodException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalArgumentException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalAccessException e) {
			throw new BSProgrammerException(e);
		} catch (InvocationTargetException e) {
			throw new BSProgrammerException(e);
		}

		return value;
	}

	public String getCommas(String[] tableFields) {
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

	private String[] getTableFields(Class<? extends BSTableManager> c) {
		return getFields(c, true);
	}

	private String[] getObjectFields(Class<? extends BSTableManager> c) {
		return getFields(c, false);
	}

	private String[] getFields(Class<? extends BSTableManager> c, boolean asFields) {
		String[] out = null;
		List<String> list = new ArrayList<String>();

		String pre = asFields ? "c" : "";

		String name = null;
		Method[] methods = c.getDeclaredMethods();
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

	private String getTableName(Class<? extends BSTableManager> c) {
		String out = null;
		if (this.tableName == null) {
			Field privateStringField;
			try {
				privateStringField = c.getDeclaredField("TABLE");
				privateStringField.setAccessible(true);
				out = (String) privateStringField.get(this);

			} catch (SecurityException e) {
				throw new BSProgrammerException(e);
			} catch (NoSuchFieldException e) {
				throw new BSProgrammerException(e);
			} catch (IllegalArgumentException e) {
				throw new BSProgrammerException(e);
			} catch (IllegalAccessException e) {
				throw new BSProgrammerException(e);
			}

			this.tableName = out;
		}

		return out;
	}

}
