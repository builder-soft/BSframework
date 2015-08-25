package cl.buildersoft.framework.dataType.impl;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSConfig;

public class BSDate extends BSDataTypeAbstract implements BSDataType {
	private Date value = null;

	public BSDate() {
		value = new Date();
	}

	@Override
	public String format(Connection conn, Object data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATE");
		String out = null;
		DateFormat formatter = new SimpleDateFormat(formatDate);
		
		out = formatter.format((Date) data);
		/**<code>
		try {
			out = formatter.format((Date) data);
		} catch (ParseException e) {
			throw new BSProgrammerException("No se puede formatear el dato " + data);
		}
		</code>*/
		return out;
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATE");
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			formatter.parse(data);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}

	@Override
	public Object parse(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATE");
		Date out = null;
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			out = formatter.parse(data);
		} catch (ParseException e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.DATE;
	}
}
