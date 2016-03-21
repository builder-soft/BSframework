package cl.buildersoft.framework.beans;

import java.sql.Timestamp;


public class SessionBean extends BSBean {
	private static final long serialVersionUID = -3842283210305227641L;
	@SuppressWarnings("unused")
	private String TABLE = "bsframework.tSession";

	private String token = null;
	private Timestamp lastAccess = null;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Timestamp getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Timestamp lastAccess) {
		this.lastAccess = lastAccess;
	}

	@Override
	public String toString() {
		return "SessionBean [Id=" + getId() + ", lastAccess=" + lastAccess + ", token=" + token + "]";
	}

}
