package cl.buildersoft.framework.dataType;

import java.sql.Connection;

public interface BSDataType {
	public static final String STRING =  BSDataType.class.getPackage().getName() + ".BSString";
	public static final String INTEGER = BSDataType.class.getPackage().getName() + ".BSInteger";
	public static final String DOUBLE = BSDataType.class.getPackage().getName() + ".BSDouble";
	public static final String LONG = BSDataType.class.getPackage().getName() + ".BSLong";
	public static final String BOOLEAN = BSDataType.class.getPackage().getName() + ".BSBoolean";
	public static final String DATE = BSDataType.class.getPackage().getName() + ".BSDate";
	public static final String TIMESTAMP = BSDataType.class.getPackage().getName() + ".BSTimestamp";
	public static final String CALENDAR = BSDataType.class.getPackage().getName() + ".BSCalendar";
	public static final String DECIMAL = BSDataType.class.getPackage().getName() + ".BSDecimal";
			
	public Boolean validData(Connection conn, String data);

	public String format(Connection conn, Object data);

	public Object parse(Connection conn, String data);

	@Deprecated
	public Boolean validData(String data);

	@Deprecated
	public Object convert(String data);

}
