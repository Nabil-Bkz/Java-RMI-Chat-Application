package ClientSide;

import ServerSide.ChatConstants;
import ServerSide.Chattable;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages connection to the chat server with retry logic.
 * 
 * @author Refactored
 */
public class ConnectionManager {
    
    private static final Logger LOGGER = Logger.getLogger(ConnectionManager.class.getName());
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 2000; // 2 seconds
    
    private final String hostname;
    private final int port;
    
    /**
     * Creates a new connection manager.
     * 
     * @param hostname the server hostname
     * @param port the RMI registry port
     */
    public ConnectionManager(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    /**
     * Connects to the chat server with retry logic.
     * 
     * @return the chat service if connection succeeds
     * @throws RemoteException if connection fails after all retries
     */
    public Chattable connectWithRetry() throws RemoteException {
        RemoteException lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                LOGGER.info(String.format("Connection attempt %d of %d", attempt, MAX_RETRY_ATTEMPTS));
                
                Registry registry = LocateRegistry.getRegistry(hostname, port);
                Chattable chatService = (Chattable) registry.lookup(ChatConstants.SERVER_SERVICE_NAME);
                
                LOGGER.info("Successfully connected to chat server");
                return chatService;
                
            } catch (ConnectException e) {
                lastException = new RemoteException("Failed to connect to server", e);
                LOGGER.log(Level.WARNING, 
                    String.format("Connection attempt %d failed", attempt), e);
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RemoteException("Connection interrupted", ie);
                    }
                }
            } catch (RemoteException e) {
                lastException = e;
                LOGGER.log(Level.WARNING, 
                    String.format("Connection attempt %d failed", attempt), e);
                
                if (attempt < MAX_RETRY_ATTEMPTS) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RemoteException("Connection interrupted", ie);
                    }
                }
            } catch (Exception e) {
                lastException = new RemoteException("Unexpected error during connection", e);
                LOGGER.log(Level.SEVERE, 
                    String.format("Unexpected error on attempt %d", attempt), e);
                break; // Don't retry on unexpected errors
            }
        }
        
        throw lastException != null ? lastException : 
            new RemoteException("Failed to connect after " + MAX_RETRY_ATTEMPTS + " attempts");
    }
}

