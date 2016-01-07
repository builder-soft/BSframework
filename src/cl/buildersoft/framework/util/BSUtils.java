package cl.buildersoft.framework.util;

import java.util.ArrayList;
import java.util.List;

import cl.buildersoft.framework.util.crud.BSField;

public class BSUtils {
	public static List<Object> array2List(Object... prms) {
		List<Object> out = new ArrayList<Object>();
		if (prms != null) {
			for (Object o : prms) {
				out.add(o);
			}
		}
		return out;
	}

	public static Object[] array2ObjectArray(Object... prms) {
		Object[] out = new Object[prms.length];
		Integer i = 0;
		for (Object prm : prms) {
			out[i++] = prm;
		}
		return out;
	}

	public static Object[] list2Array(List<Object> prms) {
		Object[] out = new Object[prms.size()];

		int i = 0;
		for (Object o : prms) {
			out[i++] = o;
		}

		return out;
	}

	public static String unSplitString(String[] names, String c) {
		String out = "";
		for (String s : names) {
			out += s + c;
		}
		out = out.substring(0, out.length() - c.length());
		return out;
	}

	public static String unSplitField(BSField[] fields, String c) {
		String out = "";
		for (BSField f : fields) {
			out += f.getName() + c;
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	public static Boolean isNumber(String value) {
		Boolean out = true;
		try {
			Double.parseDouble(value);
		} catch (NumberFormatException e) {
			out = false;
		}
		return out;
	}

	public static String getCommas(Object[] values) {
		String out = "";
		for (int i = 0; i < values.length; i++) {
			out += "?,";
		}
		out = out.substring(0, out.length() - 1);
		return out;
	}

	
}
