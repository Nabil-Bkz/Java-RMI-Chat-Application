package ServerSide;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for the chat server.
 * Defines the contract for client-server communication.
 * 
 * @author Refactored
 */
public interface Chattable extends Remote {
    
    /**
     * Registers a new client to the chat server.
     * 
     * @param clientDetails Array containing: [0] username, [1] hostname, [2] client service name
     * @throws RemoteException if a remote communication error occurs
     * @throws IllegalArgumentException if clientDetails is null or invalid
     */
    void join(String[] clientDetails) throws RemoteException;
    
    /**
     * Broadcasts a chat message to all connected clients.
     * 
     * @param userName the username of the sender
     * @param chatMessage the message content
     * @throws RemoteException if a remote communication error occurs
     * @throws IllegalArgumentException if userName or chatMessage is null or empty
     */
    void updateChat(String userName, String chatMessage) throws RemoteException;
    
    /**
     * Removes a client from the chat server.
     * 
     * @param userName the username of the client leaving
     * @throws RemoteException if a remote communication error occurs
     * @throws IllegalArgumentException if userName is null or empty
     */
    void leaveChat(String userName) throws RemoteException;
    
    /**
     * Sends a private message to selected clients.
     * 
     * @param recipientIndices array of indices in the user list for recipients
     * @param privateMessage the private message content
     * @throws RemoteException if a remote communication error occurs
     * @throws IllegalArgumentException if recipientIndices is null or empty, or privateMessage is null
     */
    void sendPM(int[] recipientIndices, String privateMessage) throws RemoteException;
}
