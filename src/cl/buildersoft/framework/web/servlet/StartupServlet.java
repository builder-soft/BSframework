package cl.buildersoft.framework.web.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cl.buildersoft.framework.beans.Config;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.exception.BSConfigurationException;
import cl.buildersoft.framework.util.BSConnectionFactory;

// @ WebServlet("/startup")
public class StartupServlet extends BSHttpServlet_ {
	private static final Logger LOG = LogManager.getLogger(StartupServlet.class);
	private static final String CURRENT_CONTEXT = "CurrentContext";
	private static final long serialVersionUID = 4859610759900103241L;

	public StartupServlet() {
		super();
		LOG.trace(String.format("Creating %s", this.getClass().getName()));

	}

	public void init(ServletConfig config) throws ServletException {
		LOG.trace(String.format("Initializing %s servlet", this.getClass().getName()));
		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection();

		BSBeanUtils bu = new BSBeanUtils();

		ServletContext context = config.getServletContext();
		try {
			@SuppressWarnings("unchecked")
			List<Config> configList = (List<Config>) bu.listAll(conn, new Config());
			String msg = "";
			for (Config configBean : configList) {
				context.setAttribute(configBean.getKey(), configBean.getValue());
				msg = String.format("Reading Key=%s Value=%s", configBean.getKey(), configBean.getValue());
				LOG.debug(msg);
			}
		} finally {
			cf.closeConnection(conn);
		}

		String currentContext = context.getInitParameter(CURRENT_CONTEXT);
		if (currentContext == null) {
			throw new BSConfigurationException("'" + CURRENT_CONTEXT + "' is not declared in web.xml file for "
					+ context.getContextPath());
		} else {
			LOG.debug(String.format("%s is declared in web.xml file for %s", currentContext, context.getContextPath()));
		}
		context.setAttribute(CURRENT_CONTEXT, currentContext);

	}

	public void destroy() {
		LOG.trace(String.format("Destroing %s servlet", this.getClass().getName()));
	}

}
