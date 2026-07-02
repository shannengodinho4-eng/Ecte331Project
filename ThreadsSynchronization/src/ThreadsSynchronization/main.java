package ThreadsSynchronization;

import java.util.concurrent.Semaphore;

public class main {
    // Shared state variables updated by the threads
    private static int A1, A2, A3;
    private static int B1, B2, B3;

    // Semaphores initialized with 0 permits for event coordination
    private static Semaphore a1Done;
    private static Semaphore b2Done;
    private static Semaphore a2Done;
    private static Semaphore b3Done;

    public static void main(String[] args) {
        int totalRuns = 5000; // Stress test with high iterations (Part d)
        boolean isCorrect = true;

        System.out.println("Running 5000 iterations to verify thread " + totalRuns + " iterations...");

        for (int run = 1; run <= totalRuns; run++) {
            // Reset shared state variables for clean verification
            A1 = A2 = A3 = 0;
            B1 = B2 = B3 = 0;

            // Re-initialize semaphores to reset permit counts to 0
            a1Done = new Semaphore(0);
            b2Done = new Semaphore(0);
            a2Done = new Semaphore(0);
            b3Done = new Semaphore(0);

            // Thread A execution sequence
            Thread threadA = new Thread(() -> {
                try {
                    // FuncA1
                    A1 = LoopSumCal.calculateSum(500);
                    a1Done.release(); // Signal Thread B that A1 is ready

                    // FuncA2
                    b2Done.acquire(); // Block until B2 is ready
                    A2 = B2 + LoopSumCal.calculateSum(300);
                    a2Done.release(); // Signal Thread B that A2 is ready

                    // FuncA3
                    b3Done.acquire(); // Block until B3 is ready
                    A3 = B3 + LoopSumCal.calculateSum(400);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Thread B execution sequence
            Thread threadB = new Thread(() -> {
                try {
                    // FuncB1
                    B1 = LoopSumCal.calculateSum(250);

                    // FuncB2
                    a1Done.acquire(); // Block until A1 is ready
                    B2 = A1 + LoopSumCal.calculateSum(200);
                    b2Done.release(); // Signal Thread A that B2 is ready

                    // FuncB3
                    a2Done.acquire(); // Block until A2 is ready
                    B3 = A2 + LoopSumCal.calculateSum(400);
                    b3Done.release(); // Signal Thread A that B3 is ready

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            // Launch execution
            threadA.start();
            threadB.start();

            // Wait for both threads to finish
            try {
                threadA.join();
                threadB.join();
            } catch (InterruptedException e) {
                System.err.println("Main thread execution was interrupted.");
                return;
            }

            // Verify the computed mathematical answers
            boolean match = (A1 == 125250 && B1 == 31375 && B2 == 145350 && 
                             A2 == 190500 && B3 == 270700 && A3 == 350900);

            if (!match) {
                System.out.println("Verification failed at run #" + run);
                isCorrect = false;
                break;
            }
        }

        if (isCorrect) {
            System.out.println("A1 = " + A1 + ", Calculated Value = 125250");
            System.out.println("B1 = " + B1 + ", Calculated Value = 31375");
            System.out.println("B2 = " + B2 + ", Calculated Value = 145350");
            System.out.println("A2 = " + A2 + ", Calculated Value = 190500");
            System.out.println("B3 = " + B3 + ", Calculated Value = 270700");
            System.out.println("A3 = " + A3 + ", Calculated Value = 350900");
            System.out.println("SUCCESS: Thread execution order correct over " + totalRuns + " iterations!");
        }
    }
}