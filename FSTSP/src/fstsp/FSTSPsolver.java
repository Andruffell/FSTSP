package fstsp;

import tsp.*;
import java.util.ArrayList;

public class FSTSPsolver {

	private int sr = 1; //tempo di recupero dell'UAV
	
	public FSTSPsolver() {}
	
	public int calcSavings(Node jN, ArrayList<Integer> t, ArrayList<Node> Cprime, 
			int truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroute, int UAVadjacencyMatrix[][]) {
//		Node i = Cprime.get(Cprime.indexOf(j)-1);
//		Node k = Cprime.get(Cprime.indexOf(j)+1);
		int j = Cprime.indexOf(jN);
		int i = Cprime.indexOf(jN)-1;
		int k = Cprime.indexOf(jN)+1;
		int savings = truckAdjacencyMatrix[i][j] + truckAdjacencyMatrix[j][k] -  truckAdjacencyMatrix[i][k];
		
		for (Subroute tS : truckSubroute) {
			if (tS.getSubroute().contains(j) && tS.isUAVserved()) {
				int a = 0;
				int b = tS.getSubroute().size()-1;
				Node jPrime = new Node();
				
				for (Node node : tS.getSubroute()) {
					if (node.isUAVserved()) {
						jPrime = node;
					}
				}
				int bjindex = tS.getSubroute().get(tS.getSubroute().indexOf(j)-1).getId();
				int jindex = tS.getSubroute().get(tS.getSubroute().indexOf(j)).getId();
				int ajindex = tS.getSubroute().get(tS.getSubroute().indexOf(j)+1).getId();
				int tbPrime = -truckAdjacencyMatrix[bjindex][jindex] - truckAdjacencyMatrix[jindex][ajindex] + truckAdjacencyMatrix[bjindex][ajindex];
				
				for(int l = 0; l < tS.getSubroute().size(); l++) {
					if (l+1 < tS.getSubroute().size()) {
						tbPrime += truckAdjacencyMatrix[tS.getSubroute().get(l).getId()][tS.getSubroute().get(l+1).getId()];
					}
				}
				savings = Math.min(savings, tbPrime - (t.get(tS.getSubroute().get(a).getId())) 
						+ UAVadjacencyMatrix[tS.getSubroute().get(a).getId()][jPrime.getId()]
						+ UAVadjacencyMatrix[jPrime.getId()][tS.getSubroute().get(b).getId()]
						+ sr);
			}
		}
		return savings;
	}
}
