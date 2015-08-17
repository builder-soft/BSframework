package cl.buildersoft.framework.type;

import java.sql.Connection;
@Deprecated
public interface BSFieldDataType {
	public Boolean validData(Connection conn, String data);
	public String format(Connection conn, Object data);
	public Object parse(Connection conn, String data);

	@Deprecated
	public Boolean validData(String data);

	@Deprecated
	public Object convert(String data);

}
