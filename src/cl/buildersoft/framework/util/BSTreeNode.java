package cl.buildersoft.framework.util;

import java.util.ArrayList;
import java.util.List;

public class BSTreeNode {
	private List<BSTreeNode> children = null;
	private Object value = null;

	public BSTreeNode() {
		super();
		this.children = new ArrayList<BSTreeNode>();

	}

	public List<BSTreeNode> getChildren() {
		return children;
	}

	public void addChildren(BSTreeNode children) {
		this.children.add(children);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}