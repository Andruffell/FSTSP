package fstsp;

import tsp.*;
import java.util.ArrayList;


public class FSTSPsolver {

	private int sr = 1; //tempo di recupero dell'UAV
	
	public FSTSPsolver() {}
	
	public int calcSavings(Node jNode, ArrayList<Integer> t, ArrayList<Node> Cprime, ArrayList<Node> truckRoute,
			int truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroute, int UAVadjacencyMatrix[][]) {

		int jIndex = jNode.getId();
		int iIndex = truckRoute.get(truckRoute.indexOf(jNode)-1).getId();
		int kIndex = truckRoute.get(truckRoute.indexOf(jNode)+1).getId();
		
		int savings = truckAdjacencyMatrix[iIndex][jIndex] + truckAdjacencyMatrix[jIndex][kIndex] -  truckAdjacencyMatrix[iIndex][kIndex];
		
		for (Subroute tS : truckSubroute) {
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
	
	public static void main(String[] args) {
		
		
		
	}
}
