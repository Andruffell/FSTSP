package fstsp;

public class Node {
	private int id; 
	private boolean UAVeligible;
	private boolean UAVserved;
	
	public Node() {
		super();
		this.id = 0;
		UAVeligible = false;
		UAVserved = false;
	}
	
	public Node(int id, boolean uAVeligible, boolean uAVserved) {
		super();
		this.id = id;
		UAVeligible = uAVeligible;
		UAVserved = uAVserved;
	}

	public int getId() {
		return id;
	}

	public boolean isUAVeligible() {
		return UAVeligible;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setUAVeligible(boolean uAVeligible) {
		UAVeligible = uAVeligible;
	}

	public boolean isUAVserved() {
		return UAVserved;
	}

	public void setUAVserved(boolean uAVserved) {
		UAVserved = uAVserved;
	}
	
}
