package cl.buildersoft.framework.dataType.impl;

import java.io.Serializable;
import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSString extends BSDataTypeAbstract implements BSDataType, Serializable {
	private static final long serialVersionUID = 2219369387994739073L;

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		return true;
	}

	@Override
	public Object parse(Connection conn, String data) {
		return data.toString();
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.STRING;
	}
}
