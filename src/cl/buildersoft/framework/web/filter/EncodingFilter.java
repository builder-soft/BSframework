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

// @ WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.REQUEST })
public class EncodingFilter implements Filter {
	private static final Logger LOG = Logger.getLogger(EncodingFilter.class.getName());
	String encoding = null;

	public void destroy() {
		 
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		LOG.log(Level.FINEST,"In Enconding Filter");
		if (this.encoding != null) {
			if (null == request.getCharacterEncoding())
				request.setCharacterEncoding(this.encoding);

			response.setContentType("text/html; charset=" + this.encoding);
			response.setCharacterEncoding(this.encoding);
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.encoding = fConfig.getInitParameter("ENCODING");
	}

}
