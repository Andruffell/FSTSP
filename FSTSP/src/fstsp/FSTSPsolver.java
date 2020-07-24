package fstsp;

import tsp.*;
import java.util.ArrayList;


public class FSTSPsolver {

	private static int sr = 1; //tempo di recupero dell'UAV
	private static int droneBattery = 10; //e
	
	public FSTSPsolver() {}
	
	public static int calcSavings(Node jNode, ArrayList<Integer> t, ArrayList<Node> Cprime, ArrayList<Node> truckRoute,
			int truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroutes, int UAVadjacencyMatrix[][]) {

		int jIndex = jNode.getId();
		int iIndex = truckRoute.get(truckRoute.indexOf(jNode)-1).getId();
		int kIndex = truckRoute.get(truckRoute.indexOf(jNode)+1).getId();
		
		int savings = truckAdjacencyMatrix[iIndex][jIndex] + truckAdjacencyMatrix[jIndex][kIndex] -  truckAdjacencyMatrix[iIndex][kIndex];
		
		for (Subroute tS : truckSubroutes) {
			if (tS.getNodes().contains(jNode) && tS.isUAVserved()) {
				int a = 0;
				int b = tS.getNodes().size()-1;
				Node jPrime = new Node();
				
				for (Node node : tS.getNodes()) {
					if (node.isUAVserved()) {
						jPrime = node;
					}
				}

//				int jPrevId = tS.getNodes().get(tS.getNodes().indexOf(jNode)-1).getId(); //indice del nodo che precede j
//				int jindex = tS.getNodes().get(tS.getNodes().indexOf(jNode)).getId();	//indice di j
//				int ajindex = tS.getNodes().get(tS.getNodes().indexOf(jNode)+1).getId(); //indice del nodo che segue j

				int tbPrime = 0;
				
				for(int l = 0; l < truckRoute.indexOf(tS.getNodes().get(b))-1; l++) {
						tbPrime += truckAdjacencyMatrix[truckRoute.get(l).getId()][truckRoute.get(l+1).getId()];
				}
				
				tbPrime = -truckAdjacencyMatrix[iIndex][jIndex] - truckAdjacencyMatrix[jIndex][kIndex] + truckAdjacencyMatrix[iIndex][kIndex];
				
				int ta = t.get(tS.getNodes().get(a).getId());	//tempo di arrivo del truck in a
				int tauPrimeAJPrime = UAVadjacencyMatrix[tS.getNodes().get(a).getId()][jPrime.getId()];	//costo dell'UAV tra il nodo a e il nodo jPrime
				int tauPrimeJprimeB = UAVadjacencyMatrix[jPrime.getId()][tS.getNodes().get(b).getId()]; //costo dell'UAV tra il nodo jPrime e il nodo b
				savings = Math.min(
						  savings, 
						  tbPrime - ta + tauPrimeAJPrime + tauPrimeJprimeB + sr
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
			
			if(cost < savings) {
				int bTauId = truckRoute.indexOf(bNode);
				int aTauId = truckRoute.indexOf(aNode);
				if((t.get(bTauId) - t.get(aTauId) + cost) <= droneBattery) {
					if(savings - cost > maxSavings) {
						jNode.setUAVserved(false);
						returnVal.jStar = jNode;
						returnVal.iStar = iNode;
						returnVal.kStar = kNode;
						returnVal.maxSavings = savings - cost;
					}
				}
			}
		}
		
		return returnVal;
	}
	
	public static void main(String[] args) {
		
		int truckAdjacencyMatrix[][] = {{0,5,15,5,7},
										{5,0,5,20,3},
										{15,5,0,4,1},
										{5,20,4,0,3},
										{7,3,1,3,0}};


		TSPsolver tspNearestNeighbour = new TSPsolver();
		tspNearestNeighbour.tsp(truckAdjacencyMatrix);
	
		System.out.println("Percorso del TSP: " + tspNearestNeighbour.getList());
		
		ArrayList<Node> truckRoute = new ArrayList<>();
		System.out.print("Conversione a truckRoute: ");

		for (Integer node : tspNearestNeighbour.getList()) {
			if (node == 3) {
				truckRoute.add(new Node(node, true, true));
			}else if (node%2 == 1){
				truckRoute.add(new Node(node, true, false));
			} else {
				truckRoute.add(new Node(node, false, false));
			}
			System.out.print("\t" + node);
		}
		
		
		ArrayList<Node> Cprime = new ArrayList<>();
		
		System.out.print("\nNodi che permettono la consegna con UAV: ");
		for (Node node : truckRoute) {
			if (node.isUAVeligible()) {
				Cprime.add(node);
				System.out.print("\t" + node.getId());
			}
		}	
		
		ArrayList<Subroute> truckSubroutes = new ArrayList<>();
		truckSubroutes.add(new Subroute(truckRoute, true));

		System.out.println("\nSottogiri del camion: " + truckSubroutes);
		
		ArrayList<Integer> t = tspNearestNeighbour.getTempiDiArrivo();
		int M = Integer.MAX_VALUE;
		
		
		int UAVadjacencyMatrix[][] = {{0,3,1,1,1},
								      {3,0,1,1,2},
								      {1,1,0,4,1},
								      {1,1,4,0,1},
								      {1,2,1,1,0}
								      };
		
		for (Node j : Cprime) {
			int savings = calcSavings(j, t, Cprime, truckRoute, truckAdjacencyMatrix, truckSubroutes, UAVadjacencyMatrix);
			System.out.println("Saving " + j.getId() + " : " + savings);
		}
		
	}
}
