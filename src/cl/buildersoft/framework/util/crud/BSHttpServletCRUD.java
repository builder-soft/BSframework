package cl.buildersoft.framework.util.crud;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cl.buildersoft.framework.beans.Domain;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.type.Semaphore;
import cl.buildersoft.framework.web.servlet.BSHttpServlet_;

public abstract class BSHttpServletCRUD extends BSHttpServlet_ implements Serializable {
	private static final Logger LOG = Logger.getLogger(BSHttpServletCRUD.class.getName());
	private static final long serialVersionUID = BSHttpServletCRUD.class.getName().hashCode();

	protected abstract BSTableConfig getBSTableConfig(HttpServletRequest request);

	public abstract Semaphore setSemaphore(Connection conn, Object[] values);

//	public abstract void preExecuteAction(BSTableConfig table, String action, Long userId);

	protected abstract void configEventLog(BSTableConfig table, Long userId);

//	public abstract void postExecuteAction(BSTableConfig table, String action, Long userId);

	// public abstract String getBusinessClass();
	// public abstract void writeEventLog(Connection conn, String action,
	// HttpServletRequest request, BSTableConfig table);

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BSTableConfig table = getBSTableConfig(request);
		configEventLog(table, getCurrentUser(request).getId());

		String uri = null; // request.getServletContext().getAttribute("DALEA_CONTEXT").toString();
							//
							//
							// request.getRequestURI().substring(request.getContextPath().length());
		// LOG.log(Level.FINE,
		// request.getRequestURI().substring(request.getContextPath().length()));
		// LOG.log(Level.FINE, request.getRequestURI());
		// LOG.log(Level.FINE,
		// request.getServletContext().getInitParameter("CurrentContext"));

		// uri = request.getRequestURI();
		uri = request.getRequestURI().substring(request.getContextPath().length());

		String currentContext = request.getServletContext().getAttribute("CurrentContext").toString();
		String lastContext = readLastContext(request);
		table.setContext(currentContext);

		verifyContextOfActions(table.getActions());

		table.setUri(uri);

		HttpSession session = request.getSession(false);
		synchronized (session) {
			session.setAttribute("BSTable", table);
		}

		forward(request, response, "/servlet/common/LoadTable", !lastContext.equals(currentContext));

	}

	private void verifyContextOfActions(BSAction[] actions) {
		String context = null;
		for (BSAction action : actions) {
			context = action.getContext();
			if (context == null || context.length() == 0) {
				throw new BSProgrammerException("Action '" + action.getCode() + "' widthout context defined");
			}
		}
	}

	protected BSTableConfig initTable(HttpServletRequest request, String tableName) {
		return initTable(request, tableName, null);
	}

	protected BSTableConfig initTable(HttpServletRequest request, String tableName, BSHttpServletCRUD servlet) {
		return initTable(request, null, tableName, servlet);
	}

	protected BSTableConfig initTable(HttpServletRequest request, String database, String tableName, BSHttpServletCRUD servlet) {
		String databaseName = null;
		HttpSession session = request.getSession(false);
		if (database == null) {
			Domain domain = (Domain) session.getAttribute("Domain");
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
//			session.setAttribute("ServletManager", servlet);
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
