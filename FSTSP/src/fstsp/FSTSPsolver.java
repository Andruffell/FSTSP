package fstsp;

import tsp.*;

import java.io.IOException;
import java.util.ArrayList;
import parser.Parser;
import parser.ParserData;


public class FSTSPsolver {

	public static String fileName = "M5001.txt";
	
	private static double sr = 0.016667; //tempo di recupero dell'UAV
	private static double sl = 0.016667; //tempo di lancio dell'UAV
	private static double droneBattery = 0.666667; //e metterla nel caso a 40 
	
	public FSTSPsolver() {}
	
	public static double calcSavings(Node jNode, ArrayList<Double> t, ArrayList<Node> Cprime, ArrayList<Node> truckRoute,
			double truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroutes, double UAVadjacencyMatrix[][]) {

		Node iNode = truckRoute.get(truckRoute.indexOf(jNode)-1);
		Node kNode = truckRoute.get(truckRoute.indexOf(jNode)+1);
		
		double savings = truckAdjacencyMatrix[iNode.getId()][jNode.getId()] + truckAdjacencyMatrix[jNode.getId()][kNode.getId()] - truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
		
		for (Subroute tS : truckSubroutes) {
			if (tS.getNodes().contains(jNode) && tS.isUAVserved()) {
				Node a = tS.getNodes().get(0);
				Node b = tS.getNodes().get(tS.getNodes().size()-1);
				Node jPrime = new Node();
				
				for (Node node : tS.getNodes()) {
					if (node.isUAVserved()) {
						jPrime = node;
					}
				}

				double tbPrime = 0.0;
				tbPrime = t.get(truckRoute.indexOf(b));				
				tbPrime += -truckAdjacencyMatrix[iNode.getId()][jNode.getId()] - truckAdjacencyMatrix[jNode.getId()][kNode.getId()] + truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
				
				double ta = t.get(truckRoute.indexOf(a));	//tempo di arrivo del truck in a
				double tauPrimeAJPrime = UAVadjacencyMatrix[a.getId()][jPrime.getId()];	//costo dell'UAV tra il nodo a e il nodo jPrime
				double tauPrimeJprimeB = UAVadjacencyMatrix[jPrime.getId()][b.getId()]; //costo dell'UAV tra il nodo jPrime e il nodo b
				savings = Math.min(
						  savings, 
						  tbPrime - (ta + tauPrimeAJPrime + tauPrimeJprimeB + sr)
						  );
				
				return savings;
			}
		}
		return savings;
	}
	
	public static NodesToUpdate calcCostTruck(Node jNode, ArrayList<Double> t, Subroute subroute, double truckAdjacencyMatrix[][], double savings, double maxSavings, ArrayList<Node> truckRoute, NodesToUpdate nodesToUpdate) {
		
		NodesToUpdate returnVal = nodesToUpdate;
//		returnVal.maxSavings = maxSavings;
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		
		Node aNode = subrouteNodes.get(0);
		Node bNode = subrouteNodes.get(subrouteNodes.size() - 1);
		
		for(int l = 0; l < subrouteNodes.size() - 1; l++) {
			Node iNode = subrouteNodes.get(l);
			if(iNode.equals(jNode)) {
				iNode = subrouteNodes.get(l-1);
			}
			Node kNode = subrouteNodes.get(l+1);
			if(kNode.equals(jNode)) {
				kNode = subrouteNodes.get(l+1);
			}
			
			double tauIJ = truckAdjacencyMatrix[iNode.getId()][jNode.getId()];
			double tauJK = truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
			double tauIK = truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
			
			double cost = tauIJ + tauJK - tauIK;
//			System.out.println("cost truck: " + cost);
			if(cost < savings) {
				int bTauId = truckRoute.indexOf(bNode);
				int aTauId = truckRoute.indexOf(aNode);
				if((t.get(bTauId) - t.get(aTauId) + cost) <= droneBattery) {
					if(savings - cost > maxSavings) {
						jNode.setUAVserved(false);
						subroute.setUAVserved(false);
						returnVal.jStar = jNode;
						returnVal.iStar = iNode;
						returnVal.kStar = kNode;
						returnVal.maxSavings = savings - cost;
						//System.out.println("ciclo truck");
					}
				}
			}
		}
		
		return returnVal;
	}
	
	public static NodesToUpdate calcCostUAV(Node jNode, ArrayList<Double> t, Subroute subroute, double UAVadjacencyMatrix[][], double savings, double maxSavings, ArrayList<Node> truckRoute, double truckAdjacencyMatrix[][], NodesToUpdate nodesToUpdate) {
		
		NodesToUpdate returnVal = nodesToUpdate;
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		if(subrouteNodes.contains(jNode)) {
			int index = subrouteNodes.indexOf(jNode);
		//Node aNode = subrouteNodes.get(0);
		//Node bNode = subrouteNodes.get(subrouteNodes.size() - 1);
		
		/*for(int l = 0; l < subrouteNodes.size() - 2; l++) {
			Node iNode = subrouteNodes.get(l);
			Node kNode = subrouteNodes.get(l+2);
			*/
			
			for( int l =0;l<index;l++) {
				Node iNode = subrouteNodes.get(l);
				for(int m = subrouteNodes.size()-1; m > index; m--) {
					Node kNode = subrouteNodes.get(m);
					double tauprimeIJ = UAVadjacencyMatrix[iNode.getId()][jNode.getId()];
					double tauprimeJK = UAVadjacencyMatrix[jNode.getId()][kNode.getId()];
					if(tauprimeIJ + tauprimeJK <= droneBattery  ) {
						double tkPrime = 0;
						tkPrime = t.get(truckRoute.indexOf(kNode));				
						double negativeTruck = - truckAdjacencyMatrix[iNode.getId()][jNode.getId()] - truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
						tkPrime +=  negativeTruck + truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
						double uavcost = Math.max(tkPrime - t.get(truckRoute.indexOf(iNode))+ sr + sl ,tauprimeIJ + tauprimeJK+ sr + sl  );
						double cost = Math.max(0,uavcost -(tkPrime - t.get(truckRoute.indexOf(iNode))) );
						//System.out.println("cost UAV: " + cost);
						if ( savings - cost > maxSavings ) {
							jNode.setUAVserved(true);
							subroute.setUAVserved(true);
							returnVal.jStar = jNode;
							returnVal.iStar = iNode;
							returnVal.kStar = kNode;
							returnVal.maxSavings = savings - cost;
							//System.out.println("ciclo UAV");
						}
					}
					
				
				}
			}
		}
		return returnVal;
}
	
	public static RouteToUpdate performUpdate(NodesToUpdate nodesToUpdate, ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Node> Cprime, ArrayList<Double> t, double truckAdjacencyMatrix[][]) {
		
		RouteToUpdate returnVal = new RouteToUpdate(truckRoute, truckSubroutes, Cprime, new ArrayList<Double>());
		
		truckRoute.remove(nodesToUpdate.jStar);
		
		if(nodesToUpdate.jStar.isUAVserved()) {
			for (Subroute sub : returnVal.truckSubroutes) {
				if(sub.getNodes().remove(nodesToUpdate.jStar)) {
					sub.setUAVserved(false);
				}
//				sub.getNodes().remove(nodesToUpdate.kStar);
			}
			
//			ArrayList<Node> newList = (ArrayList<Node>) truckRoute.subList((truckRoute.indexOf(nodesToUpdate.iStar)), truckRoute.indexOf(nodesToUpdate.kStar));
			ArrayList<Node> newList = new ArrayList<>();
			int maxIndex = nodesToUpdate.kStar.getId() == 0 ? truckRoute.size()-1 : truckRoute.indexOf(nodesToUpdate.kStar);
			for(int i= truckRoute.indexOf(nodesToUpdate.iStar); i<=maxIndex; i++) {
				newList.add(truckRoute.get(i));
			}
			
			for(Subroute sub : returnVal.truckSubroutes) {
				for(Node n : newList) {
					sub.getNodes().remove(n);
				}
				//if(sub.getNodes().isEmpty()) {returnVal.truckSubroutes.remove(sub);}
			}
			
			Subroute newSubroute = new Subroute(newList, true);
			returnVal.truckSubroutes.add(newSubroute);
				
			returnVal.Cprime.remove(nodesToUpdate.iStar);
			returnVal.Cprime.remove(nodesToUpdate.jStar);
			returnVal.Cprime.remove(nodesToUpdate.kStar);
			
		} else {
				for (Subroute sub : returnVal.truckSubroutes) {
					sub.getNodes().remove(nodesToUpdate.jStar);
				}
				
				for (Subroute sub : returnVal.truckSubroutes) {
					if (sub.getNodes().contains(nodesToUpdate.iStar) && sub.getNodes().contains(nodesToUpdate.kStar)){
						sub.getNodes().add(sub.getNodes().indexOf(nodesToUpdate.kStar), nodesToUpdate.jStar);
					}
				}
				
				returnVal.truckRoute.add(returnVal.truckRoute.indexOf(nodesToUpdate.kStar), nodesToUpdate.jStar);
			}
		
		returnVal.t.add(0.0);
		for(int l=1; l<returnVal.truckRoute.size(); l++) {
			int previousNodeIndex = returnVal.truckRoute.get(l-1).getId();
			int currentNodeIndex = returnVal.truckRoute.get(l).getId();
			returnVal.t.add(returnVal.t.get(l-1) + truckAdjacencyMatrix[previousNodeIndex][currentNodeIndex]);
		}
		
		return returnVal;
	}
		

	
	public static void main(String[] args) throws IOException {
		ParserData p = new ParserData();
		Parser parser = new Parser("./src/data/" + fileName);
		
		p = parser.ReadFile();


		TSPsolver tspNearestNeighbour = new TSPsolver();
		
		tspNearestNeighbour.tsp(p.TruckMatrix);	//risoluzione con il TSP
		
		System.out.println(tspNearestNeighbour.getTempiDiArrivo() + " ");
		System.out.println("Percorso del TSP: " + tspNearestNeighbour.getList());
		
		ArrayList<Node> truckRoute = new ArrayList<>();
//		System.out.print("Conversione a truckRoute: ");
		ArrayList<Integer> canBeserved=new ArrayList<>(p.served);
		System.out.print("Can be served : ");
		System.out.println(canBeserved);
		
		for (Integer node : tspNearestNeighbour.getList()) {
			boolean served = canBeserved.get(node)==1?true:false; 
			truckRoute.add(new Node(node,served,false));	
		}	
		
		ArrayList<Node> Cprime = new ArrayList<>(); 	//creazione Cprime
		
//		System.out.print("\nNodi che permettono la consegna con UAV: ");
		for (Node node : truckRoute) {
			if (node.isUAVeligible()) {
				Cprime.add(node);
//				System.out.print("\t" + node.getId());
			}
		}	
		
		ArrayList<Subroute> truckSubroutes = new ArrayList<>();
		truckSubroutes.add(new Subroute(truckRoute));	//creazione truckSubroute

//		System.out.println("\nSottogiri del camion: " + truckSubroutes);
		
		ArrayList<Double> t = tspNearestNeighbour.getTempiDiArrivo();
		
		double maxSavings = 0; //maxSavings
		boolean stop = false;
		double savings = 0;
		
		NodesToUpdate nodesToUpdate = new NodesToUpdate();
		do {
			for (Node j : Cprime) {
				savings = calcSavings(j, t, Cprime, truckRoute, p.TruckMatrix, truckSubroutes, p.UAVMatrix);
				//System.out.println("Saving " + j.getId() + " : " + savings);
				for (Subroute subroute : truckSubroutes) {
					if (subroute.isUAVserved()) {
						nodesToUpdate = calcCostTruck(j, t, subroute, p.TruckMatrix, savings, maxSavings, truckRoute, nodesToUpdate);
					} else {
						nodesToUpdate = calcCostUAV(j, t, subroute, p.UAVMatrix, savings, maxSavings, truckRoute, p.TruckMatrix, nodesToUpdate);
					}
					maxSavings = nodesToUpdate.maxSavings;
//					System.out.println("maxsavings = " + maxSavings);
				}
			}
			if (maxSavings > 0) {
				RouteToUpdate newRoutes = performUpdate(nodesToUpdate, truckRoute, truckSubroutes, Cprime, t, p.TruckMatrix);
				truckRoute = newRoutes.truckRoute;
				truckSubroutes = newRoutes.truckSubroutes;
				Cprime = newRoutes.Cprime;
				t = newRoutes.t;
				maxSavings = 0;
				nodesToUpdate = new NodesToUpdate();
			} else {
				stop = true;
			}
		} while (!stop);
		
		for (Node node : truckRoute) {
			System.out.print("\t" + node.getId());
		}
		System.out.println("\n" + t);
		for ( Subroute sb : truckSubroutes) {
			for (Node nd : sb.getNodes()) {
				System.out.print(nd.getId() +"\t");
			}
			System.out.println(" ");
		}
	}
}
