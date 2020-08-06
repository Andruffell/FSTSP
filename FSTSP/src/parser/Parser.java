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
	public ParserData ReadFile() throws IOException {
		ParserData a = new ParserData();
		for(int i=0; i<=5;i++) {
			if(st.nextToken()!= StreamTokenizer.TT_EOF) {
				switch (i) {
				case 0:
					a.sl = (double) st.nval;
					break;
				case 1:
					a.sr = (double) st.nval;
					break;
				case 2:
					a.e = (double) st.nval;
					break;
				case 3:
					a.speedUav = (int) st.nval;
					break;
				case 4:
					a.speedTruck = (int) st.nval;
					break;
				case 5:
					a.dim = (int) st.nval;
					break;
				}
			}
		}
		st.nextToken();
		st.nextToken();
		a.served.add(0);
		for(int i=0; i<a.dim;i++) {
			st.nextToken();
			st.nextToken();
			st.nextToken();
			a.served.add((int) st.nval);
			}
		st.nextToken();
		st.nextToken();
		a.served.add(0);
		
		a.TruckMatrix = new double[a.dim+1][a.dim+1];
		for(int i=0;i<=a.dim+1;i++) {
			for(int j=0;j<=a.dim+1;j++) {
				st.nextToken();
				st.nextToken();
				st.nextToken();
				if(i<a.dim+1 && j<a.dim+1) {
					a.TruckMatrix[i][j] = st.nval/a.speedTruck;
				}
			}
			
		}
		a.UAVMatrix = new double[a.dim+1][a.dim+1];
		for(int i=0;i<=a.dim+1;i++) {
			for(int j=0;j<=a.dim+1;j++) {
				st.nextToken();
				st.nextToken();
				st.nextToken();
				if(i<a.dim+1 && j<a.dim+1) {
					a.UAVMatrix[i][j] = st.nval/a.speedUav;
				}
			}
			
		}
		return a;
	}
	
	public static void main(String[] args) throws IOException {
		Parser parser = new Parser ("./src/M4847.txt");
		ParserData d = parser.ReadFile();
		for(int k = 0;k <d.dim;k++) {
			for(int l =0;l<d.dim;l++) {
				
				System.out.print(d.TruckMatrix[k][l] + " \t");
			}
			System.out.print("\n");
		}
		for(int k = 0;k <d.dim;k++) {
			for(int l =0;l<d.dim;l++) {
				
				System.out.print(d.UAVMatrix[k][l] + " \t");
			}
			System.out.print("\n");
		}
	}
	
}
