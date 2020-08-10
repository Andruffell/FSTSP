package fstsp;

import tsp.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;

import parser.Parser;
import parser.ParserData;


public class FSTSPsolver {

	public static String fileName = "M5106.txt";
	
	private static double sr = 0.016667; //tempo di recupero dell'UAV in numero di ore (0.016 = 1 minuto)
	private static double sl = 0.016667; //tempo di lancio dell'UAV in numero di ore (0.016 = 1 minuto)
	private static double droneBattery = 0.666667; //durata della batteria del drone in numero di ore (0.66 = 40 minuti)
	
	/**
	 * Calcola i savings ottenuti rimuovendo un nodo (jNode) dalla route del truck
	 * @param jNode nodo da rimuovere
	 * @param t vettore dei tempi di arrivo del truck in ogni nodo
	 * @param Cprime vettore dei nodi che possono essere serviti dall'UAV
	 * @param truckRoute route del truck
	 * @param truckAdjacencyMatrix matrice di adiacenza con i costi del truck
	 * @param truckSubroutes vettore di tutti i sottopercorsi (tratte) che effettua il truck
	 * @param UAVadjacencyMatrix matrice di adiacenza con i costi dell'UAV
	 * @return il valore del saving
	 */
	public static double calcSavings(Node jNode, ArrayList<Double> t, ArrayList<Node> Cprime, ArrayList<Node> truckRoute,
			double truckAdjacencyMatrix[][], ArrayList<Subroute> truckSubroutes, double UAVadjacencyMatrix[][]) {

		//Prendiamo il nodo precedente e successivo a j
		Node iNode = truckRoute.get(truckRoute.indexOf(jNode)-1);
		Node kNode = truckRoute.get(truckRoute.indexOf(jNode)+1);
		
		//inizializziamo savings sommando i costi per andare da i a j e da j a k e sottraendo il costo di i-k
		double savings = truckAdjacencyMatrix[iNode.getId()][jNode.getId()] + truckAdjacencyMatrix[jNode.getId()][kNode.getId()] - truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
		
		//per ogni subroute del truck
		for (Subroute tS : truckSubroutes) {
			//se contiene il nodo j ed � servita dall'UAV
			if (tS.getNodes().contains(jNode) && tS.isUAVserved()) {
				//Prendiamo il primo e l'ultimo nodo
				Node a = tS.getNodes().get(0);
				Node b = tS.getNodes().get(tS.getNodes().size()-1);
				//Creiamo un nuovo nodo jPrime
				Node jPrime = new Node();
				//Per ogni nodo del tS
				for (Node node : tS.getNodes()) {
					//Se il nodo � servito dall'UAV allora lo salviamo in jPrime
					if (node.isUAVserved()) {
						jPrime = node;
					}
				}
				//Calcolo del tempo di arrivo nel nodo b
				double tbPrime = 0.0;
				tbPrime = t.get(truckRoute.indexOf(b));				
				tbPrime -= truckAdjacencyMatrix[iNode.getId()][jNode.getId()];
				tbPrime -= truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
				tbPrime += truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
				
				//Calcolo del tempo di arrivo del truck in a
				double ta = t.get(truckRoute.indexOf(a));
				double tauPrimeAJPrime = UAVadjacencyMatrix[a.getId()][jPrime.getId()];	//costo dell'UAV tra il nodo a e il nodo jPrime
				double tauPrimeJprimeB = UAVadjacencyMatrix[jPrime.getId()][b.getId()]; //costo dell'UAV tra il nodo jPrime e il nodo b
				//Aggiornamento di savings che pu� essere negativo se il truck deve aspettare l'arrivo dell'UAV
				savings = Math.min(savings, 
						  tbPrime - (ta + tauPrimeAJPrime + tauPrimeJprimeB + sr));
				return savings;
			}
		}
		return savings;
	}
	/**
	 * Calcola il costo dell'inserimento del nodo j in una posizione differente della route del truck
	 * @param jNode Nodo da inserire nella truck route
	 * @param t vettore dei tempi di arrivo
	 * @param subroute tratta da analizzare 
	 * @param truckAdjacencyMatrix matrice di adiacenza del truck
	 * @param savings valore del saving
	 * @param maxSavings valore del massimo saving calcolato in quel momento
	 * @param truckRoute vettore della route del truck
	 * @param nodesToUpdate nodi i*, k*, j* da modificare
	 * @return i nodi da modificare e il risparmio calcolato
	 */
	public static NodesToUpdate calcCostTruck(Node jNode, ArrayList<Double> t, Subroute subroute, double truckAdjacencyMatrix[][], double savings, double maxSavings, ArrayList<Node> truckRoute, NodesToUpdate nodesToUpdate) {
		//Inizializzazione variabili
		NodesToUpdate returnVal = nodesToUpdate;
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		//Salvataggio di primo e ultimo nodo della subroute
		Node aNode = subrouteNodes.get(0);
		Node bNode = subrouteNodes.get(subrouteNodes.size() - 1);
		//per l che va da 0 alla dimensione della subroute - 1
		for(int l = 0; l < subrouteNodes.size() - 1; l++) {
			//salvataggio del nodo l-esimo
			Node iNode = subrouteNodes.get(l);

			//Salvataggio del nodo (l+1)-esimo
			Node kNode = subrouteNodes.get(l+1);
			
			//Calcolo dei costi i-j, j-k e i-k
			double tauIJ = truckAdjacencyMatrix[iNode.getId()][jNode.getId()];
			double tauJK = truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
			double tauIK = truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
			//Calcolo del costo totale
			double cost = tauIJ + tauJK - tauIK;
			//Se il costo � minore del saving
			if(cost < savings) {
				//Salvataggio degli indici del nodo a e del nodo b
				int bTauId = truckRoute.indexOf(bNode);
				int aTauId = truckRoute.indexOf(aNode);
				//Se il tempo di arrivo meno quello di partenza sono minori della durata della batteria
				if((t.get(bTauId) - t.get(aTauId) + cost) <= droneBattery) {
					//Se savings - il costo sono maggiori del max savings
					if(savings - cost > maxSavings) {
						//Salvo i nodi j*, i*, k*
						jNode.setUAVserved(false);
						subroute.setUAVserved(false);
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
	/**
	 * Calcola il costo dell'inserimento del nodo j in una posizione differente della route dell'UAV
	 * @param jNode nodo da inserire
	 * @param t vettore dei tempi di arrivo
	 * @param subroute tratta da analizzare 
	 * @param UAVadjacencyMatrix matrice di adiacenza dell'UAV
	 * @param valore del saving
	 * @param maxSavings valore del massimo saving calcolato in quel momento
	 * @param truckRoute vettore della route del truck
	 * @param truckAdjacencyMatrix matrice di adiacenza del truck
	 * @param nodesToUpdate nodi i*, k*, j* da modificare
	 * @return i nodi da modificare e il risparmio calcolato
	 */
	public static NodesToUpdate calcCostUAV(Node jNode, ArrayList<Double> t, Subroute subroute, double UAVadjacencyMatrix[][], double savings, double maxSavings, ArrayList<Node> truckRoute, double truckAdjacencyMatrix[][], NodesToUpdate nodesToUpdate) {
		//Inizializzazione dele variabili
		NodesToUpdate returnVal = nodesToUpdate;
		ArrayList<Node> subrouteNodes = subroute.getNodes();
		//Se la subroute contiene il nodo j
		if(subrouteNodes.contains(jNode)) {
			//Salvataggio dell'indice del nodo J nella subroute
			int index = subrouteNodes.indexOf(jNode);
			//Per ogni l-esimo nodo che precede j
			for( int l =0;l<index;l++) {
				//Prendo il nodo i di indice l
				Node iNode = subrouteNodes.get(l);
				//Per ogni m-esimo nodo che � successivo a j (a ritroso)
				for(int m = subrouteNodes.size()-1; m > index; m--) {
					//Prendo il nodo k di indice m
					Node kNode = subrouteNodes.get(m);
					//Calcolo i costi i-j e j-k
					double tauprimeIJ = UAVadjacencyMatrix[iNode.getId()][jNode.getId()];
					double tauprimeJK = UAVadjacencyMatrix[jNode.getId()][kNode.getId()];
					//Se la loro somma � minore della durata della batteria del drone
					if(tauprimeIJ + tauprimeJK <= droneBattery  ) {
						//Prendo il tempo di arrivo al nodo k
						double tkPrime = 0;
						tkPrime = t.get(truckRoute.indexOf(kNode));				
						//Calcolo il valore da sottrarre a tkPrime (dato che rimuovo il nodo j)
						double negativeTruck = - truckAdjacencyMatrix[iNode.getId()][jNode.getId()] - truckAdjacencyMatrix[jNode.getId()][kNode.getId()];
						tkPrime +=  negativeTruck + truckAdjacencyMatrix[iNode.getId()][kNode.getId()];
						//Calcolo il costo
						double uavcost = Math.max(tkPrime - t.get(truckRoute.indexOf(iNode))+ sr + sl ,tauprimeIJ + tauprimeJK+ sr + sl  );
						double cost = Math.max(0,uavcost -(tkPrime - t.get(truckRoute.indexOf(iNode))) );
						//Se savings meno il costo � maggiore di max savings
						if ( savings - cost > maxSavings ) {
							//Salvo i nodi j*, i*, k*
							jNode.setUAVserved(true);
							subroute.setUAVserved(true);
							returnVal.jStar = jNode;
							returnVal.iStar = iNode;
							returnVal.kStar = kNode;
							returnVal.maxSavings = savings - cost;
						}
					}		
				}
			}
		}
		return returnVal;
}
	/**
	 * Salva i nodi da aggiornare
	 * @param nodesToUpdate nodi da aggiornare
	 * @param truckRoute route del truck
	 * @param truckSubroutes subroutes del truck
	 * @param cPrime vettore dei nodi che possono essere serviti dall'UAV
	 * @param t vettore dei tempi di arrivo
	 * @param truckAdjacencyMatrix matrice di adiacenza del truck
	 * @return i nodi aggironati
	 */
	public static RouteToUpdate performUpdate(NodesToUpdate nodesToUpdate, ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Node> cPrime, ArrayList<Double> t, double truckAdjacencyMatrix[][]) {
		
		//Inizializzione del valore di ritorno
		RouteToUpdate returnVal = new RouteToUpdate(truckRoute, truckSubroutes, cPrime, new ArrayList<Double>());
		//Eliminazione del nodo j* dalla truckRoute
		truckRoute.remove(nodesToUpdate.jStar);
		
		//Se il nodo j* è servito dall'UAV
		if(nodesToUpdate.jStar.isUAVserved()) {
			//Eliminazione del nodo j* dalle subroute del truck
			for (Subroute sub : returnVal.truckSubroutes) {
				if(sub.getNodes().remove(nodesToUpdate.jStar)) {
					sub.setUAVserved(false);
				}
			}
			
			//Creazione della nuova subroute
			ArrayList<Node> newList = new ArrayList<>();
			int maxIndex = nodesToUpdate.kStar.getId() == 0 ? truckRoute.size()-1 : truckRoute.indexOf(nodesToUpdate.kStar);
			for(int i= truckRoute.indexOf(nodesToUpdate.iStar); i<=maxIndex; i++) {
				newList.add(truckRoute.get(i));
			}
			
			//Separazione delle subroute del truck
			ArrayList<Subroute> newSubroutes = new ArrayList<>();
			Subroute removeSubroute = null;
			for (Subroute sub : returnVal.truckSubroutes) {
				if (sub.getNodes().contains(nodesToUpdate.iStar) && sub.getNodes().contains(nodesToUpdate.kStar)) {
					removeSubroute = sub;
					int iIndex = sub.getNodes().indexOf(nodesToUpdate.iStar);
					ListIterator<Node> liti = sub.getNodes().listIterator(iIndex);
					if (liti.hasPrevious()) {
						ArrayList<Node> startList = new ArrayList<>();
						for (int i = 0; i<=iIndex; i++) {
							startList.add(sub.getNodes().get(i));
						}
						Subroute newSubroute = new Subroute(startList, false);
						newSubroutes.add(newSubroute);
					}

					int kIndex = sub.getNodes().indexOf(nodesToUpdate.kStar);
					ListIterator<Node> litk = sub.getNodes().listIterator(kIndex);

					if (litk.hasNext()) {
						ArrayList<Node> endtList = new ArrayList<>();
						for (int i = kIndex; i<sub.getNodes().size(); i++) {
							endtList.add(sub.getNodes().get(i));
						}
						Subroute newSubroute = new Subroute(endtList, false);
						newSubroutes.add(newSubroute);
					}
				}
			}			
			returnVal.truckSubroutes.addAll(newSubroutes);
			returnVal.truckSubroutes.remove(removeSubroute);
			
			//Aggiunta della nuova subroute servita dall'UAV
			Subroute newSubroute = new Subroute(newList, true);
			returnVal.truckSubroutes.add(newSubroute);
				
			//rimozione dei nodi i*,j*,k* da cPrime
			returnVal.cPrime.remove(nodesToUpdate.iStar);
			returnVal.cPrime.remove(nodesToUpdate.jStar);
			returnVal.cPrime.remove(nodesToUpdate.kStar);
			
		} else {
				//Altrimenti eliminazione del nodo j* dalle subroute del truck
				for (Subroute sub : returnVal.truckSubroutes) {
					sub.getNodes().remove(nodesToUpdate.jStar);
				}
				//Per ogni subroute del truck
				for (Subroute sub : returnVal.truckSubroutes) {
					//Se la subroute contiene i* e k*
					if (sub.getNodes().contains(nodesToUpdate.iStar) && sub.getNodes().contains(nodesToUpdate.kStar)){
						//Aggiunta del nodo j* tra i nodi i* e k*
						sub.getNodes().add(sub.getNodes().indexOf(nodesToUpdate.kStar), nodesToUpdate.jStar);
					}
				}
				//Aggiunta del nodo j* alla truckRoute
				returnVal.truckRoute.add(returnVal.truckRoute.indexOf(nodesToUpdate.kStar), nodesToUpdate.jStar);
			}
		
		//Calcolo dei nuovi tempi di arrivo del truck per ogni nodo della truckRoute 
		returnVal.t.add(0.0);
		for(int l=1; l<returnVal.truckRoute.size(); l++) {
			int previousNodeIndex = returnVal.truckRoute.get(l-1).getId();
			int currentNodeIndex = returnVal.truckRoute.get(l).getId();
			returnVal.t.add(returnVal.t.get(l-1) + truckAdjacencyMatrix[previousNodeIndex][currentNodeIndex]);
		}
		
		return returnVal;
	}
	
	public static void main(String[] args) throws IOException {
		//Lettura dei dati dal file
		ParserData p = new ParserData();
		Parser parser = new Parser("./src/data/" + fileName);
		p = parser.ReadFile();

		long startTSPTime = System.currentTimeMillis();
		//Risoluzione del TSP considerando solo il camion tramite euristica nearest neighbour
		TSPsolver tspNearestNeighbour = new TSPsolver();
		tspNearestNeighbour.tsp(p.TruckMatrix);
		
		long startTime = System.currentTimeMillis();
		//Inizializzazione della truck route e del vettore canBeServed
		ArrayList<Node> truckRoute = new ArrayList<>();
		ArrayList<Integer> cannotBeServed = new ArrayList<>(p.served);
		//Lettura del vettore cannotBeServed
		for (Integer node : tspNearestNeighbour.getList()) {
			boolean served = cannotBeServed.get(node) == 0 ? true : false; 
			truckRoute.add(new Node(node,served,false));	
		}	
		//Popolamento del vettore cPrime
		ArrayList<Node> cPrime = new ArrayList<>();
		for (Node node : truckRoute) {
			if (node.isUAVeligible()) {
				cPrime.add(node);
			}
		}	
		//Inizializzazione truck rubroutes
		ArrayList<Subroute> truckSubroutes = new ArrayList<>();
		truckSubroutes.add(new Subroute(truckRoute));
		//Lettura del vettore dei tempi di arrivo
		ArrayList<Double> t = tspNearestNeighbour.getTempiDiArrivo();
		//Inizializzazione variabili
		double maxSavings = 0;
		boolean stop = false;
		double savings = 0;
		//Inizio risoluzione
		NodesToUpdate nodesToUpdate = new NodesToUpdate();
		do {
			//Per ogni nodo j appartenente a cPrime
			for (Node j : cPrime) {
				//Calcola i savings
				savings = calcSavings(j, t, cPrime, truckRoute, p.TruckMatrix, truckSubroutes, p.UAVMatrix);
				//Per ogni subroute del truck
				for (Subroute subroute : truckSubroutes) {
					//Se � servita dall'UAV
					if (subroute.isUAVserved()) {
						//Calcola quanto risparmierebbe se non lo fosse
						nodesToUpdate = calcCostTruck(j, t, subroute, p.TruckMatrix, savings, maxSavings, truckRoute, nodesToUpdate);
					} else {
						//Altrimenti calcola quanto risparmierebbe se lo fosse
						nodesToUpdate = calcCostUAV(j, t, subroute, p.UAVMatrix, savings, maxSavings, truckRoute, p.TruckMatrix, nodesToUpdate);
					}
					//Aggiorna maxSavings
					maxSavings = nodesToUpdate.maxSavings;
				}
			}
			//Se c'� un risparmio
			if (maxSavings > 0) {
				//Aggirona tutti i dati del problema
				RouteToUpdate newRoutes = performUpdate(nodesToUpdate, truckRoute, truckSubroutes, cPrime, t, p.TruckMatrix);
				truckRoute = newRoutes.truckRoute;
				truckSubroutes = newRoutes.truckSubroutes;
				cPrime = newRoutes.cPrime;
				t = newRoutes.t;
				maxSavings = 0;
				nodesToUpdate = new NodesToUpdate();
			} else {
				//Altrimenti termina l'algoritmo
				stop = true;
			}
		} while (!stop);
		long endTime = System.currentTimeMillis();
		long timeSpanTSP = startTime-startTSPTime;
		long timeSpan = endTime-startTime;
		//Stampa dei risultati
		
		System.out.println("Risoluzione TSP");
		System.out.println("Tempo di esecuzione del TSP: " + timeSpanTSP + "ms");
		System.out.println("Percorso del TSP : " + tspNearestNeighbour.getList());
		System.out.println("Tempi di arrivo : " + tspNearestNeighbour.getTempiDiArrivo());
		System.out.println("\nRisoluzione con UAV");
		System.out.println("Tempo di esecuzione di FSTSP: " + timeSpan + "ms");
		System.out.println("Saving totale : " + (tspNearestNeighbour.getTempiDiArrivo().get(tspNearestNeighbour.getTempiDiArrivo().size()-1) - t.get(t.size()-1))*60 + " minuti");
		stampa(truckRoute, truckSubroutes, t);
	}
	
	/**
	 * Stampa dei risultati
	 * @param truckRoute route del truck
	 * @param truckSubroutes subroute del truck
	 * @param t vettore dei tempi di arrivo
	 */
	public static void stampa(ArrayList<Node> truckRoute, ArrayList<Subroute> truckSubroutes, ArrayList<Double> t) {
		System.out.print("Truck route : [");
		for (Node node : truckRoute) {
			System.out.print(node.getId());
			if(!node.equals(truckRoute.get(truckRoute.size()-1))) {System.out.print(", ");}
		}
		System.out.println("]\nTempi di arrivo : " + t);
		System.out.print("Truck subroutes : ");
		for ( Subroute sb : truckSubroutes) {
			System.out.print("[");
			for (Node nd : sb.getNodes()) {
				System.out.print(nd.getId());
				if(!nd.equals(sb.getNodes().get((sb.getNodes().size()-1)))) {System.out.print(", ");}
			}
			if(!sb.equals(truckSubroutes.get(truckSubroutes.size()-1))) {System.out.print("], ");}
			else {System.out.println("]");}
		}
	}
}
