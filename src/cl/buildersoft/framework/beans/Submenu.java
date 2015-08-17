package cl.buildersoft.framework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Submenu implements Serializable {
	private static final long serialVersionUID = 9072040188432813085L;
	private Option option = null;
	private List<Submenu> submenuList = null;

	public Submenu(Option option) {
		this.setOption(option);
		this.submenuList = new ArrayList<Submenu>();
	}

	public void setOption(Option option) {
		this.option = option;
	}

	public Option getOption() {
		return this.option;
	}

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

	public void addSubmenu(List<Submenu> submenuList, Menu menu) {
		for (Submenu submenu : submenuList) {
			if (menu != null) {
				if (!optionInMenu(submenu.getOption(), menu.list())) {
					this.submenuList.add(submenu);
				}
			} else {
				this.submenuList.add(submenu);
			}

		}

	}

	/**
	 * <code>
	public void addSubmenu(List<Submenu> submenuList) {
		for (Submenu submenu : submenuList) {
			this.submenuList.add(submenu);
		}
	}
</code>
	 */
	public void addSubmenu(Submenu submenu) {
		this.submenuList.add(submenu);
	}

	public List<Submenu> list() {
		return this.submenuList;
	}

	@Override
	public String toString() {
		return "Submenu [option=" + option + ", submenuList=" + submenuList + "]\n\t";
	}
}
