package cl.buildersoft.framework.dataType.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSConfig;

//import java.util.Date;

public class BSCalendar extends BSDataTypeAbstract implements BSDataType, Serializable {
	private static final long serialVersionUID = -7721753756189866016L;
	private Calendar value = null;

	public BSCalendar() {
		value = Calendar.getInstance();
	}

	@Override
	public String format(Connection conn, Object data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		String out = null;
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			out = formatter.format((Calendar) data);
		} catch (IllegalArgumentException e) {
			throw new BSProgrammerException("No se puede formatear el dato " + data);
		}
		return out;
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		DateFormat formatter = new SimpleDateFormat(formatDate);
		Boolean out = null;
		try {
			formatter.parse(data);
			out = true;
		} catch (ParseException e) {
			out = false;
		}
		return out;
	}

	@Override
	public Object parse(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		Calendar out = Calendar.getInstance();
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			java.util.Date temp = formatter.parse(data);
			out.setTime(temp);
			temp = null;
		} catch (ParseException e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.CALENDAR;
	}
}
