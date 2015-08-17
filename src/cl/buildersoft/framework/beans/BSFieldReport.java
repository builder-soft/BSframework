package cl.buildersoft.framework.beans;


public class BSFieldReport {
	private String name = null;
	private String label = null;

	public BSFieldReport(String name, String label){
		this.name = name;
		this.label=label;
	}

	public String getName() {
		return name;
	}

	public String getLabel() {
		return label;
	}
	
	
	
}
