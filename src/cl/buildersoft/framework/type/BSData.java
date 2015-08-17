package cl.buildersoft.framework.type;

import java.io.Serializable;
@Deprecated
public class BSData implements Serializable{

	private static final long serialVersionUID = 6783147765454853907L;
	private String value;
	private boolean state = true;
	
	public BSData(String value)
	{
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	@Override
	public String toString() {
		return "BSData [value=" + value + ", state=" + state + "]";
	}
	
	
}
