package part_d;

import java.util.concurrent.Semaphore; // Import Semaphore tool for tracking thread orders

public class main {
    // Shared state variables updated by the threads
    private static int A1, A2, A3; 
    private static int B1, B2, B3; 

    // Semaphores initialized with 0 permits for event coordination
    private static Semaphore a1Done; 
    private static Semaphore a2Done; 
    private static Semaphore b2Done; 
    private static Semaphore b3Done; 

    public static void main(String[] args) {
        int totalRuns = 5000; // Counter for stress testing execution safety
        boolean isCorrect = true; // tracking flag to see if calculations match up

        System.out.println("Running " + totalRuns + " iterations to verify thread execution..."); // Log testing start
        
        int runIndex = 1; // start counter at 1 for the loop
        while (runIndex <= totalRuns) { // keep looping to check for any weird thread timing bugs
            // wipe the values clean before each run
            A1 = 0; A2 = 0; A3 = 0; 
            B1 = 0; B2 = 0; B3 = 0; 

            // Re-initialize semaphores to reset permit counts to 0
            a1Done = new Semaphore(0); 
            b2Done = new Semaphore(0);
            a2Done = new Semaphore(0); 
            b3Done = new Semaphore(0); 
            
            // Thread A execution sequence setup
            Thread threadA = new Thread(() -> { // Open clean lambda thread expression block
                try { // Open safety tracking catch block
                	
                    A1 = LoopSumCal.calculateSum(500); // Do sum execution for baseline value A1
                    
                    a1Done.release(); // Increment semaphore to kick off thread B block
                   
                    b2Done.acquire(); // wait here until b2Done gets hit
                    
                    A2 = B2 + LoopSumCal.calculateSum(300); // Run compound A2 value tracking code
                    
                    a2Done.release(); // Increment semaphore to unlock thread B next step

                    b3Done.acquire(); // wait here until b3Done gets hit
                    
                    A3 = B3 + LoopSumCal.calculateSum(400); // Calculate final value block entry for thread A

                } catch (InterruptedException e) { 
                    Thread.currentThread().interrupt(); 
                } 
            }); 

            
            Thread threadB = new Thread(() -> { // Open clean lambda thread expression block
                try { // Open safety tracking catch block
                    B1 = LoopSumCal.calculateSum(250); // Do sum execution for baseline value B1

                    a1Done.acquire(); //wait here until a1Done gets hit
                    B2 = A1 + LoopSumCal.calculateSum(200); // Run compound B2 calculation 
                    
                    b2Done.release(); // Increment semaphore to pass token back to thread A
                    
                    a2Done.acquire(); // wait here until a2Done gets hit
                    
                    B3 = A2 + LoopSumCal.calculateSum(400); // Run compound B3 calculation logic line
                    
                    b3Done.release(); // Increment semaphore to pass token to thread A last step
                } catch (InterruptedException e) { // Catch unexpected system interruptions
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
            } catch (InterruptedException e) { // Handle main breakdown fault scenario
                System.err.println("Main thread execution was interrupted."); // Output console fault message
                return; 
            } 
            boolean match = (A1 == 125250 && B1 == 31375 && B2 == 145350 &&  A2 == 190500 && B3 == 270700 && A3 == 350900); // Boolean validation equality check

            if (!match) { // Conditional logic entry if verification data mismatch occurs
                System.out.println("Verification failed at run #" + runIndex); 
                isCorrect = false; // Flag overall tracking state status as failed
                break; // Break execution run loops instantly to preserve current failed variables
            } 
            
            runIndex++; // Move validation run count by one
        } 

        if (isCorrect) { // Final execution evaluations assessment conditional check
            System.out.println("A1 = " + A1 + ", Calculated Value = 125250");
            System.out.println("B1 = " + B1 + ", Calculated Value = 31375" ); 
            System.out.println("B2 = " + B2 + ", Calculated Value = 145350"); 
            System.out.println("A2 = " + A2 + ", Calculated Value = 190500");
            System.out.println("B3 = " + B3 + ", Calculated Value = 270700"); 
            System.out.println("A3 = " + A3 + ", Calculated Value = 350900"); 
            System.out.println("SUCCESS: Thread execution order correct over " + totalRuns + " iterations!"); // Log successful stress test result
        } 
    } 
}