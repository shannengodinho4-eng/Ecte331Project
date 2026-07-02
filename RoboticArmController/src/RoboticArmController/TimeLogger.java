package RoboticArmController;
import java.time.LocalTime;// Import LocalTime utility for current system time
import java.time.format.DateTimeFormatter;// Import formatter tool for timestamps

/**
 * Utility class to handle formatted logging with timestamps.
 */
public class TimeLogger {
	// Setup hour-minute-second-millisecond layout
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public static void print(String message) {// Method to log messages with timestamps
        System.out.println("[" + LocalTime.now().format(formatter) + "] " + message);
    }

    public static void printEmptyLine() {
        System.out.println(); // Output an empty line without any time text
    }
}