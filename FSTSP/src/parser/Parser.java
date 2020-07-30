package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class Parser {
	public BufferedReader buffer; 
	public StreamTokenizer st;
	public Parser(String percorso) throws FileNotFoundException {
		buffer = new BufferedReader(new FileReader(percorso));
		st = new StreamTokenizer(buffer);
		st.ordinaryChar('.');
	}
	
	public double[][] ReadFile() throws IOException{
		double[][] a;
		int dim =0;
		if(st.nextToken()!= StreamTokenizer.TT_EOF) {
			dim = (int) st.nval;
		}
		a = new double[dim+2][dim+2];
		int i =0;
		int j =0;
		double val = 0;
		int stato = 0;
		while (st.nextToken() != StreamTokenizer.TT_EOF) {
			switch (stato) {
				
			case 0:
				
				i = (int) st.nval;
				stato = 1;
				break;
				
			case 1:
				j = (int) st.nval;
				stato = 2;
				break;
				
			case 2:
				val = ((double) st.nval)/20;
				a[i][j] = val;
				stato = 0;
				break;
			}
		}
		
		
		
		return a;
	}
	
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser ("./src/MatriceTruck.txt");
		double a[][] = parser.ReadFile();
		for(int k = 0;k <a.length;k++) {
			for(int l =0;l<a.length;l++) {
				
				System.out.print(a[k][l] + " \t");
				
			}
			System.out.print("\n");
		}
	}
	
}
