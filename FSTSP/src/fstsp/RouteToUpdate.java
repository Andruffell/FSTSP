package fstsp;

import java.util.ArrayList;
/**
 * Classe che contiene la route da aggiornare
 */
public class RouteToUpdate {
	public ArrayList<Node> truckRoute;
	public ArrayList<Subroute> truckSubroutes;
	public ArrayList<Node> cPrime;
	public ArrayList<Double> t;
	
	/**
	 * Costruttore
	 * @param truckRoute la route del truck
	 * @param truckSubroutes le subroutes del truck
	 * @param cPrime vettore dei noid che possono essere serviti dall'UAV
	 * @param t vettore dei tempi di arrivo
	 */
	public RouteToUpdate(ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Node> cprime,
			ArrayList<Double> t) {
		super();
		this.truckRoute = truckRoute;
		this.truckSubroutes = truckSubroutes;
		cPrime = cprime;
		this.t = t;
	}
	
	
}
