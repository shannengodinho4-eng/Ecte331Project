package RoboticArmController;

public class main_code {
    public static void main(String[] args) {
        TimeLogger.print("Real-Time Robotic Arm Controller Simulation");
        TimeLogger.printEmptyLine();

        // Run baseline simulation showing priority inversion
        long noneWait = runSimulation(PriorityProtocol.NONE, "Scenario 1: Baseline (Priority Inversion)");
        sleep(1000);
        
        // Run simulation using Priority Inheritance Protocol
        long pipWait = runSimulation(PriorityProtocol.INHERITANCE, "Scenario 2: Priority Inheritance Protocol");
        sleep(1000);
        
        // Run simulation using Priority Ceiling Protocol
        long pcpWait = runSimulation(PriorityProtocol.CEILING, "Scenario 3: Priority Ceiling Protocol");

        // Print final execution delays for comparison
        TimeLogger.printEmptyLine();
        TimeLogger.print("Performance Evaluation Results");
        TimeLogger.print("Baseline Wait Time: " + noneWait + " ms");
        TimeLogger.print("Inheritance Wait Time: " + pipWait + " ms");
        TimeLogger.print("Ceiling Wait Time: " + pcpWait + " ms");
        TimeLogger.printEmptyLine();
    }

    // Sets up the lock, resource, and threads for a test run
    private static long runSimulation(PriorityProtocol protocol, String title) {
        TimeLogger.printEmptyLine();
        TimeLogger.print("Starting " + title);
        TimeLogger.printEmptyLine();
        
        // Create the lock and motor resource using the selected protocol
        int ceilingPriority = 8; 
        mutualex lock = new mutualex(protocol, ceilingPriority);
        MotorController controller = new MotorController(lock);

        // Instantiate low, high, and medium priority real-time threads
        logger logTask = new logger(controller);
        SafetyMonitor safetyMonitor = new SafetyMonitor(controller);
        MotionPlanner motionPlanner = new MotionPlanner();

        // Start thread execution
        logTask.start();
        safetyMonitor.start();
        motionPlanner.start(); 

        // Wait for all threads to complete execution
        try {
            logTask.join();
            safetyMonitor.join();
            motionPlanner.join();
        } catch (InterruptedException e) {
            TimeLogger.print("Simulation interrupted.");
        }

        // Calculate and log total blocked time for the high-priority thread
        long waitingTime = lock.getTotalWaitingTime(safetyMonitor);
        TimeLogger.printEmptyLine();
        TimeLogger.print("Finished " + title + ". High-priority thread waited: " + waitingTime + " ms");
        return waitingTime;
    }

    // Standard sleep delay between test passes
    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
