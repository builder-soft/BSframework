package cl.buildersoft.framework.web.filter;

import java.io.IOException;
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
import cl.buildersoft.framework.web.servlet.BSHttpServlet_;

// @ WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, urlPatterns = { "/servlet/*" })
public class SessionFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(SessionFilter.class.getName());

	public SessionFilter() {
	}

	public void destroy() {
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;

//		request.getSession(true);

		BSHttpServlet_ su = new BSHttpServlet_();

		try {
			
			su.restoreSession(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		chain.doFilter(rq, rs);
	}

}
