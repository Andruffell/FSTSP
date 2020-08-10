package fstsp;

import java.util.ArrayList;
/**
 * Classe che contiene una subroute e una variabile booleana true se la subroute è servita dall'UAV
 */
public class Subroute {
	private ArrayList<Node> nodes; //nodi della subroute
	private boolean UAVserved; //true se la subroute è servita dall'UAV
	
	/**
	 * Costruttore
	 * @param subroute la subroute
	 * @param uAVserved true se la subroute è servita dall'UAV
	 */
	public Subroute(ArrayList<Node> subroute, boolean uAVserved) {
		super();
		this.nodes = subroute;
		UAVserved = uAVserved;
	}
	/**
	 * Costruttore di copia
	 * @param s elemento da copiare
	 */
	@SuppressWarnings("unchecked")
	public Subroute(ArrayList<Node> s) {
		nodes  = (ArrayList<Node>) s.clone();
		UAVserved = false;
	}

	/**
	 * getter per i nodi
	 * @return i nodi della subroute
	 */
	public ArrayList<Node> getNodes() {return nodes;}
	/**
	 * getter per UAV served
	 * @return true se la subroute è servita dall'UAV
	 */
	public boolean isUAVserved() {return UAVserved;}
	/**
	 * Setter per i nodi
	 * @param subroute la subroute da inserire
	 */
	public void setNodes(ArrayList<Node> subroute) {this.nodes = subroute;}
	/**
	 * Setter per UAV served
	 * @param uAVserved true se la subroute è servita dall'UAV
	 */
	public void setUAVserved(boolean uAVserved) {UAVserved = uAVserved;}
}
