package ServerSide;

import ClientSide.ChatClient;

/**
 * Represents a connected user in the chat system.
 * Encapsulates the user's name and their remote client reference.
 * 
 * @author Refactored
 */
public class Chatter {
    
    private final String name;
    private final ChatClient client;
    
    /**
     * Constructs a new Chatter instance.
     * 
     * @param name the username (must not be null or empty)
     * @param client the remote client reference (must not be null)
     * @throws IllegalArgumentException if name is null/empty or client is null
     */
    public Chatter(String name, ChatClient client) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        this.name = name.trim();
        this.client = client;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the remote client reference.
     * 
     * @return the ChatClient instance
     */
    public ChatClient getClient() {
        return client;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Chatter chatter = (Chatter) obj;
        return name.equals(chatter.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return "Chatter{name='" + name + "'}";
    }
}
