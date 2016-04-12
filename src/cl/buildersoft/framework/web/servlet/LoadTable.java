package cl.buildersoft.framework.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger LOG = Logger.getLogger(LoadTable.class.getName());
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

		// LOG.log(Level.INFO, "Context param: {0}",
		// request.getServletContext().getInitParameter("CurrentVersion"));

		BSmySQL mysql = new BSmySQL();

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection(request);
		try {
			table.configFields(conn, mysql);

			BSPaging paging = new BSPaging(conn, mysql, table, request);
			ResultSet rs = mysql.queryResultSet(conn, table, paging);
			table.setData(mysql.resultSet2Matrix(rs, false));

			// request.setAttribute("Data", rs);
			// request.setAttribute("Conn", conn);
			synchronized (session) {
				session.setAttribute("Paging", paging);
				session.setAttribute("Search", paging.getSearchValue(request));
				session.setAttribute("BSTable", table);
			}
		} finally {
			cf.closeConnection(conn);
		}

		if (!"DALEA_CONTEXT".equals(getApplicationValue(request, "CurrentContext"))) {
//			LOG.log(Level.INFO, "save session and redirect");
			String daleaContext = getApplicationValue(request, "DALEA_CONTEXT").toString();
			String url = daleaContext + "/servlet/RedirectServlet?URL="
					+ URLEncoder.encode("/jsp/common/crud/main2.jsp", "UTF-8");
			redirect(request, response, url);
		} else {
//			LOG.log(Level.INFO, "forward");
			forward(request, response, "/jsp/common/crud/main2.jsp", false);
		}
		// forward(request, response, "/WEB-INF/jsp/common/main2.jsp");

	}
}
