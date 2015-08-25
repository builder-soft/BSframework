package cl.buildersoft.framework.dataType;

public enum BSDataTypeEnum {

	STRING(0), INTEGER(1), DOUBLE(2), LONG(3), BOOLEAN(4), DATE(5), TIMESTAMP(6), CALENDAR(7), DECIMAL(8), TEXT(9);

	private Integer value = null;

	private BSDataTypeEnum(Integer value) {
		this.value = value;
	}
	/**
	 * <code>
	public Integer getValue() {
		return this.value;
	}
</code>
	 */
}
