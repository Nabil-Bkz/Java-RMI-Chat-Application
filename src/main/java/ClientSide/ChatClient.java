package ClientSide;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for client-side callbacks.
 * Defines methods that the server can invoke on clients.
 * 
 * @author Refactored
 */
public interface ChatClient extends Remote {
    
    /**
     * Receives a message from the server (broadcast or private).
     * 
     * @param message the message content
     * @throws RemoteException if a remote communication error occurs
     */
    void messageFromServer(String message) throws RemoteException;
    
    /**
     * Updates the client's user list with currently connected users.
     * 
     * @param currentUsers array of currently connected usernames
     * @throws RemoteException if a remote communication error occurs
     */
    void updateUserList(String[] currentUsers) throws RemoteException;
}
