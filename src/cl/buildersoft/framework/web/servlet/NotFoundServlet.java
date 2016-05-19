package cl.buildersoft.framework.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cl.buildersoft.framework.web.filter.RestoreSessionFilter;

/**
 * Servlet implementation class NotFoundServlet
 */
@WebServlet("/servlet/common/NotFoundServlet")
public class NotFoundServlet extends BSHttpServlet_ {
	private static final Logger LOG = Logger.getLogger(NotFoundServlet.class.getName());
	private static final String NOT_FOUND_PAGE = "/WEB-INF/jsp/common/resource-not-found2.jsp";
	private static final long serialVersionUID = -4785172320410838322L;

	@Override
	public void init() {
		LOG.log(Level.CONFIG, "Running NotFoundServlet init");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LOG.log(Level.CONFIG, "Strating NotFoundServlet service");
		if (!"DALEA_CONTEXT".equals(getApplicationValue(request, "CurrentContext"))) {
			String daleaContext = getApplicationValue(request, "DALEA_CONTEXT").toString();
			String url = daleaContext + "/servlet/RedirectServlet?URL=" + URLEncoder.encode(NOT_FOUND_PAGE, "UTF-8");
			redirect(request, response, url);
		} else {
			forward(request, response, NOT_FOUND_PAGE, false);
		}
	}

}
