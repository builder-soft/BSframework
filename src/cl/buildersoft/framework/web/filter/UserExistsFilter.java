package cl.buildersoft.framework.web.filter;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cl.buildersoft.framework.util.BSUtils;

/**
 * Servlet Filter implementation class UserExists
 */
// @ WebFilter(urlPatterns = { "/servlet/*" }, dispatcherTypes = {
// DispatcherType.REQUEST, DispatcherType.FORWARD })
public class UserExistsFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(UserExistsFilter.class.getName());

	public UserExistsFilter() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
			ServletException {
		Level level = Level.FINE;
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		LOG.log(Level.CONFIG,
				"User Exists Filter, Context: {0}, {1}",
				BSUtils.array2ObjectArray(request.getServletContext().getAttribute("CurrentContext").toString(),
						request.getRequestURI()));

		Boolean goHome = Boolean.FALSE;

		String uri = request.getRequestURI();
		LOG.log(level, "URI: {0}", uri);


		
//		URL url = request.getServletContext().getResource(uri);
//		if (url == null) {
//			LOG.log(Level.WARNING, "Resource {0} not exists", uri);
//		} else {
//			LOG.log(Level.INFO, "Resource \"{0}\" content is \"{1}\"", BSUtils.array2ObjectArray(uri, url.toString()));
//		}

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
