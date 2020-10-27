import java.io.FileReader;
import java.util.Scanner;

public class Solver {
	
	// Stores the number of nodes 
	int numNodes; 
	
	// residual graph
	// Stores the capacity of each edge 
	static int capacity[][]; 

	// residual graph
	// Stores the cost per flow of each edge 
	static int cost[][]; 

	// flow network
	static int flow[][]; 

	// Stores the distance from each node 
	int distance[];
	// Chosen edge at each node
	int nodeEdge[]; 
	//
	int srcVal[]; 
	
	// Stores the marked routes / edges 
	boolean marked[]; 
	
	/**
	 * You can use this to test your program without running the jUnit test,
	 * and you can use your own input file. You can of course also make your
	 * own tests and add it to the jUnit tests.
	 */
	public static void main(String[] args) {
		Solver m = new Solver();
		int[] answer = m.solve_1("C:/Users/Chaz/eclipse-workspace/assign2/data/p3_05.in");
		
		// Print matrix for current test
		System.out.println('\n' + "flow matrix");
		printMatrix(flow);
		
		System.out.println('\n' + "cost matrix");
		printMatrix(cost);
		
		System.out.println('\n' + "capacity matrix");
		printMatrix(capacity);
		
		// Print final result for current test
		System.out.println("\n" + answer[0] + " " + answer[1]);
	}
	
	// Method to display a 2D array matrix for testing
	static void printMatrix(int[][] a) {
		int len = a.length;
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
	}

	// Modified Ford Fulkerson Algorithm
	// Function to obtain the maximum network Flow 
	int[] maximumFlow(int warehouseNode, int disasterNode) 
	{ 
		int max_flow = 0;
		int max_cost = 0; 
		
		// Assign variables
		flow = new int[numNodes][numNodes]; 
		distance = new int[numNodes + 1]; 
		srcVal = new int[numNodes]; 
		marked = new boolean[numNodes]; 
		nodeEdge = new int[numNodes]; 

		// If a path exist from src to sink 
		while (pathExists(warehouseNode, disasterNode)) { 
        	//when we get here it means path still exists from source to destination
			
			// Set the default max path flow
			int path_flow = Integer.MAX_VALUE; 
			// find maximum flow of path filled by current bfs
			for (int i = disasterNode; i != warehouseNode; i = srcVal[i]) {
				
				int f = 0;
				
				if (flow[i][srcVal[i]] != 0) {
					f = flow[i][srcVal[i]];
				} 
				else {
					f = capacity[srcVal[i]][i] - flow[srcVal[i]][i];
				}
				
				path_flow = Math.min(path_flow, f); 
			}
			
            // update the residual matrii capacities
            // reverse edges along the path
            // reduce the capacity on fwd edge by path_flow
            // add the capacity on back edge by path_flow

			for (int i = disasterNode; i != warehouseNode; i = srcVal[i]) { 

				if (flow[i][srcVal[i]] != 0) { 
					flow[i][srcVal[i]] -= path_flow; 
					max_cost -= path_flow * cost[i][srcVal[i]]; 
				} 
				else { 
					flow[srcVal[i]][i] += path_flow; 
					max_cost += path_flow * cost[srcVal[i]][i]; 
				} 
			} 
			// add the flow of current path to maximum flow
			max_flow += path_flow; 
		} 
		
		int[] result = { max_flow, max_cost };

		// Return pair total cost and sink 
		return result; 
	} 
	
	// function to check if it is possible 
	// to have a flow from warehouseNode to disasterNode using modified bellman ford
	boolean pathExists(int warehouseNode, int disasterNode) 
	{ 
		// Unmark all
		for (int i = 0; i < marked.length; i++) {
			marked[i] = false;
		}
		
		// Reset all distance to max
		for (int i = 0; i < distance.length; i++) {
			distance[i] = Integer.MAX_VALUE;
		}

		// Distance from the warehouseNode
		distance[warehouseNode] = 0; 

		// Iterate untill warehouseNode reaches numNodes 
		while (warehouseNode != numNodes) { 

			int best = numNodes; 
			marked[warehouseNode] = true; 

			for (int i = 0; i < numNodes; i++) { 

				// If already found 
				if (marked[i]) {
					continue;  // go to next iteration
				}

				// Evaluate while flow 
				// is still in supply 
				if (flow[i][warehouseNode] != 0) { 

					// Obtain the total value 
					int val = distance[warehouseNode] + nodeEdge[warehouseNode] 
						- nodeEdge[i] - cost[i][warehouseNode]; 

					// If distance[i] is > minimum value 
					if (distance[i] > val) { 

						// Update new distance values
						distance[i] = val; 
						srcVal[i] = warehouseNode; 
					} 
				} 

				if (flow[warehouseNode][i] < capacity[warehouseNode][i]) { 

					int val = distance[warehouseNode] + nodeEdge[warehouseNode] 
							- nodeEdge[i] + cost[warehouseNode][i]; 

					// If dist[i] is > minimum value 
					if (distance[i] > val) { 

						// Update new distance values
						distance[i] = val; 
						srcVal[i] = warehouseNode; 
					} 
				} 

				if (distance[i] < distance[best]) {
					best = i; 
				}
			} 

			// Update src to best for 
			// next iteration 
			warehouseNode = best; 
		} 

		for (int i = 0; i < numNodes; i++)  {
			nodeEdge[i] = Math.min(nodeEdge[i] + distance[i], Integer.MAX_VALUE); 
		}

		// Return the value obtained at sink 
		return marked[disasterNode]; 
	} 
	
	/** The solve_1 method accepts a String containing the path to the
	 * input file containing the transportation network as described 
	 * in the assignment. 
	 * 
	 * For this problem you are only required to find the cheapest cost
	 * of sending one item from the source vertex to the destination 
	 * vertex, so you only need to return one value. 
	 * 
	 * @param 	infile the file containing the input
	 * @return	an array [1,x] where x is the cheapest cost of sending
	 *          a package from the source vertex to the destination vertex
	 */
	
	public int[] solve_1(String infile) {
		try {
			readData(infile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Node where warehouse is located
		int warehouseNode = 0;
		// Node where disaster zone is located
		int disasterNode = numNodes-1;
		
		int[] answer = maximumFlow(warehouseNode, disasterNode); 
		return answer;
	}

	/** The solve_2 method accepts a String containing the path to the
	 * input file containing the transportation network as described 
	 * in the assignment. 
	 * 
	 * For this problem you are only required to find the maximum flow
	 * in the transportation network, so again, you only need to return
	 * one value, and since the cost for each edge is going to be 0,
	 * the cost of the transfer should just be 0
	 * 
	 * 
	 * @param 	infile the file containing the input
	 * @return	an array [x,0] where x is the maximum flow in the network
	 */
	
	public int[] solve_2(String infile) {
		try {
			readData(infile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Node where warehouse is located
		int warehouseNode = 0;
		// Node where disaster zone is located
		int disasterNode = numNodes-1;
		
		int[] answer = maximumFlow(warehouseNode, disasterNode); 
		return answer;
	}

	/** The solve_3 method solves the full problem, that is, if there
	 * are several maximum flows, we choose the one that is cheapest
	 * (as described in the assignment specification), so here you 
	 * must return both the maximum flow and the cost of the flow
	 * 
	 * @param 	infile the file containing the input
	 * @return	an array [x,y] where x is the maximum flow in the network
	 *          and y is the cost of flow
	 */
	public int[] solve_3(String infile) {
		try {
			readData(infile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Node where warehouse is located
		int warehouseNode = 0;
		// Node where disaster zone is located
		int disasterNode = numNodes-1;
		
		int[] answer = maximumFlow(warehouseNode, disasterNode); 
		return answer;
	}


	/**
	 * The standard readData method that we have been using in the 
	 * previous assignments.
	 * 
	 * Feel free to write a different readData method if you need
	 * a different one for your different solve methods
	 * 
	 * @param infile the input file containing the problem
	 * @throws Exception if file is not found or if there is an input reading error
	 */
   	public void readData(String infile) throws Exception {
   		Scanner in = new Scanner(new FileReader(infile));
   		
   		int v = in.nextInt(); numNodes = v;
   		int e = in.nextInt();
   		
   	    capacity = new int[v][e];
   	    cost = new int[v][e];
   		
   		while (in.hasNext()) {
   			in.nextLine();
   			
   	   	    int i = in.nextInt();
   	   	    int j = in.nextInt();
   	   	    
   	   	    System.out.println(i + " " + j);
   	   	    
   	   	    capacity[i][j] = in.nextInt();
   	   	    cost[i][j] = in.nextInt();
   		}
   		in.close();
   	}
}
