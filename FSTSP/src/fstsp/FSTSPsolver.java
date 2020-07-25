package fstsp;

import tsp.*;
import java.util.ArrayList;


public class FSTSPsolver {

	private static int sr = 1; //tempo di recupero dell'UAV
	private static int sl = 1; //tempo di lancio dell'UAV
	private static int droneBattery = 1000; //e
	
	public FSTSPsolver() {}
	
	public static int calcSavings(Node jNode, ArrayList<Integer> t, ArrayList<Node> Cprime, ArrayList<Node> truckRoute,
			int truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroutes, int UAVadjacencyMatrix[][]) {

		Node iNode = truckRoute.get(truckRoute.indexOf(jNode)-1);
		Node kNode = truckRoute.get(truckRoute.indexOf(jNode)+1);
		
		int savings = truckAdjacencyMatrix[iNode.getId()][jNode.getId()] + truckAdjacencyMatrix[jNode.getId()][kNode.getId()] -  truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
		
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

				int tbPrime = 0;
				
				for(int l = 0; l < truckRoute.indexOf(b)-1; l++) {
						tbPrime += truckAdjacencyMatrix[truckRoute.get(l).getId()][truckRoute.get(l+1).getId()];
				}
				
				tbPrime = -truckAdjacencyMatrix[iNode.getId()][jNode.getId()] - truckAdjacencyMatrix[jNode.getId()][kNode.getId()] + truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
				
				int ta = t.get(truckRoute.indexOf(a));	//tempo di arrivo del truck in a
				int tauPrimeAJPrime = UAVadjacencyMatrix[a.getId()][jPrime.getId()];	//costo dell'UAV tra il nodo a e il nodo jPrime
				int tauPrimeJprimeB = UAVadjacencyMatrix[jPrime.getId()][b.getId()]; //costo dell'UAV tra il nodo jPrime e il nodo b
				savings = Math.min(
						  savings, 
						  tbPrime - (ta + tauPrimeAJPrime + tauPrimeJprimeB + sr)
						  );
				
				return savings;
			}
		}
		return savings;
	}
	
	public static NodesToUpdate calcCostTruck(Node jNode, ArrayList<Integer> t, Subroute subroute, int truckAdjacencyMatrix[][], int savings, int maxSavings, ArrayList<Node> truckRoute) {
		
		NodesToUpdate returnVal = new NodesToUpdate();
		
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		
		Node aNode = subrouteNodes.get(0);
		Node bNode = subrouteNodes.get(subrouteNodes.size() - 1);
		
		for(int l = 0; l < subrouteNodes.size() - 1; l++) {
			Node iNode = subrouteNodes.get(l);
			Node kNode = subrouteNodes.get(l+1);
			
			int tauIJ = truckAdjacencyMatrix[iNode.getId()][jNode.getId()];
			int tauJK = truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
			int tauIK = truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
			
			int cost = tauIJ + tauJK - tauIK;
			System.out.println("cost truck: " + cost);
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
						System.out.println("ciclo truck");
					}
				}
			}
		}
		
		return returnVal;
	}
	
	public static NodesToUpdate calcCostUAV(Node jNode, ArrayList<Integer> t, Subroute subroute, int UAVadjacencyMatrix[][], int savings, int maxSavings, ArrayList<Node> truckRoute, int truckAdjacencyMatrix[][]) {
		
		NodesToUpdate returnVal = new NodesToUpdate();
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		
		//Node aNode = subrouteNodes.get(0);
		//Node bNode = subrouteNodes.get(subrouteNodes.size() - 1);
		
		for(int l = 0; l < subrouteNodes.size() - 1; l++) {
			Node iNode = subrouteNodes.get(l);
			Node kNode = subrouteNodes.get(l+1);
			int tauprimeIJ = UAVadjacencyMatrix[iNode.getId()][jNode.getId()];
			int tauprimeJK = UAVadjacencyMatrix[jNode.getId()][kNode.getId()];
			
			if(tauprimeIJ + tauprimeJK <= droneBattery  ) {
				int tkPrime = 0;
				
				for(int m = 0; m < truckRoute.indexOf(kNode)-1; m++) {
						tkPrime += truckAdjacencyMatrix[truckRoute.get(m).getId()][truckRoute.get(m+1).getId()];
				}
				
				tkPrime = - truckAdjacencyMatrix[iNode.getId()][jNode.getId()] - truckAdjacencyMatrix[jNode.getId()][kNode.getId()] + truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
				int uavcost = Math.max(tkPrime - t.get(truckRoute.indexOf(iNode))+ sr + sl ,tauprimeIJ + tauprimeJK+ sr + sl  );
				int cost = Math.max(0,uavcost -(tkPrime - t.get(truckRoute.indexOf(iNode))) );
				System.out.println("cost UAV: " + cost);
				if ( savings -cost > maxSavings ) {
					jNode.setUAVserved(true);
					subroute.setUAVserved(true);
					returnVal.jStar = jNode;
					returnVal.iStar = iNode;
					returnVal.kStar = kNode;
					returnVal.maxSavings = savings - cost;
					System.out.println("ciclo UAV");
				}
			}
			
		
	}
		return returnVal;
}
	
	public static RouteToUpdate performUpdate(NodesToUpdate nodesToUpdate, ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Node> Cprime, ArrayList<Integer> t, int truckAdjacencyMatrix[][]) {
		
		RouteToUpdate returnVal = new RouteToUpdate(truckRoute, truckSubroutes, Cprime, t);
		
		truckRoute.remove(nodesToUpdate.jStar);
		
		if(nodesToUpdate.jStar.isUAVserved()) {
			for (Subroute sub : returnVal.truckSubroutes) {
				sub.getNodes().remove(nodesToUpdate.jStar);
			}
			
			ArrayList<Node> newList = (ArrayList<Node>) truckRoute.subList((truckRoute.indexOf(nodesToUpdate.iStar)), truckRoute.indexOf(nodesToUpdate.kStar));
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
		
		returnVal.t.add(0);
		for(int l=1; l<returnVal.truckRoute.size(); l++) {
			int previousNodeIndex = returnVal.truckRoute.get(l-1).getId();
			int currentNodeIndex = returnVal.truckRoute.get(l).getId();
			returnVal.t.add(returnVal.t.get(l-1) + truckAdjacencyMatrix[previousNodeIndex][currentNodeIndex]);
		}
		
		return returnVal;
	}
		

	
	public static void main(String[] args) {
		
		int truckAdjacencyMatrix[][] = {{0,5,15,5,70},
										{5,0,50,2,3},
										{15,50,0,40,10},
										{5,2,40,0,3},
										{70,3,10,3,0}};


		TSPsolver tspNearestNeighbour = new TSPsolver();
		tspNearestNeighbour.tsp(truckAdjacencyMatrix);	//risoluzione con il TSP
	
		System.out.println("Percorso del TSP: " + tspNearestNeighbour.getList());
		
		ArrayList<Node> truckRoute = new ArrayList<>();
//		System.out.print("Conversione a truckRoute: ");

		for (Integer node : tspNearestNeighbour.getList()) {
			if (node == 3) {
				truckRoute.add(new Node(node, true, true));
			}else if (node%2 == 1){
				truckRoute.add(new Node(node, true, false));
			} else {
				truckRoute.add(new Node(node, false, false));
			}
//			System.out.print("\t" + node);
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
		truckSubroutes.add(new Subroute(truckRoute, true));	//creazione truckSubroute

//		System.out.println("\nSottogiri del camion: " + truckSubroutes);
		
		ArrayList<Integer> t = tspNearestNeighbour.getTempiDiArrivo();
		
		int UAVadjacencyMatrix[][] = {{0,3,1,1,1},
								      {3,0,1,1,2},
								      {1,1,0,4,1},
								      {1,1,4,0,1},
								      {1,2,1,1,0}
								      };
		
		int maxSavings = 0; //maxSavings
		boolean stop = false;
		int savings = 0;
		
		NodesToUpdate nodesToUpdate = new NodesToUpdate();
		do {
			for (Node j : Cprime) {
				savings = calcSavings(j, t, Cprime, truckRoute, truckAdjacencyMatrix, truckSubroutes, UAVadjacencyMatrix);
				System.out.println("Saving " + j.getId() + " : " + savings);
				for (Subroute subroute : truckSubroutes) {
					if (subroute.isUAVserved()) {
						nodesToUpdate = calcCostTruck(j, t, subroute, truckAdjacencyMatrix, savings, maxSavings, truckRoute);
					} else {
						nodesToUpdate = calcCostUAV(j, t, subroute, UAVadjacencyMatrix, savings, maxSavings, truckRoute, truckAdjacencyMatrix);
					}
					maxSavings = nodesToUpdate.maxSavings;
					System.out.println("maxsavings = " + maxSavings);
				}
			}
			if (maxSavings > 0) {
				RouteToUpdate newRoutes = performUpdate(nodesToUpdate, truckRoute, truckSubroutes, Cprime, t, truckAdjacencyMatrix);
				truckRoute = newRoutes.truckRoute;
				truckSubroutes = newRoutes.truckSubroutes;
				Cprime = newRoutes.Cprime;
				t = newRoutes.t;
				maxSavings = 0;
				
			} else {
				stop = true;
			}
		} while (!stop);
		
		for (Node node : truckRoute) {
			System.out.print("\t" + node.getId());
		}
		System.out.println("\n" + t);
	}
}
