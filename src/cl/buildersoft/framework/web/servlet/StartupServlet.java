package cl.buildersoft.framework.web.servlet;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import cl.buildersoft.framework.beans.Config;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.util.BSConnectionFactory;

// @ WebServlet("/startup")
public class StartupServlet extends BSHttpServlet_ {
	private static final Logger LOG = Logger.getLogger(StartupServlet.class.getName());
	private static final long serialVersionUID = 4859610759900103241L;

	public StartupServlet() {
		super();
		LOG.log(Level.INFO, "Creating {0}", this.getClass().getName());

	}

	public void init(ServletConfig config) throws ServletException {
		LOG.log(Level.INFO, "Initializing {0} servlet", this.getClass().getName());
		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection();

		BSBeanUtils bu = new BSBeanUtils();

		ServletContext context = config.getServletContext();
		try {
			List<Config> configList = (List<Config>) bu.listAll(conn, new Config());
			String msg = "";
			for (Config configBean : configList) {
				context.setAttribute(configBean.getKey(), configBean.getValue());
				msg += String.format("Reading Key=%s value=%s\n", configBean.getKey(), configBean.getValue());
			}
			LOG.log(Level.INFO, msg);
		} finally {
			cf.closeConnection(conn);
		}

	}

	public void destroy() {
		LOG.log(Level.INFO, "Destroing {0} servlet", this.getClass().getName());
	}

}
