package cl.buildersoft.framework.dataType.impl;

import java.io.IOException;
import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSBoolean extends BSDataTypeAbstract implements BSDataType {
	private Boolean value = false;

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		Boolean out = true;

		out = Boolean.parseBoolean(data);

		return out;
	}

	@Override
	public Object parse(Connection conn, String data) {
		return Boolean.parseBoolean(data);
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.BOOLEAN;
	}

}
