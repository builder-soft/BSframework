package cl.buildersoft.framework.services.impl;

import static cl.buildersoft.framework.util.BSUtils.array2List;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cl.buildersoft.framework.beans.DomainAttribute;
import cl.buildersoft.framework.beans.Menu;
import cl.buildersoft.framework.beans.Option;
import cl.buildersoft.framework.beans.Rol;
import cl.buildersoft.framework.beans.Submenu;
import cl.buildersoft.framework.database.BSBeanUtils;
import cl.buildersoft.framework.exception.BSDataBaseException;
import cl.buildersoft.framework.services.BSMenuService;
import cl.buildersoft.framework.util.BSDataUtils;

public class BSMenuServiceImpl extends BSDataUtils implements BSMenuService {

	@Override
	public Menu getMenu(Connection conn, Map<String, DomainAttribute> domainAttribute, boolean isAdmin, List<Rol> rols) {
		return getMenu(conn, domainAttribute, isAdmin, rols, null);
	}

	@Override
	public Menu getMenu(Connection conn, Map<String, DomainAttribute> domainAttribute, boolean isAdmin, List<Rol> rols, Long type) {
		Menu menu = null;

		Submenu sub = null;
		if (rols == null) {
			List<Submenu> main = fillSubmenu(conn, domainAttribute, isAdmin, sub, null, menu, type);
			menu = new Menu();
			addMainMenuToMenu(main, menu);
		} else {
			Rol rol = rols.get(0);

			List<Submenu> main = fillSubmenu(conn, domainAttribute, isAdmin, sub, rol, menu, type);
			menu = new Menu();
			addMainMenuToMenu(main, menu);
			/** ------------- */
			for (int i = 1; i < rols.size(); i++) {
				rol = rols.get(i);

				main = fillSubmenu(conn, domainAttribute, isAdmin, sub, rol, menu, type);
				addMainMenuToMenu(main, menu);

				for (Submenu sub1 : menu.list()) {
					complement(conn, domainAttribute, isAdmin, sub1, rol, menu, type);
				}
			}
		}

		return menu;
	}

	private List<Submenu> fillSubmenu(Connection conn, Map<String, DomainAttribute> domainAttribute, boolean isAdmin,
			Submenu main, Rol rol, Menu menu, Long type) {
		List<Submenu> subList = getSubmenu(conn, domainAttribute, isAdmin, main, rol, type);

		for (Submenu sub : subList) {
			List<Submenu> auxList = fillSubmenu(conn, domainAttribute, isAdmin, sub, rol, menu, type);
			sub.addSubmenu(auxList, menu);
		}
		return subList;
	}

	private void complement(Connection conn, Map<String, DomainAttribute> domainAttribute, boolean isAdmin, Submenu main,
			Rol rol, Menu menu, Long type) {
		List<Submenu> subList = getSubmenu(conn, domainAttribute, isAdmin, main, rol, type);

		main.addSubmenu(subList, menu);

		for (Submenu sub : main.list()) {
			complement(conn, domainAttribute, isAdmin, sub, rol, menu, type);
		}
	}

	private List<Submenu> getSubmenu(Connection conn, Map<String, DomainAttribute> domainAttribute, boolean isAdmin, Submenu sub,
			Rol rol, Long type) {
		String sql = null;
		List<Object> prms = null;
		Option parent = sub != null ? sub.getOption() : null;

		String modules = !isAdmin ? getDomainAttribtesAsList(domainAttribute) : "";

		if (parent == null && rol == null) {
			sql = "SELECT cId AS cOption ";
			sql += "FROM tOption AS o ";
			sql += "WHERE " + modules + " cParent IS NULL";
			if (!isAdmin) {
				sql += " AND cIsAdmin=FALSE";
			}
			prms = null;
		} else if (parent == null && rol != null) {
			sql = "SELECT cOption ";
			sql += "FROM tR_RolOption r ";
			sql += "LEFT JOIN tOption AS o ON r.cOption=o.cId ";
			sql += "WHERE " + modules + " o.cParent IS NULL AND r.cRol=?";
			if (!isAdmin) {
				sql += " AND cIsAdmin=FALSE";
			}
			prms = array2List(rol.getId());
		} else if (parent != null && rol == null) {
			sql = "SELECT cId AS cOption ";
			sql += "FROM tOption AS o ";
			sql += "WHERE " + modules + " cParent=?";
			if (!isAdmin) {
				sql += " AND cIsAdmin=FALSE";
			}
			prms = array2List(parent.getId());
		} else if (parent != null && rol != null) {
			sql = "SELECT cOption ";
			sql += "FROM tR_RolOption r ";
			sql += "LEFT JOIN tOption AS o ON r.cOption=o.cId ";
			sql += "WHERE " + modules + " o.cParent=? AND r.cRol=?";
			if (!isAdmin) {
				sql += " AND cIsAdmin=FALSE";
			}
			prms = array2List(parent.getId(), rol.getId());
		}

		if (type != null) {
			sql += " AND o.cType=1";
		}
		sql += " AND cEnable=TRUE";
		sql += " ORDER BY cOrder";
		return getSubmenuFromDB(conn, sql, prms);

	}

	private String getDomainAttribtesAsList(Map<String, DomainAttribute> domainAttribute) {
		String out = "(o.cContext IS NULL OR o.cContext IN(";
		Boolean doIn = false;

		Iterator<String> iterator = domainAttribute.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			// System.out.println("Clave: " + key + " -> Valor: " +
			// domainAttribute.get(key));

			if (Boolean.parseBoolean(domainAttribute.get(key).getValue())) {
				doIn = true;
				out += ("'" + key + "_CONTEXT',");
			}

		}
		if (doIn) {
			out = out.substring(0, out.length() - 2);

			out = out.concat("')) AND ");
		} else {
			out = "o.cContext IS NULL AND";
			// out = out.concat("'')) AND ");
		}
		return out;
	}

	private void addMainMenuToMenu(List<Submenu> mainMenu, Menu menu) {
		for (Submenu submenu : mainMenu) {
			if (!submenu.optionInMenu(submenu.getOption(), menu.list())) {
				menu.addSubmenu(submenu);
			}
		}
	}

	private List<Submenu> getSubmenuFromDB(Connection conn, String sql, List<Object> prms) {
		Submenu submenu = null;
		List<Submenu> out = new ArrayList<Submenu>();

		Option option = null;
		BSBeanUtils bu = new BSBeanUtils();

		ResultSet rs = queryResultSet(conn, sql, prms);
		try {
			while (rs.next()) {
				option = new Option();
				option.setId(rs.getLong("cOption"));
				bu.search(conn, option);

				submenu = new Submenu(option);
				out.add(submenu);
			}
		} catch (SQLException e) {
			throw new BSDataBaseException(e);
		} finally {
			closeSQL(rs);
		}
		return out;
	}

	@Override
	public Boolean optionInMenu(Option opt, List<Submenu> main) {
		Boolean exists = Boolean.FALSE;

		for (Submenu sub : main) {
			if (sub.getOption().getId().equals(opt.getId())) {
				exists = Boolean.TRUE;
				break;
			} else {
				exists = optionInMenu(opt, sub.list());
				if (exists) {
					break;
				}
			}
		}
		return exists;
	}

	@Override
	public Option searchResourceByKey(Connection conn, String key) {
		String sql = "SELECT cId FROM tOption WHERE cKey=? AND cType=2";
		Option out = null;

		String idString = super.queryField(conn, sql, key);
		if (idString != null) {
			Long id = Long.parseLong(idString);
			out = new Option();
			out.setId(id);

			BSBeanUtils bu = new BSBeanUtils();
			bu.search(conn, out);
		}
		return out;
	}

	@Override
	public Option searchOptionByKey(Connection conn, String key) {
		String sql = "SELECT cId FROM tOption WHERE cKey=?";
		Option out = null;

		String idString = super.queryField(conn, sql, key);
		if (idString != null) {
			Long id = Long.parseLong(idString);
			out = new Option();
			out.setId(id);

			BSBeanUtils bu = new BSBeanUtils();
			bu.search(conn, out);
		}
		return out;
	}
}
