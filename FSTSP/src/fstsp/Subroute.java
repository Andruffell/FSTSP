package fstsp;

import java.util.ArrayList;

public class Subroute {
	private ArrayList<Node> nodes;
	private boolean UAVserved;
	
	public Subroute(ArrayList<Node> subroute, boolean uAVserved) {
		super();
		this.nodes = subroute;
		UAVserved = uAVserved;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public boolean isUAVserved() {
		return UAVserved;
	}

	public void setNodes(ArrayList<Node> subroute) {
		this.nodes = subroute;
	}

	public void setUAVserved(boolean uAVserved) {
		UAVserved = uAVserved;
	}
}
