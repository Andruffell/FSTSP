package fstsp;

import java.util.ArrayList;

public class RouteToUpdate {
	public ArrayList<Node> truckRoute;
	public ArrayList<Subroute> truckSubroutes;
	public ArrayList<Node> Cprime;
	public ArrayList<Double> t;
	
	public RouteToUpdate(ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Node> cprime,
			ArrayList<Double> t) {
		super();
		this.truckRoute = truckRoute;
		this.truckSubroutes = truckSubroutes;
		Cprime = cprime;
		this.t = t;
	}
	
	
}
