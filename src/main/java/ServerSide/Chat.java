package ServerSide;

import ClientSide.ChatClient;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main chat server implementation using Java RMI.
 * Manages client connections, message broadcasting, and user list updates.
 * 
 * @author Refactored
 */
public class Chat extends UnicastRemoteObject implements Chattable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Chat.class.getName());
    
    /** Thread-safe list of connected clients */
    private final List<Chatter> connectedClients;
    
    /**
     * Constructs a new Chat server instance.
     * 
     * @throws RemoteException if RMI export fails
     */
    public Chat() throws RemoteException {
        super();
        this.connectedClients = Collections.synchronizedList(new ArrayList<>());
        LOGGER.info("Chat server initialized");
    }
    
    /**
     * Main entry point for the chat server.
     * Creates and starts the RMI registry and binds the chat service.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            LOGGER.info("Starting chat server...");
            
            // Create RMI registry
            Registry registry = LocateRegistry.createRegistry(ChatConstants.RMI_REGISTRY_PORT);
            LOGGER.info("RMI Registry created on port " + ChatConstants.RMI_REGISTRY_PORT);
            
            // Create and bind chat service
            Chattable chatService = new Chat();
            registry.rebind(ChatConstants.SERVER_SERVICE_NAME, chatService);
            
            LOGGER.info("Chat server is running and bound to '" + ChatConstants.SERVER_SERVICE_NAME + "'");
            LOGGER.info("Server ready to accept connections");
            
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Failed to start chat server", e);
            System.err.println("Error starting server: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error starting server", e);
            System.err.println("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    @Override
    public void join(String[] clientDetails) throws RemoteException {
        validateClientDetails(clientDetails);
        
        String username = clientDetails[0].trim();
        String hostname = clientDetails[1];
        String clientServiceName = clientDetails[2];
        
        try {
            // Check if username already exists
            if (isUserConnected(username)) {
                LOGGER.warning("Attempted to join with duplicate username: " + username);
                throw new RemoteException("Username '" + username + "' is already in use");
            }
            
            // Lookup client's remote object
            String clientUrl = "rmi://" + hostname + "/" + clientServiceName;
            ChatClient client = (ChatClient) Naming.lookup(clientUrl);
            
            // Add client to connected list
            Chatter newChatter = new Chatter(username, client);
            synchronized (connectedClients) {
                connectedClients.add(newChatter);
            }
            
            LOGGER.info("User '" + username + "' joined the chat");
            
            // Notify all clients
            String joinMessage = MessageFormatter.formatServerMessage(
                username + " has joined the chat!");
            broadcastMessage(joinMessage);
            
            // Update user lists
            updateAllUserLists();
            
        } catch (RemoteException e) {
            LOGGER.log(Level.SEVERE, "Error joining user: " + username, e);
            throw e;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error joining user: " + username, e);
            throw new RemoteException("Failed to join chat: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void leaveChat(String userName) throws RemoteException {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        String trimmedName = userName.trim();
        boolean removed = false;
        
        synchronized (connectedClients) {
            Iterator<Chatter> iterator = connectedClients.iterator();
            while (iterator.hasNext()) {
                Chatter chatter = iterator.next();
                if (chatter.getName().equals(trimmedName)) {
                    iterator.remove();
                    removed = true;
                    break;
                }
            }
        }
        
        if (removed) {
            LOGGER.info("User '" + trimmedName + "' left the chat");
            
            // Update remaining clients' user lists
            if (!connectedClients.isEmpty()) {
                updateAllUserLists();
            }
        } else {
            LOGGER.warning("Attempted to remove non-existent user: " + trimmedName);
        }
    }
    
    @Override
    public void updateChat(String userName, String chatMessage) throws RemoteException {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (chatMessage == null || chatMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Chat message cannot be null or empty");
        }
        
        // Sanitize inputs
        String sanitizedUsername = MessageFormatter.sanitizeInput(userName);
        String sanitizedMessage = MessageFormatter.sanitizeInput(chatMessage);
        
        // Format message with timestamp
        String formattedMessage = MessageFormatter.formatChatMessage(sanitizedUsername, sanitizedMessage);
        LOGGER.fine("Broadcasting message from " + sanitizedUsername);
        broadcastMessage(formattedMessage);
    }
    
    @Override
    public void sendPM(int[] recipientIndices, String privateMessage) throws RemoteException {
        if (recipientIndices == null || recipientIndices.length == 0) {
            throw new IllegalArgumentException("Recipient indices cannot be null or empty");
        }
        if (privateMessage == null || privateMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Private message cannot be null or empty");
        }
        
        // Sanitize the private message
        String sanitizedMessage = MessageFormatter.sanitizeInput(privateMessage);
        
        synchronized (connectedClients) {
            for (int index : recipientIndices) {
                if (index < 0 || index >= connectedClients.size()) {
                    LOGGER.warning("Invalid recipient index: " + index);
                    continue;
                }
                
                try {
                    Chatter recipient = connectedClients.get(index);
                    recipient.getClient().messageFromServer(sanitizedMessage);
                    LOGGER.fine("Sent private message to: " + recipient.getName());
                } catch (RemoteException e) {
                    LOGGER.log(Level.WARNING, "Failed to send PM to index " + index, e);
                    // Continue sending to other recipients
                }
            }
        }
    }
    
    /**
     * Broadcasts a message to all connected clients.
     * 
     * @param message the message to broadcast
     */
    private void broadcastMessage(String message) {
        List<Chatter> clientsToRemove = new ArrayList<>();
        
        synchronized (connectedClients) {
            for (Chatter chatter : connectedClients) {
                try {
                    chatter.getClient().messageFromServer(message);
                } catch (RemoteException e) {
                    LOGGER.log(Level.WARNING, "Failed to send message to " + chatter.getName() + 
                              " - removing from list", e);
                    clientsToRemove.add(chatter);
                }
            }
            
            // Remove disconnected clients
            connectedClients.removeAll(clientsToRemove);
        }
        
        // Update user lists if clients were removed
        if (!clientsToRemove.isEmpty() && !connectedClients.isEmpty()) {
            updateAllUserLists();
        }
    }
    
    /**
     * Updates the user list for all connected clients.
     */
    private void updateAllUserLists() {
        String[] currentUsers = getUserList();
        
        synchronized (connectedClients) {
            List<Chatter> clientsToRemove = new ArrayList<>();
            
            for (Chatter chatter : connectedClients) {
                try {
                    chatter.getClient().updateUserList(currentUsers);
                } catch (RemoteException e) {
                    LOGGER.log(Level.WARNING, "Failed to update user list for " + chatter.getName(), e);
                    clientsToRemove.add(chatter);
                }
            }
            
            // Remove disconnected clients
            connectedClients.removeAll(clientsToRemove);
        }
    }
    
    /**
     * Gets an array of all currently connected usernames.
     * 
     * @return array of usernames
     */
    private String[] getUserList() {
        synchronized (connectedClients) {
            String[] userList = new String[connectedClients.size()];
            for (int i = 0; i < connectedClients.size(); i++) {
                userList[i] = connectedClients.get(i).getName();
            }
            return userList;
        }
    }
    
    /**
     * Checks if a username is already connected.
     * 
     * @param username the username to check
     * @return true if the user is connected, false otherwise
     */
    private boolean isUserConnected(String username) {
        synchronized (connectedClients) {
            return connectedClients.stream()
                    .anyMatch(chatter -> chatter.getName().equalsIgnoreCase(username));
        }
    }
    
    /**
     * Validates client details array.
     * 
     * @param clientDetails the client details to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClientDetails(String[] clientDetails) {
        if (clientDetails == null) {
            throw new IllegalArgumentException("Client details cannot be null");
        }
        if (clientDetails.length < 3) {
            throw new IllegalArgumentException("Client details must contain at least 3 elements");
        }
        if (clientDetails[0] == null || clientDetails[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        
        // Validate username format
        String username = clientDetails[0].trim();
        if (!MessageFormatter.isValidUsername(username)) {
            throw new IllegalArgumentException(
                "Invalid username format. Username must be 3-20 characters and contain only letters, numbers, underscores, and hyphens.");
        }
        
        if (clientDetails[1] == null || clientDetails[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Hostname cannot be null or empty");
        }
        if (clientDetails[2] == null || clientDetails[2].trim().isEmpty()) {
            throw new IllegalArgumentException("Client service name cannot be null or empty");
        }
    }
    
    /**
     * Gets the number of currently connected clients.
     * 
     * @return the number of connected clients
     */
    public int getConnectedClientCount() {
        return connectedClients.size();
    }
}
