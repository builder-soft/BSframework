package cl.buildersoft.framework.dataType.impl;

import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSLong extends BSDataTypeAbstract implements BSDataType {

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		try {
			Long.parseLong(data);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Object parse(Connection conn, String data) {
		return Long.parseLong(data);
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.LONG;
	}
}
