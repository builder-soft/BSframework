package cl.buildersoft.framework.type;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.util.BSConfig;
@Deprecated
public class BSTimestamp implements BSFieldDataType {

	@Override
	public Boolean validData(String data) {
		throw new BSProgrammerException(
				"",
				"Hay que llamar al metodo validData(Connection, String); para el tipo BSTimestamp");
	}

	@Override
	public Object convert(String data) {
		return null;
	}

	@Override
	public String format(Connection conn, Object data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		String out = null;
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			out = formatter.format((Date) data);
		} catch (Exception e) {
			throw new BSProgrammerException("",
					"No se puede formatear el dato " + data);
		}
		return out;
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			formatter.parse(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Object parse(Connection conn, String data) {
		BSConfig config = new BSConfig();
		String formatDate = config.getString(conn, "FORMAT_DATETIME");
		Date out = null;
		DateFormat formatter = new SimpleDateFormat(formatDate);
		try {
			out = formatter.parse(data);
		} catch (Exception e) {
			throw new BSProgrammerException("", e.getMessage());
		}
		return out;
	}

}
