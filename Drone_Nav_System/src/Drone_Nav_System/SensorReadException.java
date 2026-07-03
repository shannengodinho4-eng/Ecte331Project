package Drone_Nav_System;

import java.io.IOException;

/**
 * Simulates a physical hardware glitch, like a loose wire or a radio timeout.
 * Extends IOException because it deals directly with raw data stream drops.
 */
public class SensorReadException extends IOException {
    
    /**
     * Creates a hardware failure log entry.
     * * @param message Specifies which physical sensor dropped offline during the cycle.
     */
    public SensorReadException(String message) {
        super(message);
    }
}