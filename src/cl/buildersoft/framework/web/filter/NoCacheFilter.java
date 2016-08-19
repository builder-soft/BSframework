package cl.buildersoft.framework.web.filter;

import java.io.IOException;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cl.buildersoft.framework.util.BSUtils;

// @ WebFilter(urlPatterns = { "/servlet/*", "/jsp/*" })
public class NoCacheFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger(NoCacheFilter.class);

	public NoCacheFilter() {
	}

	public void destroy() {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;

		LOG.trace(String.format("No Cache Filter, Context: %s, %s", request.getServletContext().getAttribute("CurrentContext")
				.toString(), request.getRequestURI()));

		response.setDateHeader("Date", new Date().getTime());
		response.setDateHeader("Expires", 0);
		response.setHeader("Cache-Control", "no-cache, must-revalidate, s-maxage=0, proxy-revalidate, private");
		response.setHeader("Pragma", "no-cache");
		response.setContentType("text/html");

		chain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
