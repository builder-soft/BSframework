package cl.buildersoft.framework.util.crud;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cl.buildersoft.framework.beans.Domain;
import cl.buildersoft.framework.beans.LogInfoBean;
import cl.buildersoft.framework.business.services.EventLogService;
import cl.buildersoft.framework.business.services.ServiceFactory;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.type.Semaphore;
import cl.buildersoft.framework.util.BSUtils;
import cl.buildersoft.framework.web.servlet.BSHttpServlet_;

public abstract class BSHttpServletCRUD extends BSHttpServlet_ {
	private static final long serialVersionUID = BSHttpServletCRUD.class.getName().hashCode();

	protected abstract BSTableConfig getBSTableConfig(HttpServletRequest request);

	protected abstract Semaphore setSemaphore(Connection conn, Object[] values);

	protected abstract void configEventLog(BSTableConfig table, Long userId);

	// public abstract String getBusinessClass();
	// public abstract void writeEventLog(Connection conn, String action,
	// HttpServletRequest request, BSTableConfig table);

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BSTableConfig table = getBSTableConfig(request);

		String uri = request.getRequestURI().substring(request.getContextPath().length());

		table.setUri(uri);

		HttpSession session = request.getSession(false);
		synchronized (session) {
			session.setAttribute("BSTable", table);
		}

		forward(request, response, "/servlet/common/LoadTable");

	}

	protected BSTableConfig initTable(HttpServletRequest request, String tableName) {
		return initTable(request, tableName, null);
	}

	protected BSTableConfig initTable(HttpServletRequest request, String tableName, BSHttpServletCRUD servlet) {
		return initTable(request, null, tableName, servlet);
	}

	protected BSTableConfig initTable(HttpServletRequest request, String database, String tableName, BSHttpServletCRUD servlet) {
		String databaseName = null;
		if (database == null) {
			Domain domain = (Domain) request.getSession(false).getAttribute("Domain");
			databaseName = domain.getDatabase();
		} else {
			databaseName = database;
		}

		BSTableConfig table = new BSTableConfig(databaseName, tableName);
		BSmySQL mysql = new BSmySQL();
		Connection conn = getConnection(request);
		table.configFields(conn, mysql);
		mysql.closeConnection(conn);

		if (servlet != null) {
			request.setAttribute("ServletManager", servlet);
		}
		return table;
	}

	protected void hideFields(BSTableConfig table, String... hideFields) {
		for (String fieldName : hideFields) {
			table.getField(fieldName).setShowInTable(false);
		}
	}

	protected String getFieldsNamesWithCommas(BSField[] fields) {
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

}
