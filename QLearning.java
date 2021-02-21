import java.util.Random;

public class QLearning {
	
	private final int STATE_TOTAL_NUM = 17;//16 normal states + Broken State 
	private final String[] actions = {"N", "S", "E", "W", "R"};
	private final int MAX_TRIALS = 100000;
	private double[][] Q;
	private double alpha = 0.01;
	private double gamma = 0.95;
	
	public QLearning() 
	{
		Random rand = new Random();
		double explorationChance = 1;
		
		Q = new double[STATE_TOTAL_NUM][actions.length];
		int trials = 0;
		int iterations = 0;
		String action = "BEGIN";
		boolean learning = true;
		
		Simulator simulator = new Simulator();
		
		int currState = -1;
		while (learning) {
			//First round with BEGIN will be gabaged
			simulator.commit(action);
			int nextState = simulator.currState;
			double reward = simulator.reward;
			int index = -1;
			if (actions[0].equals(action)) {
				index = 0;
			}else if(actions[1].equals(action)) {
				index = 1;
			}else if (actions[2].equals(action)) {
				index = 2;
			}else if(actions[3].equals(action)) {
				index = 3;}
			else if(actions[4].equals(action)) {
				index = 4;}
			else {}
			//Update q-values
			if (index != -1) {
				double q = Q[currState][index];
				
				int bestIndex = -1;
				double maxQ = -1;
				for (int i = 0; i < actions.length; i++) {
					if (Q[nextState][i] > maxQ) {
						bestIndex = i;
						maxQ = Q[nextState][i];
					}
					else if (Q[nextState][i] == maxQ) {
						if (Math.random()>0.5) {
							bestIndex = i;
						}else {}
					}else {}
				}
				
				double qPrime = Q[nextState][bestIndex];
				Q[currState][index] = q + alpha * (reward + gamma * qPrime - q);
			}
			if (trials == MAX_TRIALS) {
				learning = false;
			}
			else {
			}
			//Do exploration
			if (Math.random() < explorationChance) {
				int randomIndex= rand.nextInt(actions.length);
				action = actions[randomIndex];
			}
			//Do exploitation
			else 
			{
				int bestIndex = -1;
				double maxQ = -1;
				for (int i = 0; i < actions.length; i++) {
					if (Q[nextState][i] > maxQ) {
						bestIndex = i;
						maxQ = Q[nextState][i];
					}
					else if (Q[nextState][i] == maxQ) {
						if (Math.random()>0.5) {
							bestIndex = i;
						}else {}
					}else {}
				}
				action = actions[bestIndex];
			}
			//When it is done.
			if (!learning) {
				action = "TERMINAL";
			}
			else if(nextState==16) {
				action = "RESET";
				iterations =0;
				trials++;
				explorationChance = Math.pow(trials,-0.1);
			}
			else if (iterations >= 17) {
				action = "RESET";
				iterations = 0;
				trials++;
				explorationChance = Math.pow(trials, -0.1);	
			}
			iterations++;
			currState = nextState;
		}
		 
		System.out.println("Policy action.\n");
		for (int i = 0; i < STATE_TOTAL_NUM - 1; i++) {
			
			int bestIndex = -1;
			double maxQ = -1;
			for (int j = 0; j < actions.length; j++) {
				if (Q[i][j] > maxQ) {
					bestIndex = j;
					maxQ = Q[i][j];
				}
				else if (Q[i][j] == maxQ) {
					if (Math.random()<0.5) {
						bestIndex = j;
					}else {}
				}else {}
			}
			System.out.println("State\t" + i + "\t" + actions[bestIndex] + "\t"+String.format("%.4f", Q[i][bestIndex]));
		}
		System.out.println("Q-value.");
		for (int i = 0; i < STATE_TOTAL_NUM - 1; i++) {
			System.out.print("State\t" + i);
			for (int j = 0; j < actions.length; j++) {
				System.out.print("\t(" + actions[j] + "," + String.format("%.4f", Q[i][j]) + ")");
			}
			System.out.print("\n");
		}
		
		
	}
	
	
	
	private class Simulator {
		//Map
		public final char[] states = {'R', 'F', 'F', 'C', 'R', 'F', 'R', 'R',
				'F', 'C', 'F', 'F', 'F', 'R', 'F', 'R', 'B'};
		//Move directions
		private final int[][] diagonalMoves = {{0, +1}, {0, -1}, {+1, 0}, {-1, 0}, {0, 0}};
		private final double probOnRock = 1;
		//Current State and reward
		public int currState;
		public double reward;
		
		public Simulator() {
			commit("RESET");
		}
		
		public void commit(String action) {
			if (action.equals("RESET")) {
				Random rand = new Random();
				currState = rand.nextInt(16);
				reward = getReward(currState);
			}
			//When state is broken)
			else if (currState == 16 || action.equals("TERMINAL")) {
				reward = 0;
			}
			// When action is remain
			else if (action.equals("R")) {
				reward = getReward(currState);
			}
			//Otherwise
			else {
				int i = -1;
				if (actions[0].equals(action)) {
					i = 0;
				}else if(actions[1].equals(action)) {
					i = 1;
				}else if (actions[2].equals(action)) {
					i = 2;
				}else if(actions[3].equals(action)) {
					i = 3;}
				else if(actions[4].equals(action)) {
					i = 4;}
				else {}
				if (i >= 0 && i <= 3) {
					if(states[currState]=='R') {
						//Fail to move out the rock
						if(Math.random()>=probOnRock) {
							currState = 16;
							reward =0;
						}
						//Success to move out from the rock
						else {
							//Get the dimension
							int x = currState % 4 + diagonalMoves[i][0];
							int y = currState / 4 + diagonalMoves[i][1];
							//Not move off the edge
							if (x >= 0 && x < 4 && y >= 0 && y < 4) {
								currState = x + 4 * y;
								reward = getReward(currState);
							}
							// Move off the edge=> borken
							else {
								currState=16;
								reward = 0;
							}
						}
					}
					//Try to move on Crator-> broken
					else if(states[currState]=='C') 
					{
						currState = 16;
						reward =0;
					}
					//Other flat
					else 
					{
						int x = currState % 4 + diagonalMoves[i][0];
						int y = currState / 4 + diagonalMoves[i][1];
						if (x >= 0 && x < 4 && y >= 0 && y < 4) {
							currState = x + 4 * y;
							reward = getReward(currState);
						}
						else {
							currState = 16;
							reward = 0;
						}
					}
				}
			}
		}
		
		private double getReward(int currState) {
			if (states[currState] == 'F'||states[currState]=='B') {
				return 0;
			}
			else if (states[currState] == 'R') {
				return 1;
			}
			else if (states[currState] == 'C') {
				return 6;
			}
			else {
				return (Double) null;
			}
		}
	}
	
	public static void main(String[] args) {
		new QLearning();
	}
	
}
