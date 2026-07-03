package Drone_Nav_System;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Main simulation class for the fault-tolerant autonomous drone navigation system.
 * Simulates redundant sensor readings, majority voting (TMR), and safe mode transitions.
 */
public class Drone_main {
	// Start the drone at a default baseline altitude of 120 meters
    private static int currentAltitude = 120;
    private static Random random = new Random();
    
 // Generate a random 4-digit ID for the log file so runs don't overwrite each other
    private static String logFileName = "log_" + (1000 + random.nextInt(9000)) + ".txt";

    /**
     * Main method that controls the simulation loop and processes sensor data.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        logEvent("START: Drone navigation controller is now running.");
        System.out.println();
     // Tracks consecutive bad cycles, if this hits 2, emergency ground the drone
        int consecutiveFailures = 0;

        try {// Run the simulation for 10 cycles
            for (int cycle = 1; cycle <= 10; cycle++) {
                System.out.println("--- Reading Cycle: " + cycle + " ---");
                System.out.println(); 
                //raw altitude data from each sensor stored
                int valA = -1;
                int valB = -1;
                int valC = -1;

                boolean statusA = true;
                boolean validA = false;
                boolean statusB = true;
                boolean validB = false;
                boolean statusC = true;
                boolean validC = false;
               

                // reading sensor C
                try {
                    valC = readSensor("Sensor C");
                    if (valC >= 0 && valC <= 200) {
                        validC = true;
                    } else {
                        logEvent("CORRUPTED READING: Sensor C value out of range (" + valC + "m)");
                    }
                } catch (SensorReadException e) {
                    statusC = false; // Hardware link is dead for this cycle
                    logEvent("SENSOR FAILURE: Sensor C link lost.");
                }

             // reading sensor B

                try {
                    valB = readSensor("Sensor B");
                    if (valB >= 0 && valB <= 200) {
                        validB = true;
                    } else {
                        logEvent("CORRUPTED READING: Sensor B value out of range (" + valB + "m)");
                    }
                } catch (SensorReadException e) {
                    statusB = false;
                    logEvent("SENSOR FAILURE: Sensor B link lost.");
                }

             // reading sensor A
                try {
                    valA = readSensor("Sensor A");
                    if (valA >= 0 && valA <= 200) {
                        validA = true;
                    } else {
                        logEvent("CORRUPTED READING: Sensor A value out of range (" + valA + "m)");
                    }
                } catch (SensorReadException e) {
                    statusA = false;
                    logEvent("SENSOR FAILURE: Sensor A link lost.");
                }

                System.out.println();

                // Display outputs clearly
                System.out.print("Sensor Readings -> ");
                System.out.print("A: " + (validA ? valA + "m" : "INVALID") + " | ");
                System.out.print("B: " + (validB ? valB + "m" : "INVALID") + " | ");
                System.out.println("C: " + (validC ? valC + "m" : "INVALID"));

                System.out.println();
                
             // Count up how many functional data streams we actually have right now
                int validCount = 0;
                if (validA) validCount++;
                if (validB) validCount++;
                if (validC) validCount++;

                boolean cycleFailed = false;
                //checking if atleast 2 sensors are working to perform a majority vote
                if (validCount >= 2) {
                	//checking if Sensors B and C match
                    if (validB && validC && valB == valC) {
                        currentAltitude = valB;
                        consecutiveFailures = 0; //reset counter since system is stable 
                        logEvent("MAJORITY DECISION: Agreement between B and C. Selected Altitude: " + currentAltitude + "m");
                        //flag if sensor A is giving a weird reading or failed
                        if (!validA || valA != currentAltitude) {
                            logEvent("OUTLIER DETECTION: Sensor A variance detected.");
                        }
                    }
                  //checking if Sensors A and C match
                    else if (validA && validC && valA == valC) {
                        currentAltitude = valA;
                        consecutiveFailures = 0;
                        logEvent("MAJORITY DECISION: Agreement between A and C. Selected Altitude: " + currentAltitude + "m");
                        if (!validB || valB != currentAltitude) {
                            logEvent("OUTLIER DETECTION: Sensor B variance detected.");
                        }
                    }
                  //checking if Sensors A and B match
                    else if (validA && validB && valA == valB) {
                        currentAltitude = valA;
                        consecutiveFailures = 0;
                        logEvent("MAJORITY DECISION: Agreement between A and B. Selected Altitude: " + currentAltitude + "m");
                        if (!validC || valC != currentAltitude) {
                            logEvent("OUTLIER DETECTION: Sensor C variance detected.");
                        }
                    }
                    else { //if every sensor returned a completely different number.
                        cycleFailed = true;
                        logEvent("RELIABILITY FAILURE: No majority agreement found among different sensor readings.");
                    }
                } else { // if sensors are not working well
                    cycleFailed = true;
                    logEvent("RELIABILITY FAILURE: Insufficient valid sensors available to vote.");
                }

                System.out.println();

                if (cycleFailed) { //if voting fails,  fallback routine will be handled
                    consecutiveFailures++;
                    // fix by maintaining the last known good altitude reading
                    logEvent("FALLBACK DECISION: Maintaining previous valid altitude: " + currentAltitude + "m");
                    System.out.println("Reliability Status: Consecutive Failures [" + consecutiveFailures + "/2]");
                    //if 2 failures in a row , trigger emergency landing
                    if (consecutiveFailures >= 2) {
                        throw new SystemReliabilityException("Maximum reliability failure threshold reached.");
                    }
                } else {
                    System.out.println("Reliability Status: System Stable [0/2]");
                }
                System.out.println();
                System.out.println(); 
            }
         // Force an emergency landing due to persistent sensor failures.
        } catch (SystemReliabilityException e) {
            System.out.println();
            logEvent("CRITICAL ERROR: " + e.getMessage());
            logEvent("SAFE MODE ACTIVATED: Stopping execution safety routines.");
            System.out.println("\n>> SAFE MODE SHUTDOWN COMPLETE <<");
        }
    }

    /**
     * Grabs a mock altitude reading from the requested sensor.
     * to decide if the sensor works normally, spits out out-of-bounds data, 
     * or drops the connection entirely.
     * * @param sensorId The label of the hardware piece we are querying (e.g., "Sensor A").
     * @return A valid altitude in meters, or a garbage value representing data corruption.
     * @throws SensorReadException Triggers a 15% failure rate if the connection times out.
     */
    public static int readSensor(String sensorId) throws SensorReadException {
        int chance = random.nextInt(100);
     // 15% chance the sensor completely loses connection/hardware drops out
        if (chance < 15) {
            throw new SensorReadException(sensorId + " transmission error.");
        }
        else if (chance < 30) { // 15% chance (15 to 29) the sensor sends a completely impossible garbage value
            if (random.nextBoolean()) {
                return -25;
            } else {
                return 225;
            }
        }
        else {
            int variance = random.nextInt(3) - 1;
            // Ensures the valid reading strictly stays within the 0 to 200 bounds
            int validReading = currentAltitude + variance;
            return Math.max(0, Math.min(200, validReading));
        }
    }

    /**
     * Prints the event to the console and appends it to the active log file 
     * with a current timestamp.
     * * @param message The text snippet or error details we want to record.
     */
    public static void logEvent(String message) {
        String timestamp = LocalDateTime.now().toString();
        String entry = "[" + timestamp + "] " + message;
        
        System.out.println(entry);

        try {
            // Appends directly to the unique run session log file name
            FileWriter fw = new FileWriter(logFileName, true);
            fw.write(entry + "\n");
            fw.close();
        } catch (IOException e) {
            System.out.println("Log update error encountered.");
        }
    }
}