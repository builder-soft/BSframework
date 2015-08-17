package cl.buildersoft.framework.dataType;

import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSDataTypeUtil {
	// private static final String BS = ".BS";

	private static final String BS = ".BS";

	/**
	 * <code>
	public static BSFieldDataType create(BSField field) {
		return create(field.getType());
	}</code>
	 */

	@SuppressWarnings("rawtypes")
	public static BSDataType create(String bsDataType) {
		String classInstance = bsDataType;
		BSDataType out = null;
		try {
			Class newClass = Class.forName(classInstance);
			out = (BSDataType) newClass.newInstance();
		} catch (Exception e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}

	/**
	 * <code>
	@ SuppressWarnings("rawtypes")
	@ Deprecated
	public boolean evaluate(String evaluateData, BSField field) {
		Boolean response = null;
		try {
			String classInstance = this.getClass().getPackage().getName() + BS + field.getType().toString();

			Class newClass = Class.forName(classInstance);

			BSFieldDataType data = (BSFieldDataType) newClass.newInstance();
			Class partypes[] = new Class[1];
			partypes[0] = String.class;
			Object[] obj = { evaluateData };
			response = (Boolean) data.getClass().getMethod("validData", partypes).invoke(data, obj);

		} catch (Exception e) {
			throw new BSProgrammerException("0000", "Error al procesar el tipo " + field.getType().toString());
		}
		return response;
	}
	</code>
	 */

	public static Boolean isNumber(BSDataType dataType) {
		return dataType instanceof BSDouble || dataType instanceof BSInteger || dataType instanceof BSLong;
	}

	public static Boolean isTime(BSDataType dataType) {
		return dataType instanceof BSDate || dataType instanceof BSTimestamp;
	}

	public static Boolean isBoolean(BSDataType dataType) {
		return dataType instanceof BSBoolean;
	}

	public static String toNormalString(BSDataType dataType) {
		String out = dataType.toString();
		Integer index = out.indexOf(BS);
		out = out.substring(index + BS.length());
		return out;
	}
}
