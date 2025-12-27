# Java RMI Chat Application

A modern, distributed chat application built with Java RMI technology. This project demonstrates real-time client-server communication using Remote Method Invocation (RMI).

## 🎓 Project Information

This is a third-year college project showcasing distributed systems concepts and Java networking.

## ✨ Features

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

## 🏗️ Architecture

The application follows a distributed client-server architecture:

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Client 1  │◄────────┤             │────────►│   Client 2  │
│   (GUI)     │         │   Server    │         │   (GUI)     │
└─────────────┘         │  (RMI)      │         └─────────────┘
                        │             │
┌─────────────┐         │             │         ┌─────────────┐
│   Client 3  │────────►│             │◄────────│   Client N  │
│   (GUI)     │         └─────────────┘         │   (GUI)     │
└─────────────┘                                  └─────────────┘
```

- **Server**: Central message broker managing all client connections
- **Clients**: GUI applications that connect to the server via RMI
- **Communication**: Bidirectional RMI communication (client ↔ server ↔ clients)

## 🛠️ Technology Stack

- **Java 19**: Modern Java features and performance
- **Java RMI**: Remote Method Invocation for distributed communication
- **Java Swing**: Cross-platform GUI framework
- **Maven**: Build automation and dependency management

## 📋 Prerequisites

- **Java 19** or higher
- **Maven 3.6** or higher
- **Network access** (for RMI communication)

## 🚀 Getting Started

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
   - Type messages and press `Enter` or click **"📤 Send"** to broadcast
   - Select users from the list and click **"🔒 Private"** for private messages
   - Click **"🗑️ Clear"** or press `Ctrl+L` to clear chat history

## 📁 Project Structure

```
Java-RMI-Chat-Application/
├── src/
│   └── main/
│       └── java/
│           ├── ClientSide/
│           │   ├── ChatClient.java          # Remote interface for client callbacks
│           │   ├── Client.java              # Client implementation with connection management
│           │   ├── ClientRMIGUI.java        # Modern GUI application
│           │   ├── ConnectionManager.java   # Connection retry logic
│           │   └── GUIConstants.java        # UI styling constants
│           └── ServerSide/
│               ├── Chattable.java           # Remote interface for server operations
│               ├── Chat.java                # Main server implementation
│               ├── Chatter.java              # User representation class
│               ├── ChatConstants.java        # Server configuration constants
│               └── MessageFormatter.java    # Message formatting utilities
├── pom.xml                                  # Maven configuration
├── .gitignore                               # Git ignore rules
└── README.md                                # This file
```

## 🎨 GUI Features

### Main Window Components

- **Status Bar**: Shows connection status (Connected/Disconnected) with color coding
- **Chat Area**: Displays all messages with timestamps
- **User List**: Shows online users with count display
- **Input Field**: Message input with Enter key support
- **Action Buttons**:
  - ▶ Start - Connect to server
  - 📤 Send - Broadcast message
  - 🔒 Private - Send private message
  - 🗑️ Clear - Clear chat history

### Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Enter` | Send message |
| `Ctrl+L` | Clear chat |

## 🔧 Configuration

### Server Configuration

Edit `ServerSide/ChatConstants.java` to modify:
- RMI Registry Port (default: 1099)
- Server Service Name (default: "Chat")
- Client Service Prefix

### Client Configuration

Edit `ClientSide/GUIConstants.java` to customize:
- Color scheme
- Fonts
- Window dimensions
- UI styling

## 🐛 Troubleshooting

### Connection Issues

- **"Failed to connect to server"**: Ensure the server is running before starting clients
- **"Connection refused"**: Check if port 1099 is available and not blocked by firewall
- **"Username already in use"**: Choose a different username

### Build Issues

- **Compilation errors**: Ensure Java 19+ is installed and Maven is properly configured
- **Class not found**: Run `mvn clean compile` to rebuild the project

## 📝 Code Quality

This project follows Java best practices:

- ✅ Comprehensive JavaDoc documentation
- ✅ Proper exception handling
- ✅ Thread-safe operations
- ✅ Input validation and sanitization
- ✅ Clean code architecture
- ✅ Separation of concerns
- ✅ Modern Java features

## 🔒 Security Features

- Username format validation
- Input sanitization to prevent injection
- Thread-safe client management
- Proper resource cleanup

## 📊 Performance

- Efficient message broadcasting
- Automatic cleanup of disconnected clients
- Optimized RMI communication
- Thread-safe collections

## 🤝 Contributing

This is a college project. For improvements or suggestions, please create an issue or pull request.

## 📄 License

This project is a college assignment project.

## 👨‍💻 Author

Refactored and improved for better code quality and user experience.

---

**Note**: Make sure to start the server before launching any clients. The server must be running for clients to connect successfully.
