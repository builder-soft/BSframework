package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class Domain extends BSBean implements Serializable {
	private static final long serialVersionUID = 372116778968735407L;
	private String name = null;
	private String database = null;
	@SuppressWarnings("unused")
	private String TABLE = "tDomain";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	@Override
	public String toString() {
		return "Domain [name=" + name + ", database=" + database + ", Id=" + getId() + "]";
	}

	 

}
