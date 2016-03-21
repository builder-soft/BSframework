package cl.buildersoft.framework.beans;


public class SessionDataBean extends BSBean {
	private static final long serialVersionUID = -5016005306711675113L;
	@SuppressWarnings("unused")
	private String TABLE = "bsframework.tSessionData";

	private Long session = null;
	private String name = null;
	private String data = null;

	public Long getSession() {
		return session;
	}

	public void setSession(Long session) {
		this.session = session;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
