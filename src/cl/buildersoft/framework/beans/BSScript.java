package cl.buildersoft.framework.beans;

import java.util.List;

public class BSScript {
private String path = null;
private List<String> listScriptNames = null;

public BSScript(String path) {
	this.path = path;
}
public String getPath() {
	return path;
}
public void setPath(String path) {
	this.path = path;
}
public List<String> getListScriptNames() {
	return listScriptNames;
}
public void setListScriptNames(List<String> listScriptNames) {
	this.listScriptNames = listScriptNames;
}

}
