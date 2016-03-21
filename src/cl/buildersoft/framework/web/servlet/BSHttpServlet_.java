package cl.buildersoft.framework.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cl.buildersoft.framework.beans.Domain;
import cl.buildersoft.framework.beans.SessionBean;
import cl.buildersoft.framework.beans.SessionDataBean;
import cl.buildersoft.framework.beans.User;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSSystemException;
import cl.buildersoft.framework.util.BSConnectionFactory;

// D:\temp\5\20160304\app-web-master\app-web-master\sso\src\cl\buildersoft\sso\filter
public class BSHttpServlet_ extends HttpServlet {
	private static final Logger LOG = Logger.getLogger(BSHttpServlet_.class.getName());
	private static final long serialVersionUID = 7807647668104655759L;
	private static final String SESSION_COOKIE_NAME = "SESSION_SSO";
	private static final int MAX_AGE = 24 * 60 * 60;
	private static final String ROOT = "/";

	protected Connection getConnection(HttpServletRequest request) {
		BSConnectionFactory cf = new BSConnectionFactory();
		return cf.getConnection(request);
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

	protected Domain getCurrentDomain(HttpServletRequest request) {
		return (Domain) request.getSession(false).getAttribute("Domain");
	}

	protected void showParameters(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		String name = null;
		while (names.hasMoreElements()) {
			name = (String) names.nextElement();

			LOG.log(Level.CONFIG, "Name={0}", request.getParameter(name));

		}
	}

	protected Boolean bootstrap(Connection conn) {
		return true;
	}

	/***************************************/
	// Desde aqui las funciones que sirven para el manejo de sesion.
	/***************************************/
	protected void forward(HttpServletRequest request, HttpServletResponse response, String uri) throws ServletException,
			IOException {
		forward(request, response, uri, true);
	}

	protected void forward(HttpServletRequest request, HttpServletResponse response, String uri, Boolean saveSessionToDB)
			throws ServletException, IOException {
		if (saveSessionToDB) {
			// LOG.log(Level.INFO, "Savind values");
			updateSession(request, response);
		} else {
			swapCookie(request, response);
		}
		request.getRequestDispatcher(uri).forward(request, response);
	}

	private void swapCookie(HttpServletRequest request, HttpServletResponse response) {
		String value = readTokenValue(request);
		saveCookieToResponse(response, value, false);
	}

	protected void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws ServletException,
			IOException {
		updateSession(request, response);
		response.sendRedirect(url);
	}

	public HttpSession createSession(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(true);
		String token = session.getId() + System.currentTimeMillis();

		saveCookieToResponse(response, token);
		session.setAttribute(SESSION_COOKIE_NAME, token);

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection();
		try {
			saveSessionToDB(conn, session, true);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, e.getMessage(), e);
			throw new BSSystemException(e);
		} finally {
			cf.closeConnection(conn);
		}
		return session;
	}

	public void restoreSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String token = readTokenValue(request);

		if (token == null) {
			LOG.log(Level.WARNING, "Cookie not found");
		} else {
			HttpSession session = request.getSession(false);

			if (session.getAttribute(SESSION_COOKIE_NAME) == null) {
				session.setAttribute(SESSION_COOKIE_NAME, token);
				LOG.log(Level.INFO, "session.getAttribute(SESSION_COOKIE_NAME) is null {0}",
						session.getAttribute(SESSION_COOKIE_NAME) == null);
			}

			BSConnectionFactory cf = new BSConnectionFactory();
			Connection conn = cf.getConnection();
			try {
				readSessionDataFromDB(conn, session, token);
			} finally {
				cf.closeConnection(conn);
			}
		}
	}

	public void updateSession(HttpServletRequest request, HttpServletResponse response) {
		String token = readTokenValue(request);
		if (token != null) {
			HttpSession session = request.getSession(false);

			if (session.getAttribute(SESSION_COOKIE_NAME) == null) {
				session.setAttribute(SESSION_COOKIE_NAME, token);
				LOG.log(Level.INFO, "session.getAttribute(SESSION_COOKIE_NAME) is null {0}",
						session.getAttribute(SESSION_COOKIE_NAME) == null);
			}

			if (session != null) {
				BSConnectionFactory cf = new BSConnectionFactory();
				Connection conn = cf.getConnection();
				try {
					// saveSessionToDB(conn, session, sessionId);
					saveSessionToDB(conn, session, false);
				} finally {
					cf.closeConnection(conn);
				}

			}
			saveCookieToResponse(response, token);
		}
	}

	public void deleteSession(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			// String token =
			// session.getAttribute(SESSION_COOKIE_NAME).toString();
			String token = readTokenValue(request);

			saveCookieToResponse(response, token, true);

			deleteSessionOfDB(token);
			session.invalidate();
		}
	}

	private void deleteSessionOfDB(String token) {
		SessionBean sessionBean = new SessionBean();
		SessionDataBean sessionDataBean = new SessionDataBean();
		BSBeanUtils bu = new BSBeanUtils();

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection();
		try {
			if (bu.search(conn, sessionBean, "cToken=?", token)) {
				List<SessionDataBean> dataList = (List<SessionDataBean>) bu.list(conn, sessionDataBean, "cSession=?",
						sessionBean.getId());

				for (SessionDataBean data : dataList) {
					bu.delete(conn, data);
				}
				bu.delete(conn, sessionBean);
			}
		} finally {
			cf.closeConnection(conn);
		}

	}

	private SessionBean saveSessionToDB(Connection conn, HttpSession session, Boolean createIfNotExists) {
		SessionBean sessionBean = new SessionBean();
		SessionDataBean sessionDataBean = null;
		BSBeanUtils bu = new BSBeanUtils();

		String token = session.getAttribute(SESSION_COOKIE_NAME).toString();

		// sessionBean.setSessionId(sessionId);
		Boolean foundIt = bu.search(conn, sessionBean, "cToken=?", token);
		if (foundIt || createIfNotExists) {
			sessionBean.setLastAccess(new Timestamp(System.currentTimeMillis()));
			if (!foundIt) {
				sessionBean.setToken(token);
			}
			bu.save(conn, sessionBean);

			Enumeration<String> names = session.getAttributeNames();
			String name = null;

			while (names.hasMoreElements()) {
				name = names.nextElement();
				if (!SESSION_COOKIE_NAME.equals(name)) {
					sessionDataBean = new SessionDataBean();

					// sessionDataBean.setSession(sessionBean.getId());
					if (bu.search(conn, sessionDataBean, "cSession=? AND cName=?", sessionBean.getId(), name)) {
						sessionDataBean.setData(objectToString(session.getAttribute(name)));
						bu.update(conn, sessionDataBean);
					} else {
						sessionDataBean.setSession(sessionBean.getId());
						sessionDataBean.setName(name);
						sessionDataBean.setData(objectToString(session.getAttribute(name)));
						bu.insert(conn, sessionDataBean);
					}
				}
			}
		}
		return sessionBean;
	}

	private String readTokenValue(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		String out = null;

		if (cookies != null) {
			for (Cookie currentCookie : cookies) {
				if (currentCookie.getName().equals(SESSION_COOKIE_NAME)) {
					out = currentCookie.getValue();
					break;
				}
			}
		}
		if (out == null) {
			HttpSession session = request.getSession(false);
			if (session != null) {
				Object obj = session.getAttribute(SESSION_COOKIE_NAME);
				if (obj != null) {
					out = (String) obj;
				}
			}
		}

		return out;
	}

	private String objectToString(Object obj) throws BSSystemException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
		} catch (IOException e) {
			throw new BSSystemException(e);
		}
		byte[] userAsBytes = baos.toByteArray();

		BASE64Encoder encoder = new BASE64Encoder();
		String out = encoder.encodeBuffer(userAsBytes);

		return out;
	}

	private Object stringToObject(String str) throws BSSystemException {
		BASE64Decoder decoder = new BASE64Decoder();
		ObjectInputStream ois = null;
		Object out = null;
		try {
			byte[] objectAsBytes = decoder.decodeBuffer(str);
			ByteArrayInputStream bais = new ByteArrayInputStream(objectAsBytes);
			ois = new ObjectInputStream(bais);
			out = ois.readObject();
		} catch (Exception e) {
			throw new BSSystemException(e);
		}

		return out;
	}

	private Cookie saveCookieToResponse(HttpServletResponse response, String token) {
		return saveCookieToResponse(response, token, false);
	}

	private Cookie saveCookieToResponse(HttpServletResponse response, String token, Boolean delete) {
		Cookie cookie = new Cookie(SESSION_COOKIE_NAME, delete ? null : token);
		cookie.setPath(ROOT);
		cookie.setMaxAge(delete ? 0 : MAX_AGE);
		response.addCookie(cookie);
		return cookie;
	}

	private void readSessionDataFromDB(Connection conn, HttpSession session, String token) {
		BSBeanUtils bu = new BSBeanUtils();
		SessionBean sessionBean = new SessionBean();
		SessionDataBean sessionDataBean = new SessionDataBean();

		synchronized (this) {
			if (bu.search(conn, sessionBean, "cToken=?", token)) {
				List<SessionDataBean> objectList = (List<SessionDataBean>) bu.list(conn, sessionDataBean, "cSession=?",
						sessionBean.getId());
				Object obj = null;

				Enumeration<String> names = session.getAttributeNames();
				String name = null;
				while (names.hasMoreElements()) {
					name = names.nextElement();
					session.removeAttribute(name);
				}

				for (SessionDataBean record : objectList) {
					obj = stringToObject(record.getData());
					session.setAttribute(record.getName(), obj);
				}
			}
		}
	}
}
