package cl.buildersoft.framework.services;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cl.buildersoft.framework.beans.DomainAttribute;
import cl.buildersoft.framework.beans.Menu;
import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.User;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.services.impl.BSMenuServiceImpl;
import cl.buildersoft.framework.services.impl.BSUserServiceImpl;
import cl.buildersoft.framework.util.BSConnectionFactory;

public class GetMenuTest {

	@Test
	public void testGetRol1() {
		Menu menu = null;

		User user = new User();
		user.setId(1L);

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection("rsa");

		BSMenuService menuService = new BSMenuServiceImpl();
		BSUserService userService = new BSUserServiceImpl();
		BSBeanUtils bu = new BSBeanUtils();
		bu.search(conn, user);

		Map<String, DomainAttribute> m = getMap();

		List<Rol> rols = userService.getRols(conn, user);
		menu = menuService.getMenu(conn, m, user.getAdmin(), rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

	private Map<String, DomainAttribute> getMap() {
		Map<String, DomainAttribute> out = new HashMap<String, DomainAttribute>();

		DomainAttribute daTrue = new DomainAttribute();
		DomainAttribute daFalse = new DomainAttribute();
		daTrue.setValue("true");
		daFalse.setValue("false");

		out.put("ALBIZIA", daTrue);
		out.put("TIMECTRL", daFalse);

		return out;
	}

	@Test
	public void testGetMenu1() {
		Menu menu = null;

		User user = new User();
		user.setId(1L);

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection("rsa");

		BSUserService userService = new BSUserServiceImpl();
		List<Rol> rols = userService.getRols(conn, user);
		BSBeanUtils bu = new BSBeanUtils();
		bu.search(conn, user);

		Map<String, DomainAttribute> m = getMap();

		BSMenuService menuService = new BSMenuServiceImpl();
		menu = menuService.getMenu(conn, m, user.getAdmin(), rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

}
