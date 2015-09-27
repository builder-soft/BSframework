package cl.buildersoft.framework.services;

import java.sql.Connection;
import java.util.List;

import cl.buildersoft.framework.beans.Menu;
import cl.buildersoft.framework.beans.Option;
import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.Submenu;

public interface BSMenuService {
	public Menu getMenu(Connection conn, boolean isAdmin, List<Rol> rols);
	public Menu getMenu(Connection conn, boolean isAdmin, List<Rol> rols, Long type);
	
	public Boolean optionInMenu(Option opt, List<Submenu> main);
	
	public Option searchResourceByKey(Connection conn, String code);
}
