package cl.buildersoft.framework.dataType;

import java.math.BigDecimal;
import java.sql.Connection;

import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSDecimal extends Object implements BSDataType {

	@Override
	public Boolean validData(Connection conn, String data) {
		Boolean out = true;
		try {
			new BigDecimal(data);
		} catch (Exception e) {
			out = false;
		}
		return out;
	}

	@Override
	public String format(Connection conn, Object data) {
		String out = "";
		if (data instanceof BigDecimal) {
			out = ((BigDecimal) data).toString();
		}
		return out;
	}

	@Override
	public Object parse(Connection conn, String data) {
		BigDecimal out = null;
		try {
			out = new BigDecimal(data);
		} catch (Exception e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}

	@Override
	@Deprecated
	public Boolean validData(String data) {
		return null;
	}

	@Override
	@Deprecated
	public Object convert(String data) {
		return null;
	}
	
	@Override
	public String toString(){
		return BSDataType.DECIMAL;
	}
}
