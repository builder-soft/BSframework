package cl.buildersoft.framework.dataType.impl;

import java.math.BigDecimal;
import java.sql.Connection;

import cl.buildersoft.framework.dataType.BSDataType;
import cl.buildersoft.framework.dataType.BSDataTypeAbstract;
import cl.buildersoft.framework.dataType.BSDataTypeEnum;
import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSDecimal extends BSDataTypeAbstract implements BSDataType {
	private BigDecimal value = new BigDecimal(0);

	@Override
	public Boolean validData(Connection conn, String data) {
		Boolean out = true;
		try {
			new BigDecimal(data);
		} catch (NumberFormatException e) {
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
		} catch (NumberFormatException e) {
			throw new BSProgrammerException(e);
		}
		return out;
	}

	@Override
	public BSDataTypeEnum getDataTypeEnum() {
		return BSDataTypeEnum.DECIMAL;
	}
}
