package ServerSide;

/**
 * Constants used throughout the chat application.
 * Centralizes configuration values for easier maintenance.
 */
public final class ChatConstants {
    
    private ChatConstants() {
        // Utility class - prevent instantiation
    }
    
    /** Default RMI registry port */
    public static final int RMI_REGISTRY_PORT = 1099;
    
    /** Server service name in RMI registry */
    public static final String SERVER_SERVICE_NAME = "Chat";
    
    /** Client service name prefix */
    public static final String CLIENT_SERVICE_PREFIX = "ClientListenService_";
    
    /** Minimum number of users required for private messaging */
    public static final int MIN_USERS_FOR_PRIVATE_MESSAGE = 2;
    
    /** Initial capacity for client list */
    public static final int INITIAL_CLIENT_LIST_CAPACITY = 10;
    
    /** Capacity increment for client list */
    public static final int CLIENT_LIST_CAPACITY_INCREMENT = 1;
}

