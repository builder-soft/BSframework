package cl.buildersoft.framework.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cl.buildersoft.framework.beans.BSBean;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.exception.BSProgrammerException;

public class BSBeanUtilsSP extends BSBeanUtils {
	public List<? extends BSBean> list(Connection conn, BSBean bean, String spName) {
		return list(conn, bean, spName, new ArrayList());
	}

	public List<? extends BSBean> list(Connection conn, BSBean bean, String spName, Object oneParam) {
		List<Object> params = new ArrayList<Object>();
		params.add(oneParam);
		return list(conn, bean, spName, params);
	}

	public List<? extends BSBean> list(Connection conn, BSBean bean, String spName, List<Object> params) {

		if (params.size() == 0) {
			params = null;
		}

		BSmySQL mysql = new BSmySQL();
		ResultSet rs = mysql.callSingleSP(conn, spName, params);

		List<BSBean> out = new ArrayList<BSBean>();
		Long id = null;
		try {
			while (rs.next()) {
				id = rs.getLong(1);
				BSBean object = bean.getClass().newInstance();
				object.setId(id);
				super.search(conn, object);
				out.add((BSBean) object);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException("", e.getMessage());
		} catch (Exception e) {
			throw new BSProgrammerException("", e.getMessage());
		}

		return out;
	}

	public BSBean get(Connection conn, BSBean bean, String spName, Object oneParam) {
		List<Object> params = new ArrayList<Object>();
		params.add(oneParam);
		return get(conn, bean, spName, params);
	}

	public BSBean get(Connection conn, BSBean bean, String spName, List<Object> params) {

		BSmySQL mysql = new BSmySQL();
		ResultSet rs = mysql.callSingleSP(conn, spName, params);

		BSBean out = null;
		Long id = null;
		try {
			if (rs.next()) {
				id = rs.getLong(1);
				out = bean.getClass().newInstance();
				out.setId(id);
				super.search(conn, out);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException("", e.getMessage());
		} catch (Exception e) {
			throw new BSProgrammerException("", e.getMessage());
		}

		return out;
	}
}
