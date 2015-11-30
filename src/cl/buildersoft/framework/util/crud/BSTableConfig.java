package cl.buildersoft.framework.util.crud;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import cl.buildersoft.framework.dataType.BSDataTypeEnum;
import cl.buildersoft.framework.dataType.BSDataTypeFactory;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSUtils;

public class BSTableConfig {
	private final static Logger LOG = Logger.getLogger(BSTableConfig.class.getName());
	private String database = null;
	private String tableName = null;
	private String[] fields = null;
	private Map<String, BSField> fieldsMap = null;
	private String title = null;
	private String uri = null;
	private BSAction[] actions = null;
	private String sortField = null;
	private Map<String, String[]> fkInfo = null;
	// private Set<String> tablesCommon = null;
	private String pk = null;
	private String key = null;
	private String script = "";

	private String viewName = null;
	private String saveSP = null;
	private String deleteSP = null;

	public BSTableConfig(String database) {
		this.database = database;
	}

	public BSTableConfig(String database, String tableName) {
		this(database, tableName, null);
	}

	public BSTableConfig(String database, String tableName, String viewName) {
		this.database = database;
		this.fields = new String[0];
		this.fieldsMap = new HashMap<String, BSField>();
		this.actions = new BSAction[0];
		this.tableName = tableName;
		this.viewName = viewName;
		this.title = tableName;

		createInsert();
		createEdit();
		createDelete();
	}

	public String getDatabase() {
		return this.database;
	}

	public BSField[] getFKFields() {
		List<BSField> fkFields = new ArrayList<BSField>();
		BSField field = null;
		for (String f : fields) {
			field = getField(f);
			if (field.isFK()) {
				fkFields.add(field);
			}
		}
		BSField[] out = (BSField[]) fkFields.toArray();
		return out;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortFieldName) {
		this.sortField = sortFieldName;
	}

	public void setSortField(BSField field) {
		setSortField(field.getName());
	}

	private void createInsert() {
		BSAction insert = new BSAction("INSERT", BSActionType.Table);
		insert.setLabel("Nuevo");
		insert.setUrl("/servlet/common/NewRecord");
		this.addAction(insert);
	}

	private void createEdit() {
		BSAction edit = new BSAction("EDIT", BSActionType.Record);
		edit.setLabel("Modificar");
		edit.setUrl("/servlet/common/SearchRecord");
		this.addAction(edit);
	}

	private void createDelete() {
		BSAction delete = new BSAction("DELETE", BSActionType.MultiRecord);
		delete.setLabel("Borrar");
		delete.setUrl("/servlet/common/crud/DeleteRecords");
		this.addAction(delete);
	}

	public String getFieldsNames(BSField[] fields) {
		String out = "";
		if (fields.length == 0) {
			out = "*";
		} else {
			for (BSField field : fields) {
				out += field.getName() + ",";
			}
			out = out.substring(0, out.length() - 1);
		}
		return out;
	}

	public void configFields(Connection conn, BSmySQL mysql) {
		configBasic(conn, mysql);
		if (this.viewName == null) {
			configFKFields(conn, mysql);
		}

	}

	private void configBasic(Connection conn, BSmySQL mysql) {
		String sql = getSQL();
		ResultSet resultSet = mysql.queryResultSet(conn, sql, null);
		configBasic(conn, mysql, resultSet);
	}

	protected void configBasic(Connection conn, BSmySQL mysql, ResultSet resultSet) {
		BSField[] fields = getFields();

		ResultSetMetaData metaData;
		try {
			metaData = resultSet.getMetaData();
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

		String name = null;

		if (fields.length == 0) {
			Integer n;
			try {
				n = metaData.getColumnCount();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}

			BSField field = null;
			Boolean hasPK = Boolean.FALSE;
			n++;
			for (Integer i = 1; i < n; i++) {
				try {
					name = metaData.getColumnName(i);
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
				field = new BSField(name, name.substring(1));
				addField(field);
				configField(conn, metaData, name, i, field);
				if (field.isPK() && !hasPK) {
					hasPK = Boolean.TRUE;
				}
				try {
					field.setIsNullable(metaData.isNullable(i) == ResultSetMetaData.columnNullable);
				} catch (SQLException e) {
					throw new BSDataBaseException(e);
				}
			}

			if (!hasPK) {
				throw new BSProgrammerException("0104");
			}
		} else {
			Integer i = 1;
			for (BSField field : fields) {
				name = field.getName();

				configField(conn, metaData, name, i, field);

				i++;
			}
		}
	}

	private String getSQL() {
		String out = "SELECT ";
		if (getFields().length == 0) {
			out += "*";
		} else {
			out += unSplitFieldNames(",");
		}

		out += " FROM " + getDatabase() + "." + getTableOrViewName();
		out += " LIMIT 0,1";
		return out;
	}

	public String unSplitFieldNames(String s) {
		return unSplitFieldNames(getFields(), s);
	}

	public String unSplitFieldNames(BSField[] fields, String s) {
		String out = "";
		for (BSField f : fields) {
			out += f.getName() + s;
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	private String unSplitActionCodes(String s) {
		String out = "";
		for (BSAction f : getActions()) {
			out += f.getCode() + s;
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	private void configFKFields(Connection conn, BSmySQL mySQL) {
		BSField[] fields = getFields();

		String databaseFK = null;
		String tableFK = null;
		String fieldFK = null;

		for (BSField field : fields) {
			String[] pkTableInfo = getFKTableInfo(conn, field);

			if (pkTableInfo != null) {
				field.setFK(pkTableInfo[0], pkTableInfo[1], pkTableInfo[2]);

				databaseFK = field.getFKDatabase();
				tableFK = field.getFKTable();
				fieldFK = field.getFKField();

				if (tableFK != null && fieldFK != null) {
					String sql = "SELECT cId, cName ";
					sql += "FROM " + databaseFK + ".";
					sql += tableFK;
					// sql += field2Table(conn, field.getName());
					sql += " ORDER BY cName";

					ResultSet data = mySQL.queryResultSet(conn, sql, null);

					field.setFkData(mySQL.resultSet2Matrix(data));
				}
			}
		}
	}

	/**
	 * <code>
	private String field2Table(Connection conn, String fieldName) {
		String out = null;
		String table = "t" + fieldName.substring(1);
		String view = "";
		Boolean isExist = isExistInCommon(conn, table);
		if (!isExist) {
			view = "v" + fieldName.substring(1);
			isExist = isExistInCommon(conn, view);
			if (isExist) {
				out = view;
			}
		} else {
			out = table;
		}
		if (!isExist) {
			throw new BSProgrammerException("", "No existe la tabla '" + table + "' o vista '" + view + "'");
		}

		return out;
	}
</code>
	 */
	private String[] getFKTableInfo(Connection conn, BSField field) {
		/**
		 * <code>
		 * PKTABLE_CAT=bscommon 
		 * PKTABLE_SCHEM=null 
		 * PKTABLE_NAME=tboard
		 * PKCOLUMN_NAME=cId
		 * </code>
		 */
		DatabaseMetaData dbmd;
		ResultSet rs;
		String[] out = null;

		if (this.fkInfo == null) {
			this.fkInfo = new HashMap<String, String[]>();
			try {
				dbmd = (DatabaseMetaData) conn.getMetaData();
				rs = dbmd.getImportedKeys(null, null, getTableName());

				while (rs.next()) {
					String code = rs.getString("FKCOLUMN_NAME");
					String database = rs.getString("PKTABLE_CAT");
					String table = rs.getString("PKTABLE_NAME");
					String column = rs.getString("PKCOLUMN_NAME");

					String[] info = new String[3];
					info[0] = database;
					info[1] = table;
					info[2] = column;

					this.fkInfo.put(code, info);
				}

			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
		}

		out = this.fkInfo.get(field.getName());

		return out;
	}

	/**
	 * <code>
	private Boolean isExistInCommon(Connection conn, String table) {
		// Boolean out = Boolean.FALSE;
		if (this.tablesCommon == null) {
			this.tablesCommon = new HashSet<String>();

			Set<String> databases = databasesList(conn);

			try {
				Iterator<String> dataBasesIter = databases.iterator();

				while (dataBasesIter.hasNext()) {
					String database = (String) dataBasesIter.next();

					DatabaseMetaData dbmd = (DatabaseMetaData) conn.getMetaData();
					ResultSet tables = dbmd.getTables(database, null, null, null);

					while (tables.next()) {
						// ResultSetMetaData md = tables.getMetaData();
						this.tablesCommon.add(tables.getString("TABLE_NAME").toLowerCase());
					}
					tables.close();
				}
			} catch (SQLException e) {
				throw new BSDataBaseException("", e.getMessage());
			}
		}

		return this.tablesCommon.contains(table.toLowerCase());
	}

	private Set<String> databasesList(Connection conn) {
		Set<String> databases = new HashSet<String>();
		Iterator iterator = this.fkInfo.entrySet().iterator();
		String[] info = null;
		Map.Entry entry = null;
		while (iterator.hasNext()) {
			entry = (Map.Entry) iterator.next();
			info = (String[]) entry.getValue();
			databases.add(info[0]);
		}
		return databases;
	}
</code>
	 */
	protected void configField(Connection conn, ResultSetMetaData metaData, String name, Integer i, BSField field) {
		try {
			if (field.getType() == null) {
				setRealType(metaData, i, field);
			}
			if (field.isPK() == null) {
				BSField pk = getPKField(conn);
				if (pk != null) {
					field.setPK(pk.getName().equals(name));
				} else {
					field.setPK(Boolean.FALSE);
				}
			}
			if (field.getLength() == null) {
				field.setLength(metaData.getColumnDisplaySize(i));
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}

	}

	private void setRealType(ResultSetMetaData metaData, Integer i, BSField field) {

		String typeName;
		try {
			LOG.log(Level.FINE, "{0} - {1}",
					BSUtils.array2ObjectArray(metaData.getColumnClassName(i), metaData.getColumnTypeName(i)));
			typeName = metaData.getColumnTypeName(i);

		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
		BSDataTypeFactory dtf = new BSDataTypeFactory();

		if (typeName.equals("BIGINT")) {
			field.setType(dtf.create(BSDataTypeEnum.LONG));
		} else if (typeName.equals("VARCHAR") || typeName.equals("CHAR")) {
			field.setType(dtf.create(BSDataTypeEnum.STRING));
		} else if (typeName.equals("DATE")) {
			field.setType(dtf.create(BSDataTypeEnum.DATE));
		} else if (typeName.equals("TIMESTAMP")) {
			field.setType(dtf.create(BSDataTypeEnum.TIMESTAMP));
		} else if (typeName.equals("DOUBLE")) {
			field.setType(dtf.create(BSDataTypeEnum.DOUBLE));
		} else if (typeName.equals("BIT")) {
			field.setType(dtf.create(BSDataTypeEnum.BOOLEAN));
		} else if (typeName.equals("INT")) {
			field.setType(dtf.create(BSDataTypeEnum.INTEGER));
		} else {
			throw new BSProgrammerException("No está catalogado el tipo " + typeName
					+ ", verifique método BSTableConfig.setRealType()");
		}
	}

	public String getTableOrViewName() {
		String out = null;
		if (this.viewName != null) {
			out = this.viewName;
		} else {
			out = this.tableName;
		}
		return out;
	}

	public String getTableName() {
		return this.tableName;
	}

	public BSField[] getFields() {
		BSField[] out = new BSField[this.fields.length];
		Integer i = 0;
		for (String field : this.fields) {
			out[i++] = getField(field);
		}
		return out;
	}

	public BSAction[] getActions() {
		return this.actions;
	}

	/**
	 * public String [] getActionCodes() { BSAction [] actions = getActions();
	 * String [] names = new String [actions.length]; Integer i = 0;
	 * for(BSAction action: actions){ names[i++]=action.getCode(); }
	 * 
	 * return names; }
	 */
	public BSAction[] getActions(BSActionType actionType) {
		BSAction[] out = new BSAction[0];
		for (BSAction action : this.actions) {
			if (action.getActionType().equals(actionType)) {
				BSAction[] aux = new BSAction[out.length + 1];
				System.arraycopy(out, 0, aux, 0, out.length);
				aux[aux.length - 1] = action;
				out = aux;
			}
		}
		return out;
	}

	public void renameAction(String source, String target) {
		BSAction action = getAction(source);
		if (action == null) {
			throw new BSProgrammerException("Accion no encontrada, posibles: [" + unSplitActionCodes(",") + "]");
		}
		action.setCode(target);
	}

	public BSAction getAction(String code) {
		BSAction out = null;
		for (BSAction action : this.actions) {
			if (action.getCode().equals(code)) {
				out = action;
				break;
			}
		}
		return out;
	}

	public void addField(BSField field) {
		this.fieldsMap.put(field.getName(), field);

		String[] target = new String[this.fields.length + 1];
		System.arraycopy(this.fields, 0, target, 0, this.fields.length);
		target[target.length - 1] = field.getName();
		this.fields = target;
	}

	public void addAction(BSAction action) {
		BSAction[] target = new BSAction[this.actions.length + 1];
		System.arraycopy(this.actions, 0, target, 0, this.actions.length);
		target[target.length - 1] = action;
		this.actions = target;
	}

	public void removeField(String code) {
		String[] target = new String[this.fields.length - 1];
		Integer i = 0;
		for (String field : this.fields) {
			if (!code.equals(field)) {
				target[i++] = field;
			} else {
				this.fieldsMap.remove(code);
			}
		}
		this.fields = target;
	}

	public void removeAction(String code) {
		BSAction[] target = new BSAction[this.actions.length - 1];
		Integer i = 0;
		for (BSAction action : this.actions) {
			if (!action.getCode().equals(code)) {
				target[i++] = action;
			}
		}
		this.actions = target;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public BSField getKeyField(Connection conn) {
		String pk = getPKField(conn).getName();

		String fieldName = null;
		if (this.key == null) {
			DatabaseMetaData dbmd;
			try {
				dbmd = (DatabaseMetaData) conn.getMetaData();

				ResultSet rs = dbmd.getIndexInfo(getDatabase(), null, getTableName(), true, false);
				while (rs.next()) {
					fieldName = rs.getString("COLUMN_NAME");
					if (!pk.equals(fieldName)) {
						break;
					}
				}
				rs.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
			this.key = fieldName;
		}
		return getField(this.key);
	}

	public BSField getPKField(Connection conn) {
		String fieldName = null;
		BSField out = null;
		if (this.pk == null && this.viewName == null) {
			DatabaseMetaData dbmd;
			try {
				dbmd = (DatabaseMetaData) conn.getMetaData();

				ResultSet rs = dbmd.getPrimaryKeys(getDatabase(), null, getTableOrViewName());
				while (rs.next()) {
					fieldName = rs.getString("COLUMN_NAME");
				}
				rs.close();
			} catch (SQLException e) {
				throw new BSDataBaseException(e);
			}
			this.pk = fieldName;
			out = getField(this.pk);
		}
		return out;
	}

	public BSField getField(String name) {
		BSField out = this.fieldsMap.get(name);
		if (out == null) {
			LOG.log(Level.SEVERE, "Field {0} not found, the fields list is {1}", BSUtils.array2List(name, this.fieldsMap));
			throw new BSProgrammerException("Field '" + name + "' not found");
		}
		return out;
	}

	/**
	 * @deprecated Use getPKField()
	 */
	public BSField getIdField() {
		BSField[] fields = getFields();
		BSField out = null;

		if (fields.length == 0) {
			throw new BSProgrammerException("No se ha definido BSFields[] para la tabla " + getTableName());
		} else {
			for (BSField s : fields) {
				if (s.isId()) {
					out = s;
					break;
				}
			}
		}
		return out;
	}

	/**
	 * @deprecated Use deleteIdMap
	 */
	public BSField[] deleteId() {
		BSField[] out = new BSField[this.fields.length - 1];
		int i = 0;
		BSField field = null;
		for (String fieldName : this.fields) {
			field = getField(fieldName);
			if (!field.isId()) {
				out[i++] = field;
			}
		}
		return out;
	}

	public Map<String, BSField> deleteIdMap() {
		BSField[] out = this.deleteId();
		Map<String, BSField> mapField = new HashMap<String, BSField>();
		for (BSField s : out) {
			mapField.put(s.getName(), s);
		}
		return mapField;
	}

	public Boolean usingView() {
		return viewName != null;
	}

	public String getSaveSP() {
		return saveSP;
	}

	public void setSaveSP(String saveSP) {
		this.saveSP = saveSP;
	}

	public String getDeleteSP() {
		return deleteSP;
	}

	public void setDeleteSP(String deleteSP) {
		this.deleteSP = deleteSP;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

}
