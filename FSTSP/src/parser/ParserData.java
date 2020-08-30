package parser;

import java.util.ArrayList;

public class ParserData {
		public double sl;
		public double sr;
		public double e;
		public int speedUav;
		public int speedTruck;
		public int dim;
		public ArrayList<Integer> served;
		public double[][] TruckMatrix;
		public double[][] UAVMatrix;
		
		public ParserData(double sl, double sr, double e, int speedUav, int speedTruck, int dim,
				ArrayList<Integer> served, double[][] truckMatrix, double[][] uAVMatrix) {
			super();
			this.sl = sl;
			this.sr = sr;
			this.e = e;
			this.speedUav = speedUav;
			this.speedTruck = speedTruck;
			this.dim = dim;
			this.served = served;
			TruckMatrix = truckMatrix;
			UAVMatrix = uAVMatrix;
		}
		
		public ParserData() {
			super();
			this.sl = 0.0;
			this.sr = 0.0;
			this.e = 0.0;
			this.speedUav = 0;
			this.speedTruck = 0;
			this.dim = 0;
			this.served = new ArrayList<>();
		}

		public int getSpeedTruck() {
			return speedTruck;
		}

		public void setSpeedTruck(int speedTruck) {
			this.speedTruck = speedTruck;
		}
		
		
		
}

