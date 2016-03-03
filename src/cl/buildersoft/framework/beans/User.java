package cl.buildersoft.framework.beans;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import cl.buildersoft.framework.util.BSDateTimeUtil;

public class User extends BSBean implements Serializable {

	private static final long serialVersionUID = 68866001748593379L;
	private String mail = null;
	private String name = null;
	private String password = null;
	private Boolean admin = null;
	private Date lastChangePass = null;
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

	@Override
	public String toString() {
		return "User [Id=" + getId() + ", name=" + name + ", mail=" + mail + ", admin=" + admin + ", lastChangePass="
				+ BSDateTimeUtil.date2String(lastChangePass, "yyyy-MM-dd") + "]";
	}

	public Date getLastChangePass() {
		return lastChangePass;
	}

	public void setLastChangePass(Date lastChangePass) {
		this.lastChangePass = lastChangePass;
	}

}
