package ThreadsSynchronization;

public class LoopSumCal {
    /**
     * Dedicated utility class method that calculates sum using a loop.
     */
    public static int calculateSum(int limit) {
        int sum = 0;
        for (int i = 0; i <= limit; i++) {
            sum += i;
        }
        return sum;
    }
}