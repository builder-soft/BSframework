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
import cl.buildersoft.framework.exception.BSConfigurationException;
import cl.buildersoft.framework.util.BSConnectionFactory;
import cl.buildersoft.framework.util.BSUtils;

// @ WebServlet("/startup")
public class StartupServlet extends BSHttpServlet_ {
	private static final String CURRENT_CONTEXT = "CurrentContext";
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
				msg += String.format("\nReading Key=%s value=%s", configBean.getKey(), configBean.getValue());
			}
			LOG.log(Level.INFO, msg);
		} finally {
			cf.closeConnection(conn);
		}

		String currentContext = context.getInitParameter(CURRENT_CONTEXT);
		if (currentContext == null) {
			throw new BSConfigurationException("'" + CURRENT_CONTEXT + "' is not declared in web.xml file for "
					+ context.getContextPath());
		} else {
			LOG.log(Level.INFO, "{0} is declared in web.xml file for {1}",
					BSUtils.array2ObjectArray(currentContext, context.getContextPath()));
		}
		context.setAttribute(CURRENT_CONTEXT, currentContext);

	}

	public void destroy() {
		LOG.log(Level.INFO, "Destroing {0} servlet", this.getClass().getName());
	}

}
