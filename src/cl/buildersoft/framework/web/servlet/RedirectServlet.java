package cl.buildersoft.framework.web.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RedirectServlet
 */
@WebServlet("/servlet/RedirectServlet")
public class RedirectServlet extends BSHttpServlet_ {
	private static final Logger LOG = LogManager.getLogger(RedirectServlet.class);
	private static final long serialVersionUID = -7842326785701649806L;

	public RedirectServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter("URL");
		url = URLDecoder.decode(url, "UTF-8");
		LOG.trace(String.format("Forwarding to '%s'", url));

		forward(request, response, url);

	}

}
