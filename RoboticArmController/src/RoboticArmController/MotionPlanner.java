package RoboticArmController;

public class MotionPlanner extends Thread {
	// Flag to show if thread is working
    public static volatile boolean isComputing = false;
    public static final int PRIORITY = 5;
 // Constructor for the motion planner task
    public MotionPlanner() {
        super("Motion Planner (Medium)");
        this.setPriority(PRIORITY);// Assign the medium priority level to the thread
    }

    @Override
    public void run() {
        for (int i = 1; i <= 1; i++) { 
            try {
                Thread.sleep(400); // Wait 400ms to offset startup execution order
                TimeLogger.printEmptyLine();
                TimeLogger.print(getName() + " starting CPU calculations.");
                
                isComputing = true;// Set calculation flag back to false
                long workDone = 0;
                
                // Keep processing until 2000 units are reached
                while (workDone < 2000) {
                    if (mutualex.currentLockOwnerPriority > PRIORITY) {
                        Thread.sleep(10); // Yield to Higher priority thread
                    } else {
                        Thread.sleep(10);
                        workDone += 10;
                    }
                }
                isComputing = false;
                
                TimeLogger.print(getName() + " finished calculations.");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                isComputing = false;
                Thread.currentThread().interrupt();// Restore interrupted status flag
            }
        }
    }
}