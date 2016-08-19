package cl.buildersoft.framework.business.services;

import cl.buildersoft.framework.business.services.impl.EventLogServiceImpl;
import cl.buildersoft.framework.util.BSFactory;

public class ServiceFactory extends BSFactory {

	public static EventLogService createEventLogService() {
		return new EventLogServiceImpl();
	}

}
