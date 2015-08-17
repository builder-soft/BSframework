package cl.buildersoft.framework.util.crud;

import java.util.Arrays;
import java.util.List;

import cl.buildersoft.framework.dataType.BSDataType;

public class BSField {
	private String name = null;
	private String label = null;
	private Boolean pk = null;
	private Boolean unique = Boolean.TRUE;
	private Boolean readonly = Boolean.FALSE;
	private Integer length = null;
	private BSDataType type = null;
	private Object value = null;
	private String validationOnBlur = null;
	private Boolean visible = Boolean.TRUE;
	private String[] fkInfo = null;
	private List<Object[]> fkData = null;
	private String typeHtml = null;

	public BSField(String name, String label) {
		super();
		this.name = name;
		this.label = label;
	}

	@Deprecated
	public Boolean isId() {
		return "id".equalsIgnoreCase(this.name) || "cid".equalsIgnoreCase(this.name);
	}

	public Boolean showField() {
		Boolean out = !isPK() && isVisible();
		return out;
	}

	public Boolean isFK() {
		Boolean out = Boolean.FALSE;
		List<Object[]> data = getFKData();
		out = data != null;
		return out;
	}

	/**
	 * <code>
	public Boolean isNumber() {
		return getType().equals(BSFieldType.Double) || getType().equals(BSFieldType.Integer)
				|| getType().equals(BSFieldType.Long);
	}

	public Boolean isTime() {
		return getType().equals(BSFieldType.Date) || getType().equals(BSFieldType.Timestamp);
	}
</code>
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Boolean isPK() {
		return pk;
	}

	public void setPK(Boolean pk) {
		this.pk = pk;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public BSDataType getType() {
		return type;
	}

	public void setType(BSDataType type) {
		this.type = type;
	}

	public Object getValue() {
		return value;
	}

	public String getValueAsString() {
		return getValue().toString();
	}

	public Long getValueAsLong() {
		return Long.parseLong(getValueAsString());
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public Boolean isUnique() {
		return unique;
	}

	public void setUnique(Boolean unique) {
		this.unique = unique;
	}

	public String getValidationOnBlur() {
		return validationOnBlur;
	}

	public void setValidationOnBlur(String validationOnBlur) {
		this.validationOnBlur = validationOnBlur;
	}

	public Boolean isVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	@Deprecated
	public String getFKDatabase() {
		return fkInfo != null ? fkInfo[0] : null;
	}

	@Deprecated
	public String getFKTable() {
		return fkInfo != null ? fkInfo[1] : null;
	}

	@Deprecated
	public String getFKField() {
		return fkInfo != null ? fkInfo[2] : null;
	}

	@Deprecated
	public void setFK(String fkDatabase, String fkTable, String fkField) {
		this.fkInfo = new String[3];
		this.fkInfo[0] = fkDatabase;
		this.fkInfo[1] = fkTable;
		this.fkInfo[2] = fkField;
	}

	public List<Object[]> getFKData() {
		return fkData;
	}

	public void setFkData(List<Object[]> fkData) {
		this.fkData = fkData;
	}

	public void setFKInfo(String[] fkInfo) {
		this.fkInfo = fkInfo;
	}

	public String[] getFKInfo() {
		return this.fkInfo;
	}

	@Override
	public String toString() {
		return "BSField [name=" + name + ", label=" + label + ", pk=" + pk + ", unique=" + unique + ", readonly=" + readonly
				+ ", length=" + length + ", type=" + type + ", value=" + value + ", validationOnBlur=" + validationOnBlur
				+ ", visible=" + visible + ", fk=" + Arrays.toString(fkInfo) + ", fkData=" + fkData + "]";
	}

	public String getTypeHtml() {
		return typeHtml;
	}

	public void setTypeHtml(String typeHtml) {
		this.typeHtml = typeHtml;
	}
}
