package cl.buildersoft.framework.dataType;

import java.sql.Connection;

public class BSDouble implements BSDataType {

	@Override
	public Boolean validData(String data) {
		try {
			Double.parseDouble(data);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Object convert(String data) {
		return null;
	}

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		return null;
	}

	@Override
	public Object parse(Connection conn, String data) {
		data = data.replaceAll(",", ".");
		Double out = Double.parseDouble(data);
		return out;
	}

	@Override
	public String toString() {
		return BSDataType.DOUBLE;
	}
}
