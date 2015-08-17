package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class BSBean implements Serializable {
	private static final long serialVersionUID = 2652748543694355210L;
	private Long id = null;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
