package cl.buildersoft.framework.util;

import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSFactory {
	public Object getInstance(String className) {
		Object out = null;
		try {
			Class<?> javaClass = (Class<?>) Class.forName(className);
			out = javaClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new BSProgrammerException(e);
		} catch (InstantiationException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalAccessException e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}
}
