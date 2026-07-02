package RoboticArmController;

import java.util.HashMap;
import java.util.Map;

public class mutualex {
    
    private final PriorityProtocol protocol;// Store the selected protocol
    private Thread owner = null; //Track which thread holds the lock
    private final int ceilingPriority; //Store the high priority ceiling cap
        
    //Global tracker for active owner priority
    public static volatile int currentLockOwnerPriority = -1;
    
    //Store normal thread priorities
    private final Map<Thread, Integer> originalPriorities = new HashMap<>();
    // Store timestamp when lock was requested
    private final Map<Thread, Long> lockRequestedTime = new HashMap<>();
    // Store total calculated waiting delay
    private final Map<Thread, Long> totalWaitTime = new HashMap<>();

    public mutualex(PriorityProtocol protocol, int ceilingPriority) {
    	// Save protocol setting and ceiling priority level
        this.protocol = protocol;
        this.ceilingPriority = ceilingPriority;
    }

    public synchronized void lock() { // Method to acquire the resource lock
        Thread caller = Thread.currentThread();
        long requestTime = System.currentTimeMillis();
        // Save the request timestamp
        lockRequestedTime.put(caller, requestTime);

        TimeLogger.print(caller.getName() + " attempting to acquire lock. Priority: " + caller.getPriority());

     // Loop while another thread holds lock
        while (owner != null) {
            if (protocol == PriorityProtocol.INHERITANCE) { // Check if PIP protocol is active
                if (caller.getPriority() > owner.getPriority()) { // If waiting thread is higher priority
                    owner.setPriority(caller.getPriority());// Raise owner priority to match caller
                    currentLockOwnerPriority = caller.getPriority(); // Update global tracker value
                    TimeLogger.print("PRIORITY INHERITANCE: " + owner.getName() + " elevated to priority " + caller.getPriority());
                }
            }
            try { //Try block for wait state
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
     // Set current caller as the lock owner
        owner = caller;
        long acquireTime = System.currentTimeMillis();
     // Calculating the time spent waiting
        long currentWait = acquireTime - requestTime;
        
        totalWaitTime.put(caller, totalWaitTime.getOrDefault(caller, 0L) + currentWait);

        if (!originalPriorities.containsKey(caller)) {
            originalPriorities.put(caller, caller.getPriority());
        }
        // Log success
        TimeLogger.print(caller.getName() + " ACQUIRED lock. Wait time: " + currentWait + " ms");

        if (protocol == PriorityProtocol.CEILING) { // Check if PCP protocol is active
            if (caller.getPriority() < ceilingPriority) {
                caller.setPriority(ceilingPriority); // Lift thread priority to ceiling immediately
                TimeLogger.print("PRIORITY CEILING: " + caller.getName() + " elevated to ceiling " + ceilingPriority);
            }
        }
        
        currentLockOwnerPriority = caller.getPriority(); // Set global tracker
    }

    public synchronized void unlock() { // Method to release the lock
        Thread caller = Thread.currentThread();
        if (caller != owner) { // Verify if caller is actual owner
            return; 
        }

        TimeLogger.print(caller.getName() + " releasing lock.");// Log release 

        if (originalPriorities.containsKey(caller)) {
            int origPriority = originalPriorities.get(caller);// Retrieve original priority value
            if (caller.getPriority() != origPriority) {
            	// Log reset
                TimeLogger.print("PRIORITY RESTORED: " + caller.getName() + " back to " + origPriority);
                caller.setPriority(origPriority);
            }
        }

        owner = null;// Free up the owner field
        currentLockOwnerPriority = -1; // Clear global tracker
        notifyAll();// Wake up all waiting threads
    }

    public synchronized long getTotalWaitingTime(Thread t) {
        // Check if the thread has a recorded wait time
        if (totalWaitTime.containsKey(t)) {
            return totalWaitTime.get(t); // Return the saved time
        }
        return 0L; // Default to 0 if not found
    }
}