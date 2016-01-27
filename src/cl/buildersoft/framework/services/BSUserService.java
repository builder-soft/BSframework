package cl.buildersoft.framework.services;

import java.sql.Connection;
import java.util.List;

import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.User;

public interface BSUserService {
	public User login(Connection conn, String mail, String password);

	public List<Rol> getRols(Connection conn, User user);

	public User search(Connection conn, String mail);

	public User getSystemUser();

	public User getAnonymousUser();

}
