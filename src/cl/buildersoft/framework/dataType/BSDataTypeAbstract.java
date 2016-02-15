package cl.buildersoft.framework.dataType;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import cl.buildersoft.framework.dataType.impl.BSBoolean;
import cl.buildersoft.framework.dataType.impl.BSCalendar;
import cl.buildersoft.framework.dataType.impl.BSDate;
import cl.buildersoft.framework.dataType.impl.BSDouble;
import cl.buildersoft.framework.dataType.impl.BSInteger;
import cl.buildersoft.framework.dataType.impl.BSLong;
import cl.buildersoft.framework.dataType.impl.BSTimestamp;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSConfig;

public abstract class BSDataTypeAbstract {
	private static final String LOCALE = "LOCALE";

	public Boolean isNumber() {
		return this instanceof BSDouble || this instanceof BSInteger || this instanceof BSLong;
	}

	public Boolean isTime() {
		return this instanceof BSDate || this instanceof BSTimestamp || this instanceof BSCalendar;
	}

	public Boolean isBoolean() {
		return this instanceof BSBoolean;
	}

	protected Object parseNumber(Connection conn, String value, String patternKey, Class javaClass) {
		String pattern = getConfig(conn, patternKey);
		Locale locale = new Locale(getLocale(conn));
		Object out = null;

		ParsePosition parsePosition = new ParsePosition(0);

		if (pattern != null && pattern.length() > 0) {
			DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
			format.applyLocalizedPattern(pattern);

			Number num = format.parse(value, parsePosition);

			if (javaClass.equals(Integer.class)) {
				out = num.intValue();
			} else if (javaClass.equals(Double.class)) {
				out = num.doubleValue();
			} else {
				out = num.longValue();
			}

		} else {
			NumberFormat formatNumber = NumberFormat.getNumberInstance(locale);
			out = formatNumber.parse(value, parsePosition);
		}
		if (parsePosition.getIndex() < value.length()) {
			throw new BSProgrammerException("Number " + value + " is not correct ");
		}
		return out;
	}

	public String getLocale(Connection conn) {
		return getConfig(conn, LOCALE).toLowerCase();
	}

	private String getConfig(Connection conn, String key) {
		BSConfig config = new BSConfig();
		return config.getString(conn, key);
	}
}
