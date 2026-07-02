package RoboticArmController;

public class logger extends Thread {
    private final MotorController motorController;

    // constructor to setup a low priority thread
    public logger(MotorController motorController) {
        super("Logger (Low)");
        this.setPriority(2); 
        this.motorController = motorController;
    }

    @Override
    public void run() {
        int runCount = 1;
        
        // simple loop to run the simulation task once
        while (runCount <= 1) {
            TimeLogger.printEmptyLine();
            TimeLogger.print(this.getName() + " ready.");
            
            // grab lock and use motor for 1000ms
            this.motorController.useMotor(this.getName(), 1000); 
            
            // wait out the remaining time
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            runCount++;
        }
    }
}