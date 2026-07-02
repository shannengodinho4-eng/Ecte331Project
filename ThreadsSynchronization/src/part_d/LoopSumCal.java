package part_d;

public class LoopSumCal { // Utility class for counting sums

    public static int calculateSum(int limit) { // Method to loop and add numbers
    	// Accumulator for the final total
        int sum = 0; // Running total variable
        int i = 0;   // Loop counter
        
        // Loop up to the limit number
        while (i <= limit) {
            sum += i; 
            i++;   
        }
     // Send back the final total
        return sum;
    }
}