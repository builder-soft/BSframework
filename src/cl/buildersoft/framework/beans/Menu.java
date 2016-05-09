package cl.buildersoft.framework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Serializable {
	private static final long serialVersionUID = 5145378896631406784L;
	private List<Submenu> submenuList = null;

	public Menu() {
		this.submenuList = new ArrayList<Submenu>();
	}

	public void addSubmenu(Submenu submenu) {
		this.submenuList.add(submenu);
	}

	public List<Submenu> list() {
		return this.submenuList;
	}

	@Override
	public String toString() {
		return "Menu [submenuList=" + submenuList + "]";
	}
}
