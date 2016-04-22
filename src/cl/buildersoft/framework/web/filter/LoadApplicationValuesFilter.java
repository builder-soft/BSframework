package cl.buildersoft.framework.web.filter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import cl.buildersoft.framework.util.BSDateTimeUtil;
import cl.buildersoft.framework.util.BSUtils;

// @ WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class LoadApplicationValuesFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(LoadApplicationValuesFilter.class.getName());
	String DateFormat = null;
	private static final String DATE_FORMAT = "DateFormat";

	public void destroy() {

	}

	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		LOG.log(Level.FINE, "Load Application Values Filter, Context: {0}, {1}",
				BSUtils.array2ObjectArray(request.getServletContext().getAttribute("CurrentContext").toString(), request.getRequestURI()));

		Object dateFormatObject = request.getServletContext().getAttribute(DATE_FORMAT);
		if (dateFormatObject == null) {
			LOG.log(Level.FINE, "Reading DateFormat");
			request.getServletContext().setAttribute(DATE_FORMAT, BSDateTimeUtil.getFormatDate(request));
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
//		this.DateFormat = fConfig.getInitParameter("ENCODING");
	}

}
