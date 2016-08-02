package cl.buildersoft.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.buildersoft.framework.util.BSDateTimeUtil;

// @ WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class LoadApplicationValuesFilter implements Filter {
	private static final Logger LOG = LogManager.getLogger(LoadApplicationValuesFilter.class);
	String DateFormat = null;
	private static final String DATE_FORMAT = "DateFormat";

	public void destroy() {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		LOG.trace(String.format("Load Application Values Filter, Context: %s, %s",
				request.getServletContext().getAttribute("CurrentContext").toString(), request.getRequestURI()));

		Object dateFormatObject = request.getServletContext().getAttribute(DATE_FORMAT);
		if (dateFormatObject == null) {
			LOG.trace("Reading DateFormat");
			request.getServletContext().setAttribute(DATE_FORMAT, BSDateTimeUtil.getFormatDate(request));
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// this.DateFormat = fConfig.getInitParameter("ENCODING");
	}

}
