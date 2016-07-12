package cl.buildersoft.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet Filter implementation class UserExists
 */
// @ WebFilter(urlPatterns = { "/servlet/*" }, dispatcherTypes = {
// DispatcherType.REQUEST, DispatcherType.FORWARD })
public class UserExistsFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger(UserExistsFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		LOG.debug(String.format("User Exists Filter, Context: %s, %s", request.getServletContext().getAttribute("CurrentContext")
				.toString(), request.getRequestURI()));

		Boolean goHome = Boolean.FALSE;

		String uri = request.getRequestURI();
		LOG.trace(String.format("URI: %s", uri));

		// URL url = request.getServletContext().getResource(uri);
		// if (url == null) {
		// LOG.log(Level.WARNING, "Resource {0} not exists", uri);
		// } else {
		// LOG.log(Level.INFO, "Resource \"{0}\" content is \"{1}\"",
		// BSUtils.array2ObjectArray(uri, url.toString()));
		// }

		HttpSession session = request.getSession(false);

		if (session == null) {
			goHome = Boolean.TRUE;
		} else {
			Object user = session.getAttribute("User");
			Object rol = session.getAttribute("Rol");
			Object domain = session.getAttribute("Domain");
			if (user == null || rol == null || domain == null) {
				goHome = Boolean.TRUE;
			}
		}

		if (goHome) {
			response.sendRedirect(request.getContextPath());
		} else {
			chain.doFilter(servletRequest, servletResponse);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}
}
