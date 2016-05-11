package cl.buildersoft.framework.services;

import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.ArrayList;
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

		Map<String, DomainAttribute> attributesMap = getDomains();

		List<Rol> rols = userService.getRols(conn, user);
		menu = menuService.getMenu(conn, attributesMap, user.getAdmin(), rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

	private Map<String, DomainAttribute> getDomains() {
		return getDomains(true, false);
	}

	private Map<String, DomainAttribute> getDomains(Boolean includeAlbizia, Boolean includeTimectrl) {
		Map<String, DomainAttribute> out = new HashMap<String, DomainAttribute>();

		DomainAttribute albizia = new DomainAttribute();
		DomainAttribute timectrl = new DomainAttribute();
		albizia.setValue(includeAlbizia.toString());
		timectrl.setValue(includeAlbizia.toString());

		out.put("ALBIZIA", albizia);
		out.put("TIMECTRL", timectrl);

		return out;
	}

	@Test
	public void testGetMenu1() {
		Menu menu = null;

		User user = new User();
		user.setId(1L);

		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection("rsa");

		// BSUserService userService = new BSUserServiceImpl();
		List<Rol> rols = getRols();
		BSBeanUtils bu = new BSBeanUtils();
		bu.search(conn, user);

		Map<String, DomainAttribute> attributesMap = getDomains();

		BSMenuService menuService = new BSMenuServiceImpl();
		menu = menuService.getMenu(conn, attributesMap, user.getAdmin(), rols);

		assertTrue(menu != null && menu.list().size() > 0);
	}

	private List<Rol> getRols() {
		List<Rol> out = new ArrayList<Rol>();
		Rol rol = new Rol();
		rol.setId(1L);
		rol.setName("Administrador");
		rol.setDeleted(false);
		out.add(rol);
		return out;
	}

	@Test
	public void testGetMenu2() {
		/** This test, valid that option PROCESS exists */
		Menu menu = null;

		
		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection("rsa");

		// BSUserService userService = new BSUserServiceImpl();
		List<Rol> rols = getRols();
		
		/*
		User user = new User();
		user.setId(1L);
		BSBeanUtils bu = new BSBeanUtils();
		bu.search(conn, user);
*/
		
		Map<String, DomainAttribute> attributesMap = getDomains();

		BSMenuService menuService = new BSMenuServiceImpl();
		menu = menuService.getMenu(conn, attributesMap, false, rols);

		assertTrue(menu.list().get(menu.list().size() - 1).getOption().getKey().equals("PROCESS"));
	}
	@Test
	public void testGetMenu3() {
		/** This test, valid that option PROCESS exists */
		Menu menu = null;

		
		BSConnectionFactory cf = new BSConnectionFactory();
		Connection conn = cf.getConnection("rsa");

		// BSUserService userService = new BSUserServiceImpl();
		List<Rol> rols = getRols();
		
		/*
		User user = new User();
		user.setId(1L);
		BSBeanUtils bu = new BSBeanUtils();
		bu.search(conn, user);
*/
		
		Map<String, DomainAttribute> attributesMap = getDomains(false, false);

		BSMenuService menuService = new BSMenuServiceImpl();
		menu = menuService.getMenu(conn, attributesMap, false, rols);

		assertTrue(menu.list().get(menu.list().size() - 1).getOption().getKey().equals("PROCESS"));
	}
}
