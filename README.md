# Java RMI Chat Application

A modern, distributed chat application built with Java RMI technology. This project demonstrates real-time client-server communication using Remote Method Invocation (RMI).

##  Project Information

This is a third-year college project showcasing distributed systems concepts and Java networking.

## Features

### Core Functionality
- **Real-time Messaging**: Broadcast messages to all connected clients instantly
- **Private Messaging**: Send private messages to selected users
- **User Management**: Automatic user list updates as users join/leave
- **Username Validation**: Secure username format validation (3-20 characters, alphanumeric with underscores/hyphens)
- **Connection Management**: Automatic connection retry with up to 3 attempts
- **Thread-Safe Operations**: Concurrent client management with proper synchronization

### Enhanced Features
- **Message Timestamps**: All messages include timestamps in `[HH:mm:ss]` format
- **Modern GUI**: Clean, professional interface with modern color scheme
- **Connection Status Indicator**: Visual status bar showing connection state (Connected/Disconnected)
- **User Count Display**: Real-time display of online users
- **Clear Chat Functionality**: Clear chat history with button or keyboard shortcut (Ctrl+L)
- **Keyboard Shortcuts**: 
  - `Enter` - Send message
  - `Ctrl+L` - Clear chat
- **Input Sanitization**: Automatic sanitization of user inputs to prevent issues
- **Error Handling**: User-friendly error messages with actionable feedback
- **Auto-scroll**: Chat automatically scrolls to show latest messages

## Architecture

The application follows a distributed client-server architecture:


- **Server**: Central message broker managing all client connections
- **Clients**: GUI applications that connect to the server via RMI
- **Communication**: Bidirectional RMI communication (client ↔ server ↔ clients)

## Technology Stack

- **Java 19**: Modern Java features and performance
- **Java RMI**: Remote Method Invocation for distributed communication
- **Java Swing**: Cross-platform GUI framework
- **Maven**: Build automation and dependency management

##  Prerequisites

- **Java 19** or higher
- **Maven 3.6** or higher
- **Network access** (for RMI communication)

##  Getting Started

### Building the Project

```bash
# Clone or download the project
cd Java-RMI-Chat-Application

# Compile the project
mvn clean compile

# Package as JAR (optional)
mvn clean package
```

### Running the Application

#### Step 1: Start the Server

```bash
mvn compile exec:java -Dexec.mainClass="ServerSide.Chat"
```

The server will start and display:
```
[INFO] Starting chat server...
[INFO] RMI Registry created on port 1099
[INFO] Chat server is running and bound to 'Chat'
[INFO] Server ready to accept connections
```

#### Step 2: Launch Clients

In separate terminal windows, run:

```bash
mvn compile exec:java -Dexec.mainClass="ClientSide.ClientRMIGUI"
```

Repeat this command to launch multiple clients.

#### Step 3: Connect and Chat

1. Enter a unique username (3-20 characters, alphanumeric with underscores/hyphens)
2. Click **"▶ Start"** to connect to the server
3. Once connected, you can:
   - Type messages and press `Enter` or click **" Send"** to broadcast
   - Select users from the list and click **" Private"** for private messages
   - Click **" Clear"** or press `Ctrl+L` to clear chat history



##  Configuration

### Server Configuration

Edit `ServerSide/ChatConstants.java` to modify:
- RMI Registry Port (default: 1099)
- Server Service Name (default: "Chat")
- Client Service Prefix
