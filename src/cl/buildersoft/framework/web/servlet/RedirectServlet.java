package cl.buildersoft.framework.web.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RedirectServlet
 */
@WebServlet("/servlet/RedirectServlet")
public class RedirectServlet extends BSHttpServlet_ {
	private static final Logger LOG = Logger.getLogger(RedirectServlet.class.getName());
	private static final long serialVersionUID = -7842326785701649806L;

	public RedirectServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url = request.getParameter("URL");
		url = URLDecoder.decode(url, "UTF-8");
//		LOG.log(Level.INFO, "Forwarding to {0}", url);

		forward(request, response, url);

	}

}
