package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class Option extends BSBean implements Serializable{
	private static final long serialVersionUID = -1126831609688823765L;
	@SuppressWarnings("unused")
	private String TABLE = "tOption";
	private String key = null;
	private String label = null;
	private String url = null;
	private Long parent = null;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		return "\n\tOption [id=" + super.getId() + ", key=" + key + ", label=" + label + ", url=" + url + ", parent=" + parent
				+ "]";
	}
}
