package cl.buildersoft.framework.type;

import java.sql.Connection;
@Deprecated
public class BSBoolean implements BSFieldDataType {

	@Override
	public Boolean validData(String data) {
		Boolean out = true;
		try {
			out = Boolean.parseBoolean(data);
		} catch (Exception e) {
			return false;
		}
		
		return out;
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
		return validData(data);
	}

	@Override
	public Object parse(Connection conn, String data) {
		return Boolean.parseBoolean(data);
	}

}
