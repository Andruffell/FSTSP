package tsp;
import java.util.ArrayList;
import java.util.Stack;

public class TSPsolver {
	
	
    private ArrayList<Integer> list;
    private Integer costo;
    public TSPsolver()
    {
        costo = 0;
        list = new ArrayList<Integer>();
    }
    
    public ArrayList<Integer> getList(){return list;}
    public Integer getCosto() {return costo;}
    /** 
     * Questa funzione utilizza un'euristica Greedy di tipo nearest neighbour. 
     * @param adjacencyMatrix che è una matrice quadrata di interi, simmetrica con diagonale principale nulla, la quale rappresenta
     * la matrice di adiacenza di un grafo non orientato.
     */
    public void tsp(int adjacencyMatrix[][])
    { 
    	
        Stack<Integer> stack = new Stack<Integer>();
        int numberOfNodes = adjacencyMatrix[1].length;
        int[] visited = new int[numberOfNodes];
        visited[0] = 1;
        stack.push(0);
        int element, dst = 0, i;
        int min = Integer.MAX_VALUE;
        boolean minFlag = false;
        list.add(0);
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
                costo = costo + min;               
                list.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        costo += adjacencyMatrix[list.get(list.size()-1)][0];
        list.add(0);
        
    }
    
    
    
	public static void main(String[] args) {
        	/*int adjacency_matrix[][] = {{0,374,200,223,108,178,252,285,240,356},
        								{374,0,255,166,433,199,135,95,136,17},
        								{200,255,0,128,277,128,180,160,131,247},
        								{223,166,128,0,430,47,52,84,40,155},
        								{108, 433, 277, 430, 0, 453, 478, 344, 389, 423},
        								{178, 199, 128, 47, 453, 0, 91, 110, 64, 181},
        								{252, 135, 180, 52, 478, 91, 0, 114, 83, 117},
        								{285, 95, 160, 84, 344, 110, 114, 0, 47, 78},
        								{240, 136, 131, 40, 389, 64, 83, 47, 0, 118},
        								{356, 17, 247, 155, 423, 181, 117, 78, 118, 0}};*/
        								
        	/*int adjacency_matrix[][] = {{0, 10, 15, 20}, 

                    					{10, 0, 35, 25}, 

                    					{15, 35, 0, 30}, 

                    					{20, 25, 30, 0}};*/
        	
		int adjacency_matrix[][] = {{0,5,15,5,7},
        							 {5,0,5,2,3},
        							 {15,5,0,4,1},
        							 {5,2,4,0,3},
        							 {7,3,1,3,0}};
        	
           
            System.out.println("the citys are visited as follows");
            TSPsolver tspNearestNeighbour = new TSPsolver();
            tspNearestNeighbour.tsp(adjacency_matrix);
            for (Integer is : tspNearestNeighbour.getList()) {
				System.out.print(is + "\t");
			}
            System.out.print("\n");
            System.out.print("costo :"+ tspNearestNeighbour.getCosto());
    }

}
