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

import cl.buildersoft.framework.util.BSUtils;

// @ WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class EncodingFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(EncodingFilter.class.getName());
	String encoding = null;

	public void init(FilterConfig fConfig) throws ServletException {
		this.encoding = fConfig.getInitParameter("ENCODING");
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;

		LOG.log(Level.FINE,
				"In Enconding Filter, Context: {0}, {1}",
				BSUtils.array2ObjectArray(request.getServletContext().getAttribute("CurrentContext").toString(),
						request.getRequestURI()));

		if (this.encoding != null) {
			if (null == servletRequest.getCharacterEncoding())
				servletRequest.setCharacterEncoding(this.encoding);

			response.setContentType("text/html; charset=" + this.encoding);
			response.setCharacterEncoding(this.encoding);
		}
		chain.doFilter(servletRequest, response);
	}

	@Override
	public void destroy() {
	}

	

}
