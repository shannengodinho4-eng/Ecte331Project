package part_c;
import java.util.concurrent.Semaphore;// Import Semaphore tool for thread coordination

public class main {
    // Shared variables
    private static int A1, A2, A3;
    private static int B1, B2, B3;

    // Semaphores starting with 0 permits
    private static Semaphore a1Done = new Semaphore(0);
    private static Semaphore b2Done = new Semaphore(0);
    private static Semaphore a2Done = new Semaphore(0);
    private static Semaphore b3Done = new Semaphore(0);

    public static void main(String[] args) {
        
    	// Create thread object for A
        Thread threadA = new Thread(new Runnable() {
            public void run() {
                try { //Start try block for catching synchronization errors
                    A1 = LoopSumCal.calculateSum(500); // Calculate sum up to 500 and save to A1
                    a1Done.release();// Release permit to let Thread B know A1 is ready
                    //Pause here until Thread B releases b2Done
                    b2Done.acquire();
                    // Compute A2 using updated B2 value
                    A2 = B2 + LoopSumCal.calculateSum(300);
                    a2Done.release();
                    b3Done.acquire();
                    // Compute A3 using updated B3 value
                    A3 = B3 + LoopSumCal.calculateSum(400);
                 // Catch block for thread interruption errors
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Thread B setup using traditional Runnable
        Thread threadB = new Thread(new Runnable() {
            public void run() {
                try {
                    B1 = LoopSumCal.calculateSum(250);

                    a1Done.acquire();
                    B2 = A1 + LoopSumCal.calculateSum(200);
                    b2Done.release();

                    a2Done.acquire();
                    B3 = A2 + LoopSumCal.calculateSum(400);
                    b3Done.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start both threads
        threadA.start();
        threadB.start();
        
        try {
            threadA.join(); // Block main thread until Thread A finishes up
            threadB.join(); // Block main thread until Thread B finishes up
        } catch (InterruptedException e) { //Catch block if main thread gets cut off
            e.printStackTrace();
        }

      // Print final values to the terminal screen
        System.out.println("A1 = " + A1);
        System.out.println("B1 = " + B1);
        System.out.println("B2 = " + B2);
        System.out.println("A2 = " + A2);
        System.out.println("B3 = " + B3);
        System.out.println("A3 = " + A3);
    }
}