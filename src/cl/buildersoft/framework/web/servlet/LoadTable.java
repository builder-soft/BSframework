package cl.buildersoft.framework.web.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.util.BSConnectionFactory;
import cl.buildersoft.framework.util.crud.BSPaging;
import cl.buildersoft.framework.util.crud.BSTableConfig;

/**
 * Servlet implementation class LoadTable
 */
@WebServlet("/servlet/common/LoadTable")
public class LoadTable extends BSHttpServlet_ {
	private static final long serialVersionUID = -2257837165074641521L;

	public LoadTable() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		BSTableConfig table = null;
		synchronized (session) {
			table = (BSTableConfig) session.getAttribute("BSTable");
		}

		BSmySQL mysql = new BSmySQL();

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection(request);
		try {
			table.configFields(conn, mysql);

			BSPaging paging = new BSPaging(conn, mysql, table, request);
			ResultSet rs = mysql.queryResultSet(conn, table, paging);

			request.setAttribute("Data", rs);
			request.setAttribute("Conn", conn);
			request.setAttribute("Paging", paging);
			request.setAttribute("Search", paging.getSearchValue(request));

			synchronized (session) {
				session.setAttribute("BSTable", table);
			}
		} finally {
//			cf.closeConnection(conn);
		}
		forward(request, response, "/WEB-INF/jsp/common/main2.jsp");

	}
}
