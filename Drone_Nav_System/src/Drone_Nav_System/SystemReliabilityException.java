package Drone_Nav_System;

/**
 * Thrown when the system runs out of fallback options and has to force an emergency landing.
 * Acts as the final tripwire when we can't establish a reliable altitude consensus.
 */
public class SystemReliabilityException extends Exception {
    
    /**
     * Creates a reliability alert with details on what broke down.
     * * @param message Explains exactly why the flight controller is pulling the plug.
     */
    public SystemReliabilityException(String message) {
        super(message);
    }
}