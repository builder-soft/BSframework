package cl.buildersoft.framework.dataType;

import java.sql.Connection;

public interface BSDataType {
	public Boolean validData(Connection conn, String data);

	public String format(Connection conn, Object data);

	public Object parse(Connection conn, String data);

	public BSDataTypeEnum getDataTypeEnum();

	public Boolean isNumber();

	public Boolean isBoolean();

	public Boolean isTime();
}
