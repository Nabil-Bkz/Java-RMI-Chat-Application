package ClientSide;

import ServerSide.ChatConstants;
import ServerSide.Chattable;
import ServerSide.MessageFormatter;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Client implementation for the chat application.
 * Handles connection to the server and implements remote callbacks.
 * 
 * @author Refactored
 */
public class Client extends UnicastRemoteObject implements ChatClient, Runnable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    private final String username;
    private final ClientRMIGUI chatGUI;
    private Chattable chatService;
    private final String hostname;
    private final String clientServiceName;
    private volatile boolean connectionProblem = false;
    private volatile boolean isConnected = false;
    
    /**
     * Default constructor for RMI export.
     * 
     * @throws RemoteException if RMI export fails
     */
    public Client() throws RemoteException {
        super();
        this.username = null;
        this.chatGUI = null;
        this.hostname = "localhost";
        this.clientServiceName = null;
    }
    
    /**
     * Constructs a new client instance.
     * 
     * @param username the username for this client
     * @param chatGUI the GUI reference for callbacks
     * @throws RemoteException if RMI export fails
     * @throws IllegalArgumentException if username or chatGUI is null
     */
    public Client(String username, ClientRMIGUI chatGUI) throws RemoteException {
        super();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (chatGUI == null) {
            throw new IllegalArgumentException("Chat GUI cannot be null");
        }
        
        this.username = username.trim();
        this.chatGUI = chatGUI;
        this.hostname = "localhost";
        this.clientServiceName = ChatConstants.CLIENT_SERVICE_PREFIX + this.username;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the chat service reference.
     * 
     * @return the Chattable service, or null if not connected
     */
    public Chattable getChatService() {
        return chatService;
    }
    
    /**
     * Checks if there was a connection problem.
     * 
     * @return true if there was a connection problem
     */
    public boolean hasConnectionProblem() {
        return connectionProblem;
    }
    
    /**
     * Checks if the client is connected to the server.
     * 
     * @return true if connected
     */
    public boolean isConnected() {
        return isConnected;
    }
    
    /**
     * Main connection logic executed in a separate thread.
     * Connects to the RMI registry, looks up the chat service, and registers this client.
     */
    @Override
    public void run() {
        try {
            LOGGER.info("Connecting to chat server...");
            
            // Connect with retry logic
            ConnectionManager connectionManager = new ConnectionManager(
                hostname, ChatConstants.RMI_REGISTRY_PORT);
            chatService = connectionManager.connectWithRetry();
            LOGGER.info("Found chat service in registry");
            
            // Register this client with RMI
            String clientUrl = "rmi://" + hostname + "/" + clientServiceName;
            try {
                Naming.rebind(clientUrl, this);
                LOGGER.info("Client registered at: " + clientUrl);
            } catch (ConnectException e) {
                connectionProblem = true;
                LOGGER.log(Level.SEVERE, "Failed to connect to RMI registry", e);
                SwingUtilities.invokeLater(() -> {
                    chatGUI.showErrorMessage("Connection Error", 
                        "Failed to connect to server. Please ensure the server is running.");
                });
                return;
            } catch (Exception e) {
                connectionProblem = true;
                LOGGER.log(Level.SEVERE, "Unexpected error during client registration", e);
                SwingUtilities.invokeLater(() -> {
                    chatGUI.showErrorMessage("Registration Error", 
                        "Failed to register client: " + e.getMessage());
                });
                return;
            }
            
            // Join the chat
            String[] clientDetails = {username, hostname, clientServiceName};
            chatService.join(clientDetails);
            isConnected = true;
            
            LOGGER.info("Successfully joined chat as: " + username);
            SwingUtilities.invokeLater(() -> {
                chatGUI.setConnected(true);
                chatGUI.updateConnectionStatus("Connected", true);
                chatGUI.appendMessage(MessageFormatter.formatServerMessage(
                    "Successfully connected to chat server"));
            });
            
        } catch (RemoteException e) {
            connectionProblem = true;
            isConnected = false;
            LOGGER.log(Level.SEVERE, "Remote exception during connection", e);
            SwingUtilities.invokeLater(() -> {
                chatGUI.setConnected(false);
                chatGUI.updateConnectionStatus("Disconnected", false);
                chatGUI.showErrorMessage("Connection Error", 
                    "Failed to connect to server after retries: " + e.getMessage() + 
                    "\n\nPlease ensure the server is running and try again.");
            });
        } catch (Exception e) {
            connectionProblem = true;
            isConnected = false;
            LOGGER.log(Level.SEVERE, "Unexpected error during connection", e);
            SwingUtilities.invokeLater(() -> {
                chatGUI.setConnected(false);
                chatGUI.updateConnectionStatus("Error", false);
                chatGUI.showErrorMessage("Connection Error", 
                    "Unexpected error: " + e.getMessage());
            });
        }
    }
    
    @Override
    public void messageFromServer(String message) throws RemoteException {
        if (message == null) {
            LOGGER.warning("Received null message from server");
            return;
        }
        
        LOGGER.fine("Received message from server");
        
        // Update GUI on EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            chatGUI.appendMessage(message);
        });
    }
    
    @Override
    public void updateUserList(String[] currentUsers) throws RemoteException {
        if (currentUsers == null) {
            LOGGER.warning("Received null user list from server");
            return;
        }
        
        LOGGER.fine("Updating user list with " + currentUsers.length + " users");
        
        // Update GUI on EDT
        SwingUtilities.invokeLater(() -> {
            chatGUI.updateUserList(currentUsers);
        });
    }
    
    /**
     * Disconnects from the chat server.
     * 
     * @throws RemoteException if disconnection fails
     */
    public void disconnect() throws RemoteException {
        if (isConnected && chatService != null) {
            try {
                chatService.leaveChat(username);
                LOGGER.info("Disconnected from chat server");
            } catch (RemoteException e) {
                LOGGER.log(Level.WARNING, "Error during disconnect", e);
                throw e;
            } finally {
                isConnected = false;
            }
        }
    }
}
