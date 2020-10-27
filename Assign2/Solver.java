import java.io.*;
import java.util.*;

/** Bid class contains values for each bid, these values
 *  include start lot, end lot and bid for these lots
 * @constructor start, end, amount - integers
 * @methods print() - prints Bid object values
 */
class Bid 
{ 
	int start;
	int end;
	int amount; 
	double ratio;

	Bid(int start, int end, int amount) 
	{ 
		this.start = start; 
		this.end = end; 
		this.amount = amount; 
		this.ratio = (double)amount / (double)((end+1) - start);
	} 
	
	public void print() {
		System.out.print(this.start);
		System.out.print(" " + this.end);
		System.out.print(" " + this.amount);
		System.out.print(" " + this.ratio);
		System.out.println();
	}
}



// Comparator to sort bids according to end time
class BidEndComparator implements Comparator<Bid> { 
	public int compare(Bid a, Bid b) { 
		if (a.end < b.end) {
			return -1;
		}
		else if (a.end == b.end) {
			return 0;
		}
		else {
			return 1;
		}
	} 
} 

public class Solver {
	
	/**
	 * You can use this to test your program without running the jUnit test,
	 * and you can use your own input file. You can of course also make your
	 * own tests and add it to the jUnit tests.
	 */
	public static void main(String[] args){
		String PATH = "src/data/test_case_03.in";
		
		Solver m = new Solver();
		int answer = m.solve(PATH);
		System.out.println("Final answer is: " + answer);
	}
	
	/** The solve method accepts a String containing the 
	 * path to the input file for the problem (as described
	 * in the assignment specification) and returns an integer
	 * denoting the maximum income 
	 * 
	 * @param infile the file containing the input
	 * @return maximum income for this set of input
	 */
	public int solve(String infile) {
		ArrayList<Bid> allBids = new ArrayList<Bid>();
		try {
			// Read data into allBids array
			allBids = readData(infile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		// Sort allBids based on end value
		Collections.sort(allBids, new BidEndComparator()); 
		
//		for (Bid b : allBids) {
//			b.print();
//		}
//		System.out.println();
		
		int answer = findMaxAmount(allBids);
		
//		System.out.println();
//		for (Bid b : allBids) {
//			b.print();
//		}
		
		return answer;
	}
	
	/** This ia a recursive binarySearch method will search for 
	 * the specified index recursively
	 * 
	 * @param bids, lowerBound, upperBound, index
	 * @return int mid containing index value
	 */
	static int binarySearch(ArrayList<Bid> allBids, int lowerBound, int upperBound, int index) {
		// Recursive Binary Search
        if (upperBound >= lowerBound) { 
        	
        	int mid = (lowerBound + upperBound) / 2; 
        	
        	//end of middle and middle+1 is less than start
        	if (allBids.get(mid).end < allBids.get(index).start) { 
        		if (allBids.get(mid + 1).end < allBids.get(index).start) {
        			// Keep searching right side
        			return binarySearch(allBids, mid + 1, upperBound, index); 
        		}
        		else {
        			// Index found at middle
        			return mid; 
        		}
        	} 
        	else {
        		// Keep searching left side
        		return binarySearch(allBids, lowerBound, mid - 1, index); 
        	}
        	
        } 
        
        // Element is not found
        return -1; 
    } 

	/** The findMaxAmount method accepts a ArrayList of Bids 
	 * containing the start, end, amount values for this bid
	 * 
	 * @param ArrayList of bid(start, end, amount) objects
	 * @return maximum income for this set of input
	 */
	static public int findMaxAmount(ArrayList<Bid> allBids) {
		int size = allBids.size(); 
		
		int subProblems[] = new int[size]; 
		subProblems[0] = allBids.get(0).amount; 

		// Find sub-problems
		for (int i=1; i < allBids.size(); i++) { 
			// Set current amount
			int amount = allBids.get(i).amount; 
			
			// Search current bid amount 
			int index = binarySearch(allBids, 0, size-1, i); 
			
			if (index != -1) {
				// add amount to current subProblem
				amount += subProblems[index]; 
			}

			// Store max amount in subProblems
			subProblems[i] = Math.max(amount, subProblems[i-1]); 
		} 
		
		// return highest subProblem
		return subProblems[size-1]; 
	} 

	/**
	 * The readData method accepts a String containing the 
	 * path to the input file for the problem.
	 * Please see the assignment specification for more information
	 * on the input format.
	 * 
	 * You should use this method to populate this class with 
	 * the information that you need to solve the problem.
	 * 
	 * @param infile the input file containing the problem
	 * @throws Exception if file is not found or if there is an input reading error
	 * @return ArrayList of bid(start, end, amount) objects
	 */
   	public ArrayList<Bid> readData(String infile) throws Exception {
   		Scanner in = new Scanner(new FileReader(infile));
   		

   		// read number of lots and number of bids
   		// create integers lots and bids

   		int lots = in.nextInt();
   		int bids = in.nextInt();
   		
   		// read bid values
   		// create object bid(start, end, amount)
   		
   		ArrayList<Bid> allBids = new ArrayList<Bid>();
   		
   		for (int i = 0; i < bids; i++) {
   			in.nextLine();
   			in.nextInt();
   			int start = in.nextInt();
   			int end = in.nextInt();
   			int amount = in.nextInt();
   			
   			Bid bid = new Bid(start, end, amount);
   			allBids.add(bid);
   		}
   		
   		// return array of all bids
   		return allBids;
   	}


}
