package cl.buildersoft.framework.services.impl;

import static cl.buildersoft.framework.util.BSUtils.array2List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.User;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.services.BSUserService;
import cl.buildersoft.framework.util.BSConnectionFactory;
import cl.buildersoft.framework.util.BSDataUtils;

public class BSUserServiceImpl extends BSDataUtils implements BSUserService {
	public BSUserServiceImpl() {
		super();
	}

	public User login(Connection conn, String mail, String password) {
		BSBeanUtils beanUtil = new BSBeanUtils();
		User user = new User();

		String sql = "SELECT cId FROM tUser WHERE cMail=? AND cPassword=MD5(?)";

		String idString;

		idString = super.queryField(conn, sql, array2List(mail, password));

		if (idString != null) {
			user.setId(Long.parseLong(idString));
			beanUtil.search(conn, user);
		} else {
			user = null;
		}

		return user;
	}

	@Override
	public List<Rol> getRols(Connection conn, User user) {
		String sql = "SELECT cRol FROM tR_UserRol WHERE cUser=?";
		List<Rol> out = new ArrayList<Rol>();

		BSBeanUtils bu = new BSBeanUtils();
		ResultSet rols = queryResultSet(conn, sql, user.getId());
		try {
			while (rols.next()) {
				Rol rol = new Rol();
				rol.setId(rols.getLong(1));

				bu.search(conn, rol);
				out.add(rol);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		}
		return out;
	}

	@Override
	public User search(Connection conn, String mail) {
		BSBeanUtils bu = new BSBeanUtils();
		User user = new User();

		if (!bu.search(conn, user, "cMail=?", mail)) {
			user = null;
		}

		return user;
	}

	@Override
	public User getSystemUser() {
		return getSpecificUser("SYSTEM");
	}

	@Override
	public User getAnonymousUser() {
		return getSpecificUser("ANONYMOUS");
	}

	private User getSpecificUser(String mail) {
		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection();
		User out = search(conn, mail);
		cf.closeConnection(conn);
		return out;
	}

}
