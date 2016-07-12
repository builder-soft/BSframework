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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.buildersoft.framework.web.servlet.BSHttpServlet_;

// @ WebFilter(dispatcherTypes = { DispatcherType.REQUEST }, urlPatterns = { "/servlet/*" })
public class RestoreSessionFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger(RestoreSessionFilter.class);

	@Override
	public void destroy() {
		LOG.entry();
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		LOG.entry();
	}

	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) rq;
		HttpServletResponse response = (HttpServletResponse) rs;

		LOG.trace(String.format("Session Filter, Context: %s, %s", request.getServletContext().getAttribute("CurrentContext")
				.toString(), request.getRequestURI()));

		BSHttpServlet_ su = new BSHttpServlet_();

		try {
			su.restoreSession(request, response);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		chain.doFilter(rq, rs);
		
	}
}
