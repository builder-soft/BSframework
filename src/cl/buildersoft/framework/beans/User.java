package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class User extends BSBean implements Serializable {

	private static final long serialVersionUID = 68866001748593379L;
	private String mail = null;
	private String name = null;
	private String password = null;
	private Boolean admin = null;
	@SuppressWarnings("unused")
	private String TABLE = "bsframework.tUser";

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

}
