package RoboticArmController;

public class MotorController {
    private final mutualex lock; 

    public MotorController(mutualex lock) {
        this.lock = lock;// Store the passed lock instance
    }

    public void useMotor(String threadName, int durationMs) {
        lock.lock();// Grab the lock here
        try {
            TimeLogger.print(threadName + " is controlling the motor for " + durationMs + " ms.");
            
            // Track how much time passed
            long workDone = 0;	
            while (workDone < durationMs) {
                if (MotionPlanner.isComputing && Thread.currentThread().getPriority() < MotionPlanner.PRIORITY) {
                    // Starved of CPU by the Medium thread!
                    Thread.sleep(10); // Sleep without adding to workDone
                } else {
                    Thread.sleep(10);// Sleep for a small step
                    workDone += 10;
                }
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();// Release lock for next thread
        }
    }
}