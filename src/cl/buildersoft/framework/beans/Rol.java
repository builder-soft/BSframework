package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class Rol extends BSBean implements Serializable {
	private static final long serialVersionUID = -1654175168914146612L;
	@SuppressWarnings("unused")
	private String TABLE = "tRol";

	private String name = null;
	private Boolean deleted = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "Rol [id=" + getId() + ", name=" + name + ", deleted=" + deleted + "]";
	}

}
