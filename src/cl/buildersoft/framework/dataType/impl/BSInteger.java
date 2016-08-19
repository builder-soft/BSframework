package cl.buildersoft.framework.dataType.impl;

import java.io.Serializable;
import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSInteger extends BSDataTypeAbstract implements BSDataType, Serializable {
	private static final long serialVersionUID = 175883259497230584L;
	private static final String PATTERN_INTEGER = "PATTERN_INTEGER";

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		try {
			// BSWeb.formatInteger(conn, data);
			parseNumber(conn, data, PATTERN_INTEGER, Integer.class);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Object parse(Connection conn, String data) {
		return (Integer) parseNumber(conn, data, PATTERN_INTEGER, Integer.class);
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.INTEGER;
	}

}
