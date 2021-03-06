package cl.buildersoft.framework.dataType.impl;

import java.io.Serializable;
import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSBoolean extends BSDataTypeAbstract implements BSDataType, Serializable {
	private static final long serialVersionUID = 3290811127084539887L;

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
