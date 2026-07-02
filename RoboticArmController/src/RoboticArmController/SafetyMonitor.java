package RoboticArmController;

public class SafetyMonitor extends Thread {
    private final MotorController motorController;

    public SafetyMonitor(MotorController motorController) {
        super("Safety Monitor (High)");// Set the thread name to high priority safety monitor
        this.setPriority(8); // Set high priority level to 8
        this.motorController = motorController;
    }

    @Override
    public void run() {
        for (int i = 1; i <= 1; i++) {
            try {// Start block to catch sleep errors
                Thread.sleep(200); 
                TimeLogger.printEmptyLine();
                TimeLogger.print(getName() + " ready.");
                
                motorController.useMotor(getName(), 500); // Try to get lock and use motor for 500ms
                
                Thread.sleep(1000);// Hold down thread for 1 second before finishing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();// Set the interrupt flag back to true
            }
        }
    }
}