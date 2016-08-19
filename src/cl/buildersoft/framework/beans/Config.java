package cl.buildersoft.framework.beans;

public class Config extends BSBean {
	private static final long serialVersionUID = 5806109359261336358L;
	private String key = null;
	private String value = null;
	private String TABLE = "tConfig";

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Config [Id=" + getId() + ", key=" + key + ", value=" + value + "]";
	}

}
