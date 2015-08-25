package cl.buildersoft.framework.dataType.impl;

import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;

public class BSDouble extends BSDataTypeAbstract implements BSDataType {

	@Override
	public String format(Connection conn, Object data) {
		return data.toString();
	}

	@Override
	public Boolean validData(Connection conn, String data) {
		try {
			Double.parseDouble(data);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public Object parse(Connection conn, String data) {
		data = data.replaceAll(",", ".");
		Double out = Double.parseDouble(data);
		return out;
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.DOUBLE;
	}
}
