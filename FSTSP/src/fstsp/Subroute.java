package fstsp;

import java.util.LinkedList;

public class Subroute {
	private LinkedList<Node> nodes;
	private boolean UAVserved;
	
	public Subroute(LinkedList<Node> subroute, boolean uAVserved) {
		super();
		this.nodes = subroute;
		UAVserved = uAVserved;
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	public boolean isUAVserved() {
		return UAVserved;
	}

	public void setNodes(LinkedList<Node> subroute) {
		this.nodes = subroute;
	}

	public void setUAVserved(boolean uAVserved) {
		UAVserved = uAVserved;
	}
}
