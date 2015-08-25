package cl.buildersoft.framework.util.crud;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import cl.buildersoft.framework.dataType.BSDataTypeEnum;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.util.BSConfig;

public class BSPaging {
	private Boolean requiresPaging = null;
	private Integer recordPerPage = null;
	private Integer recordCount = null;
	private Integer currentPage = null;
	private Integer pageCount = null;
	private Integer firstRecord = null;
	private String search = null;

	// private Integer lastRecord = null;

	public BSPaging(Connection conn, BSmySQL mysql, BSTableConfig table, HttpServletRequest request) {
		// ServletContext context = request.getServletContext();
		this.search = getSearchValue(request);
		this.currentPage = getCurrentPage(request);
		this.recordCount = recordCount(conn, mysql, table);
		this.recordPerPage = getRecordsPerPage(conn);
		this.requiresPaging = requiresPaging();
		this.pageCount = calculatePageCount(this.recordCount, this.recordPerPage);
		this.firstRecord = this.currentPage == 1 ? 0 : ((this.currentPage - 1) * recordPerPage);
	}

	public String getSearchValue(HttpServletRequest request) {
		String out = null;
		if (this.search != null) {
			out = this.search;
		} else {
			out = request.getParameter("Search");
			if (out == null) {
				out = "";
			} else {
				out = out.trim();
			}
		}
		return out;
	}

	private Integer calculatePageCount(Integer recordCount, Integer recordPerPage) {
		Integer out = recordCount / recordPerPage;
		out += recordCount % recordPerPage > 0 ? 1 : 0;
		return out;
	}

	private Integer getCurrentPage(HttpServletRequest request) {
		String currentPageString = request.getParameter("Page");
		Integer currentPageInteger;
		try {
			currentPageInteger = Integer.parseInt(currentPageString);
		} catch (NumberFormatException e) {
			currentPageInteger = new Integer(1);
		}
		return currentPageInteger;
	}

	private Integer recordCount(Connection conn, BSmySQL mysql, BSTableConfig table) {
		String sql = getSQLCount(table);
		Integer out = Integer.parseInt(mysql.queryField(conn, sql, getParams()));
		mysql.closeSQL();
		return out;
	}

	private String getSQLCount(BSTableConfig table) {
		BSField[] fields = table.getFields();

		String fieldName = "1";
		if (fields.length > 0) {
			BSField idField = table.getIdField();
			fieldName = idField.getName();
		}

		String out = "SELECT COUNT(" + fieldName + ") AS cCount ";
		out += "FROM " + table.getDatabase() + "." + table.getTableOrViewName();
		out += getWhere(table);

		return out;
	}

	public String getSQL(BSTableConfig table) {
		String sql = "SELECT " + unSplit(table, ",", false);
		sql += " FROM " + table.getDatabase() + "." + table.getTableOrViewName();
		sql += getWhere(table);
		sql += getOrder(table);

		return sql;
	}

	private String getOrder(BSTableConfig table) {
		return table.getSortField() != null ? " ORDER BY " + table.getSortField() : "";
	}

	private String getWhere(BSTableConfig table) {
		String out = "";
		if (!this.search.equals("")) {
			BSField[] fields = table.getFields();
			if (fields.length > 0) {
				out = " WHERE CONCAT(" + unSplit(table, ",", true, true) + ") LIKE ?";
			}
		}
		return out;
	}

	protected String unSplit(BSTableConfig table, String separeitor, Boolean excludeBoolean) {
		return unSplit(table, separeitor, excludeBoolean, false);
		/**
		 * <code>
		String out = "";
		Boolean useField = null;
		for (BSField f : table.getFields()) {
			if (excludeBoolean) {
				useField = !f.getType().equals(BSFieldType.Boolean) && !f.getType().equals(BSFieldType.Text);
				if (useField) {
					out += "IFNULL(" + f.getName() + ",'')" + separeitor;
					// out += f.getName() + separeitor;
				}
			} else {
				out += "IFNULL(" + f.getName() + ",'')" + separeitor;
				// out += f.getName() + separeitor;
			}
		}
		out = out.substring(0, out.length() - 1);
		return out;
		</code>
		 */
	}

	protected String unSplit(BSTableConfig table, String separeitor, Boolean excludeBoolean, Boolean validateNull) {
		String out = "";
		Boolean useField = null;
		for (BSField f : table.getFields()) {
			if (excludeBoolean) {
				useField = !f.getType().equals(BSDataTypeEnum.BOOLEAN) && !f.getType().equals(BSDataTypeEnum.TEXT);
				if (useField) {
					if (validateNull) {
						out += "IFNULL(" + f.getName() + ",'')" + separeitor;
					} else {
						out += f.getName() + separeitor;
					}
				}
			} else {
				if (validateNull) {
					out += "IFNULL(" + f.getName() + ",'')" + separeitor;
				} else {
					out += f.getName() + separeitor;
				}
			}
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	private Boolean requiresPaging() {
		return this.recordCount > this.recordPerPage;
	}

	@Deprecated
	private Integer getRecordsPerPage(ServletContext context) {
		String recordPerPageString = context.getInitParameter("bsframework.recordPerPage");
		Integer recordPerPageInteger;
		try {
			recordPerPageInteger = Integer.parseInt(recordPerPageString);
		} catch (NumberFormatException e) {
			recordPerPageInteger = new Integer(20);
		}
		return recordPerPageInteger;
	}

	private Integer getRecordsPerPage(Connection conn) {
		BSConfig config = new BSConfig();
		return config.getInteger(conn, "RECORDS_PER_PAGE");
	}

	/**********************/
	/**
	 * private BSField getIdField(BSField[] tableFields) { BSField out = null;
	 * if (tableFields.length == 0) {
	 * 
	 * } for (BSField s : tableFields) { if (s.isId()) { out = s; break; } }
	 * return out; }
	 */
	public Boolean getRequiresPaging() {
		return requiresPaging;
	}

	public void setRequiresPaging(Boolean requiresPaging) {
		this.requiresPaging = requiresPaging;
	}

	public Integer getRecordPerPage() {
		return recordPerPage;
	}

	public void setRecordPerPage(Integer recordPerPage) {
		this.recordPerPage = recordPerPage;
	}

	public Integer getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(Integer recordCount) {
		this.recordCount = recordCount;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Integer getFirstRecord() {
		return firstRecord;
	}

	public void setFirstRecord(Integer firstRecord) {
		this.firstRecord = firstRecord;
	}

	public List<Object> getParams() {
		List<Object> out = null;
		if (!this.search.equals("")) {
			out = new ArrayList<Object>();
			out.add("%" + this.search + "%");
		}
		return out;
	}
}
