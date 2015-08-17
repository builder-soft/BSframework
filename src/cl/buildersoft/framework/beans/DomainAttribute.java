package cl.buildersoft.framework.beans;

public class DomainAttribute extends BSBean {
	private static final long serialVersionUID = -1348815045687895170L;
	/**
	 * <code>
CREATE TABLE IF NOT EXISTS tDomainAttribute (
	cId			BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
	cDomain		BIGINT  NOT NULL,
	cKey		VARCHAR(10) NULL,
	cName		VARCHAR(20) NULL,
	cValue		VARCHAR(50) NULL
) ENGINE=innoDB;
</code>
	 */
	private Long domain = null;
	private String key = null;
	private String name = null;
	private String value = null;
	@SuppressWarnings("unused")
	private String TABLE = "tDomainAttribute";

	public Long getDomain() {
		return domain;
	}

	public void setDomain(Long domain) {
		this.domain = domain;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
