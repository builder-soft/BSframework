package cl.buildersoft.framework.beans;

public class RUserRol extends BSBean {
	private static final long serialVersionUID = -2596360240483281028L;
	@SuppressWarnings("unused")
	private String TABLE = "tR_UserRol";
	private Long user = null;
	private Long rol = null;

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Long getRol() {
		return rol;
	}

	public void setRol(Long rol) {
		this.rol = rol;
	}
}
