package cl.buildersoft.framework.util;

import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cl.buildersoft.framework.beans.User;
import cl.buildersoft.framework.database.BSmySQL;

public class BSHttpServlet extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(BSHttpServlet.class.getName());
	private static final long serialVersionUID = 7807647668104655759L;

	protected Connection getConnection(HttpServletRequest request) {
		BSConnectionFactory cf = new BSConnectionFactory();
		
		return cf.getConnection(request);
		
		/**<code>
		Object connObject = request.getAttribute("Connection");
		Connection out = null;
		if (connObject == null) {
			BSmySQL mysql = new BSmySQL();
			out = mysql.getConnection(request);
			request.setAttribute("Connection", out);

		} else {
			out = (Connection) connObject;
			try {
				if (out.isClosed()) {
					request.setAttribute("Connection", null);
					out = getConnection(request);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new BSDataBaseException(e);
			}
		}
		return out;		
		</code>*/
	}

	protected void closeConnection(Connection conn) {
		(new BSmySQL()).closeConnection(conn);
	}

	protected void setApplicationValue(HttpServletRequest request, String name, Object object) {
		request.getServletContext().setAttribute(name, object);
	}

	protected Object getApplicationValue(HttpServletRequest request, String name) {
		return request.getServletContext().getAttribute(name);
	}

	protected void setSessionValue(HttpServletRequest request, String name, Object object) {
		request.getSession(false).setAttribute(name, object);
	}

	protected Object getSessionValue(HttpServletRequest request, String name) {
		return request.getSession(false).getAttribute(name);
	}

	protected void forward(HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException,
			IOException {
		request.getRequestDispatcher(uri).forward(request, response);
	}

	protected String readParameterOrAttribute(HttpServletRequest request, String name) {
		String out = null;
		Object object = request.getAttribute(name);
		if (object != null) {
			out = (String) object;
		} else {
			out = request.getParameter(name);
		}
		return out;
	}

	protected User getCurrentUser(HttpServletRequest request) {
		return (User) request.getSession(false).getAttribute("User");
	}

	protected void showParameters(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		String name = null;
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();

			LOG.log(Level.CONFIG, "Name={0}", request.getParameter(name));

		}
	}

	protected Boolean bootstrap(Connection conn) {/*
String out = getServletContext().getAttribute("bsframework.gui.bootstrap") .toString() ;
LOG.log(Level.INFO, "Bootrstap config as {0}",out);
		return Boolean.parseBoolean(out);
		*/
		Boolean bootstrap = false;
		BSConfig config = new BSConfig();
		bootstrap = config.getBoolean(conn, "BOOTSTRAP");
		bootstrap = bootstrap == null ? false : bootstrap;

		return bootstrap;
		
	}
}
