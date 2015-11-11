package cl.buildersoft.framework.services;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.List;

import org.junit.Test;

import cl.buildersoft.framework.beans.Menu;
import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.User;
import cl.buildersoft.framework.services.impl.BSMenuServiceImpl;
import cl.buildersoft.framework.services.impl.BSUserServiceImpl;
import cl.buildersoft.framework.util.BSDataUtils;

public class GetMenuTest {

	@Test
	public void testGetRol1() {
		Menu menu = null;

		User user = new User();
		user.setId(1L);

		Connection conn = null; //new BSDataUtils().getConnection("org.gjt.mm.mysql.Driver", "localhost", "bsframework", "12870668",				"root");

		BSMenuService menuService = new BSMenuServiceImpl();
		BSUserService userService = new BSUserServiceImpl();

		List<Rol> rols = userService.getRols(conn, user);
		menu = menuService.getMenu(conn, false, rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

	@Test
	public void testGetMenu1() {
		Menu menu = null;

		User user = new User();
		user.setId(1L);

		Connection conn = null; //new BSDataUtils().getConnection("org.gjt.mm.mysql.Driver", "localhost", "bsframework", "12870668",				"root");

		BSUserService userService = new BSUserServiceImpl();
		List<Rol> rols = userService.getRols(conn, user);

		BSMenuService menuService = new BSMenuServiceImpl();
		menu = menuService.getMenu(conn, false, rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

}
