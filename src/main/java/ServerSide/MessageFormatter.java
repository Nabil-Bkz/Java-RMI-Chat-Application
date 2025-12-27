package ServerSide;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting chat messages with timestamps.
 * 
 * @author Refactored
 */
public final class MessageFormatter {
    
    private static final DateTimeFormatter TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private MessageFormatter() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Formats a chat message with timestamp.
     * 
     * @param username the sender's username
     * @param message the message content
     * @return formatted message with timestamp
     */
    public static String formatChatMessage(String username, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        return String.format("[%s] %s : %s\n", timestamp, username, message);
    }
    
    /**
     * Formats a server notification message with timestamp.
     * 
     * @param notification the notification message
     * @return formatted notification with timestamp
     */
    public static String formatServerMessage(String notification) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        return String.format("[%s] [Server] : %s\n", timestamp, notification);
    }
    
    /**
     * Formats a private message with timestamp.
     * 
     * @param sender the sender's username
     * @param message the message content
     * @return formatted private message with timestamp
     */
    public static String formatPrivateMessage(String sender, String message) {
        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        return String.format("[%s] [PM from %s] : %s\n", timestamp, sender, message);
    }
    
    /**
     * Sanitizes user input to prevent injection or formatting issues.
     * 
     * @param input the input to sanitize
     * @return sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove control characters but keep newlines for multi-line messages
        return input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]", "").trim();
    }
    
    /**
     * Validates username format.
     * 
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmed = username.trim();
        // Username: 3-20 chars, alphanumeric, underscore, hyphen only
        return trimmed.length() >= 3 && 
               trimmed.length() <= 20 && 
               trimmed.matches("^[a-zA-Z0-9_-]+$");
    }
}

