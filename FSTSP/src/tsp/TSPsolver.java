package tsp;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import parser.Parser;
import parser.ParserData;

public class TSPsolver {
	
    private ArrayList<Integer> list;
    private ArrayList<Double> tempiDiArrivo;
    
    public TSPsolver()
    {
        tempiDiArrivo = new ArrayList<Double>();
        list = new ArrayList<Integer>();
    }
    
    public ArrayList<Integer> getList(){return list;}
    public ArrayList<Double> getTempiDiArrivo() {return tempiDiArrivo;}
    /** 
     * Questa funzione utilizza un'euristica Greedy di tipo nearest neighbour. 
     * @param adjacencyMatrix che � una matrice quadrata di interi, simmetrica con diagonale principale nulla, la quale rappresenta
     * la matrice di adiacenza di un grafo non orientato.
     */
    public void tsp(double adjacencyMatrix[][])
    { 
    	
        Stack<Integer> stack = new Stack<Integer>();
        int numberOfNodes = adjacencyMatrix[1].length;
        int[] visited = new int[numberOfNodes];
        visited[0] = 1;
        stack.push(0);
        int element, dst = 0, i;
        double min = Integer.MAX_VALUE;
        boolean minFlag = false;
        list.add(0);
        tempiDiArrivo.add(0.0);
        while (!stack.isEmpty())
        {
            element = stack.peek();
            i = 0;
            min = Integer.MAX_VALUE;
            while (i < numberOfNodes)
            {
                if (adjacencyMatrix[element][i] > 0 && visited[i] == 0)
                {
                    if (min > adjacencyMatrix[element][i])
                    {
                        min = adjacencyMatrix[element][i];
                        dst = i;
                        minFlag = true;
                        
                    }
                }
                
                i++;
            }
            
            if (minFlag)
            {
                visited[dst] = 1;
                stack.push(dst);
                tempiDiArrivo.add(tempiDiArrivo.get(tempiDiArrivo.size()-1)+min);             
                list.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        tempiDiArrivo.add(tempiDiArrivo.get(tempiDiArrivo.size()-1) + adjacencyMatrix[list.get(list.size()-1)][0]);
        list.add(0);  
    }
        
    
	public static void main(String[] args) throws IOException {
			ParserData p = new ParserData();
			Parser parser = new Parser("./src/M4847.txt");
			p = parser.ReadFile();
        	
           
            TSPsolver tspNearestNeighbour = new TSPsolver();
            tspNearestNeighbour.tsp(p.TruckMatrix);
            System.out.println("nodi attraversati: " + tspNearestNeighbour.getList());
            System.out.println("costo: "+ tspNearestNeighbour.getTempiDiArrivo());
    }

}
