package fstsp;

import java.util.ArrayList;

public class Subroute {
	private ArrayList<Node> subroute;
	private boolean UAVserved;
	
	public Subroute(ArrayList<Node> subroute, boolean uAVserved) {
		super();
		this.subroute = subroute;
		UAVserved = uAVserved;
	}

	public ArrayList<Node> getSubroute() {
		return subroute;
	}

	public boolean isUAVserved() {
		return UAVserved;
	}

	public void setSubroute(ArrayList<Node> subroute) {
		this.subroute = subroute;
	}

	public void setUAVserved(boolean uAVserved) {
		UAVserved = uAVserved;
	}
}
