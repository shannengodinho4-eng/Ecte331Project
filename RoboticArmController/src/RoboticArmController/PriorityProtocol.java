package RoboticArmController;

/**
 * Defines the synchronization modes for the robotic arm.
 */
public enum PriorityProtocol {
    NONE,           // Baseline with no priority management
    INHERITANCE,    // Priority Inheritance Protocol
    CEILING         // Priority Ceiling Protocol
}