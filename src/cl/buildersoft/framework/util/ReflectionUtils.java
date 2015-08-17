package cl.buildersoft.framework.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtils {
	public ReflectionUtils() {
		super();
		// TODO Generado por el editor - NECTA QA
	}

	public static Object execute(String nameMethod, Object subject, Object[] objects, Class[] classes) throws Exception,
			IllegalAccessException {
		Class clazz = subject.getClass();
		Method method = clazz.getMethod(nameMethod, classes);
		method.setAccessible(true);
		return method.invoke(subject, objects);
	}

	public static Object execute(String nameMethod, Object subject, Object[] parameters) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class[] classes = ReflectionUtils.getTypes(parameters);
		Class clazz = subject.getClass();
		// Method method = clazz.getMethod(nameMethod, classes );
		// TODO revisar
		Method method = null;
		Method methods[] = clazz.getMethods();
		boolean foundIt = false;
		for (int i = 0; i < methods.length && !foundIt; i++) {
			if (methods[i].getName().equals(nameMethod.trim())) {
				method = methods[i];
				foundIt = true;
			}
		}
		method.setAccessible(true);
		return method.invoke(subject, parameters);
	}

	public static Class[] getTypes(Object[] objects) {
		Class[] classes = new Class[objects.length];
		for (int i = 0; i < objects.length; i++) {
			classes[i] = objects[i].getClass();
		}
		return classes;
	}

	public static List<String> getMethodNames(Class target) {
		Method[] methods = target.getDeclaredMethods();
		List<String> actions = new ArrayList<String>();
		for (int i = 0; i < methods.length; i++) {
			actions.add(target.getSimpleName() + "." + methods[i].getName());
		}
		return actions;
	}

	public static Map<String, List<String>> getActionsName(Class[] classes) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		for (int i = 0; i < classes.length; i++) {
			Class class1 = classes[i];
			List<String> actions = ReflectionUtils.getMethodNames(class1);

			result.put(class1.getSimpleName(), actions);
		}
		return result;
	}

	public static Method getMethodByName(Method[] methods, String attrib) {
		Method method = null;
		attrib = "get" + attrib.subSequence(0, 1).toString().toUpperCase() + attrib.subSequence(1, attrib.length()).toString();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(attrib)) {
				method = methods[i];
			}
		}
		return method;
	}
}