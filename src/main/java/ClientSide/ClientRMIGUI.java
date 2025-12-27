package ClientSide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.util.logging.Logger;
import ServerSide.MessageFormatter;

/**
 * Enhanced GUI for the chat client application.
 * Provides a modern user interface with improved UX features.
 * 
 * @author Refactored
 */
public class ClientRMIGUI extends JFrame implements ActionListener, KeyListener {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ClientRMIGUI.class.getName());
    
    // UI Constants
    private static final String WINDOW_TITLE = "Java RMI Chat Application";
    private static final String WELCOME_MESSAGE = "╔═══════════════════════════════════════╗\n" +
                                                  "║   Welcome to RMI Chat Application   ║\n" +
                                                  "║   Enter your name and press Start    ║\n" +
                                                  "╚═══════════════════════════════════════╝\n\n";
    private static final String NO_USERS_MESSAGE = "No other users";
    private static final String USER_LIST_LABEL = "👥 Online Users";
    
    // UI Components
    private JPanel textPanel;
    private JPanel inputPanel;
    private JPanel userPanel;
    private JPanel clientPanel;
    private JTextField messageTextField;
    private JTextArea chatTextArea;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton sendButton;
    private JButton startButton;
    private JButton privateMessageButton;
    private JButton clearChatButton;
    private JLabel userCountLabel;
    
    // Application state
    private String username;
    private Client chatClient;
    private boolean isConnected = false;
    private JLabel statusLabel;
    private int messageCount = 0;
    
    /**
     * Main entry point for the client application.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            Logger.getLogger(ClientRMIGUI.class.getName())
                  .warning("Could not set system look and feel: " + e.getMessage());
        }
        
        // Create and show GUI on EDT
        javax.swing.SwingUtilities.invokeLater(() -> {
            new ClientRMIGUI();
        });
    }
    
    /**
     * Constructs and initializes the chat GUI.
     */
    public ClientRMIGUI() {
        initializeGUI();
    }
    
    /**
     * Initializes all GUI components and sets up the window.
     */
    private void initializeGUI() {
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(GUIConstants.WINDOW_WIDTH, GUIConstants.WINDOW_HEIGHT);
        
        // Handle window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                handleWindowClosing();
            }
        });
        
        // Create main container
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(GUIConstants.BACKGROUND_COLOR);
        
        // Add panels
        contentPane.add(createStatusPanel(), BorderLayout.NORTH);
        contentPane.add(createTextPanel(), BorderLayout.CENTER);
        contentPane.add(createInputPanel(), BorderLayout.SOUTH);
        contentPane.add(createUserPanel(), BorderLayout.WEST);
        
        // Add keyboard shortcuts
        setupKeyboardShortcuts();
        
        // Configure window
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);
        setVisible(true);
        
        messageTextField.requestFocus();
        
        LOGGER.info("Chat GUI initialized");
    }
    
    /**
     * Sets up keyboard shortcuts.
     */
    private void setupKeyboardShortcuts() {
        // Enter to send message
        messageTextField.addKeyListener(this);
        
        // Ctrl+L to clear chat
        getRootPane().getInputMap(javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_DOWN_MASK), "clearChat");
        getRootPane().getActionMap().put("clearChat", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearChat();
            }
        });
    }
    
    /**
     * Creates the text panel for displaying chat messages.
     * 
     * @return the configured text panel
     */
    private JPanel createTextPanel() {
        chatTextArea = new JTextArea(WELCOME_MESSAGE);
        chatTextArea.setMargin(new Insets(10, 10, 10, 10));
        chatTextArea.setFont(GUIConstants.DEFAULT_FONT);
        chatTextArea.setLineWrap(true);
        chatTextArea.setWrapStyleWord(true);
        chatTextArea.setEditable(false);
        chatTextArea.setBackground(GUIConstants.CHAT_BACKGROUND);
        chatTextArea.setForeground(GUIConstants.MESSAGE_COLOR);
        chatTextArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JScrollPane scrollPane = new JScrollPane(chatTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        
        textPanel = new JPanel(new BorderLayout());
        textPanel.add(scrollPane, BorderLayout.CENTER);
        textPanel.setBackground(GUIConstants.BACKGROUND_COLOR);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        return textPanel;
    }
    
    /**
     * Creates the input panel for typing messages.
     * 
     * @return the configured input panel
     */
    private JPanel createInputPanel() {
        messageTextField = new JTextField();
        messageTextField.setFont(GUIConstants.DEFAULT_FONT);
        messageTextField.setBackground(GUIConstants.INPUT_BACKGROUND);
        messageTextField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        messageTextField.addActionListener(e -> {
            if (sendButton.isEnabled() && isConnected) {
                try {
                    handleSendButton();
                } catch (RemoteException ex) {
                    LOGGER.log(java.util.logging.Level.SEVERE, "Error sending message", ex);
                    showErrorMessage("Send Error", "Failed to send message: " + ex.getMessage());
                }
            }
        });
        
        inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageTextField, BorderLayout.CENTER);
        inputPanel.setBackground(GUIConstants.BACKGROUND_COLOR);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        return inputPanel;
    }
    
    /**
     * Creates the user panel showing connected users.
     * 
     * @return the configured user panel
     */
    private JPanel createUserPanel() {
        userPanel = new JPanel(new BorderLayout());
        
        // User list label with count
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel userLabel = new JLabel(USER_LIST_LABEL, JLabel.CENTER);
        userLabel.setFont(GUIConstants.TITLE_FONT);
        userLabel.setForeground(GUIConstants.TEXT_COLOR);
        
        userCountLabel = new JLabel("(0)", JLabel.CENTER);
        userCountLabel.setFont(new Font(GUIConstants.DEFAULT_FONT.getName(), Font.PLAIN, 11));
        userCountLabel.setForeground(new Color(100, 100, 100));
        
        headerPanel.add(userLabel, BorderLayout.CENTER);
        headerPanel.add(userCountLabel, BorderLayout.SOUTH);
        headerPanel.setBackground(GUIConstants.PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        userPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Initialize user list
        String[] noUsers = {NO_USERS_MESSAGE};
        updateClientPanel(noUsers);
        
        // Button panel
        userPanel.add(createButtonPanel(), BorderLayout.SOUTH);
        userPanel.setBackground(GUIConstants.PANEL_BACKGROUND);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        return userPanel;
    }
    
    /**
     * Creates the status panel showing connection status.
     * 
     * @return the configured status panel
     */
    private JPanel createStatusPanel() {
        statusLabel = new JLabel("Status: Not Connected", JLabel.CENTER);
        statusLabel.setFont(GUIConstants.STATUS_FONT);
        statusLabel.setForeground(GUIConstants.DISCONNECTED_COLOR);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.setBackground(new Color(250, 250, 250));
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        
        return statusPanel;
    }
    
    /**
     * Creates the button panel with action buttons.
     * 
     * @return the configured button panel
     */
    private JPanel createButtonPanel() {
        // Start button
        startButton = createStyledButton("▶ Start", GUIConstants.BUTTON_COLOR);
        startButton.addActionListener(this);
        
        // Send button
        sendButton = createStyledButton("📤 Send", GUIConstants.BUTTON_COLOR);
        sendButton.addActionListener(this);
        sendButton.setEnabled(false);
        getRootPane().setDefaultButton(sendButton);
        
        // Private message button
        privateMessageButton = createStyledButton("🔒 Private", GUIConstants.BUTTON_COLOR);
        privateMessageButton.addActionListener(this);
        privateMessageButton.setEnabled(false);
        
        // Clear chat button
        clearChatButton = createStyledButton("🗑️ Clear", new Color(180, 180, 180));
        clearChatButton.addActionListener(e -> clearChat());
        clearChatButton.setToolTipText("Clear chat (Ctrl+L)");
        
        // Layout buttons
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 8, 8));
        buttonPanel.add(startButton);
        buttonPanel.add(sendButton);
        buttonPanel.add(privateMessageButton);
        buttonPanel.add(clearChatButton);
        buttonPanel.setBackground(GUIConstants.PANEL_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        return buttonPanel;
    }
    
    /**
     * Creates a styled button with consistent appearance.
     * 
     * @param text button text
     * @param color button color
     * @return styled button
     */
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(GUIConstants.BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    /**
     * Updates the client panel with the current user list.
     * 
     * @param currentUsers array of current usernames
     */
    private void updateClientPanel(String[] currentUsers) {
        if (userPanel == null) {
            return;
        }
        
        // Remove old panel if exists
        if (clientPanel != null) {
            userPanel.remove(clientPanel);
        }
        
        // Create new panel
        clientPanel = new JPanel(new BorderLayout());
        userListModel = new DefaultListModel<>();
        
        int actualUserCount = 0;
        for (String user : currentUsers) {
            if (user != null && !user.isEmpty() && !user.equals(NO_USERS_MESSAGE)) {
                userListModel.addElement(user);
                actualUserCount++;
            }
        }
        
        // Update user count
        updateUserCount(actualUserCount);
        
        // Enable private message button if enough users
        boolean canSendPrivate = actualUserCount >= 1 && isConnected;
        privateMessageButton.setEnabled(canSendPrivate);
        
        // Create list
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setVisibleRowCount(10);
        userList.setFont(GUIConstants.USER_LIST_FONT);
        userList.setBackground(GUIConstants.CHAT_BACKGROUND);
        userList.setSelectionBackground(GUIConstants.BUTTON_COLOR);
        userList.setSelectionForeground(Color.WHITE);
        
        JScrollPane listScrollPane = new JScrollPane(userList);
        listScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        clientPanel.add(listScrollPane, BorderLayout.CENTER);
        clientPanel.setBackground(GUIConstants.PANEL_BACKGROUND);
        
        userPanel.add(clientPanel, BorderLayout.CENTER);
        userPanel.revalidate();
        userPanel.repaint();
    }
    
    /**
     * Updates the user count display.
     * 
     * @param count number of users
     */
    private void updateUserCount(int count) {
        if (userCountLabel != null) {
            userCountLabel.setText("(" + count + " online)");
        }
    }
    
    /**
     * Clears the chat text area.
     */
    private void clearChat() {
        if (chatTextArea != null) {
            chatTextArea.setText("");
            messageCount = 0;
            appendMessage(WELCOME_MESSAGE);
        }
    }
    
    /**
     * Handles action events from buttons.
     * 
     * @param e the action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        try {
            if (source == startButton) {
                handleStartButton();
            } else if (source == sendButton) {
                handleSendButton();
            } else if (source == privateMessageButton) {
                handlePrivateMessageButton();
            }
        } catch (RemoteException ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Error handling action", ex);
            showErrorMessage("Communication Error", 
                "Failed to communicate with server: " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Unexpected error handling action", ex);
            showErrorMessage("Error", "An unexpected error occurred: " + ex.getMessage());
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        // Enter key to send (already handled by action listener)
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }
    
    /**
     * Handles the Start button click.
     */
    private void handleStartButton() {
        String inputName = messageTextField.getText().trim();
        
        if (inputName.isEmpty()) {
            showErrorMessage("Invalid Input", "Please enter your name to start");
            return;
        }
        
        // Validate username using MessageFormatter
        if (!MessageFormatter.isValidUsername(inputName)) {
            showErrorMessage("Invalid Username", 
                "Username must be 3-20 characters and contain only letters, numbers, underscores, and hyphens.");
            return;
        }
        
        username = inputName;
        setTitle(username + " - " + WINDOW_TITLE);
        messageTextField.setText("");
        
        // Connect to server
        connectToServer(username);
        
        startButton.setEnabled(false);
        sendButton.setEnabled(true);
    }
    
    /**
     * Handles the Send button click.
     * 
     * @throws RemoteException if sending fails
     */
    private void handleSendButton() throws RemoteException {
        if (!isConnected || chatClient == null || chatClient.getChatService() == null) {
            showErrorMessage("Not Connected", "You are not connected to the server");
            return;
        }
        
        String message = messageTextField.getText().trim();
        
        if (message.isEmpty()) {
            return; // Don't send empty messages
        }
        
        if (message.length() > 1000) {
            showErrorMessage("Message Too Long", "Message must be 1000 characters or less");
            return;
        }
        
        messageTextField.setText("");
        sendMessage(message);
        messageCount++;
    }
    
    /**
     * Handles the Private Message button click.
     * 
     * @throws RemoteException if sending fails
     */
    private void handlePrivateMessageButton() throws RemoteException {
        if (!isConnected || chatClient == null || chatClient.getChatService() == null) {
            showErrorMessage("Not Connected", "You are not connected to the server");
            return;
        }
        
        int[] selectedIndices = userList.getSelectedIndices();
        
        if (selectedIndices.length == 0) {
            showErrorMessage("No Selection", "Please select at least one user for private message");
            return;
        }
        
        String message = messageTextField.getText().trim();
        
        if (message.isEmpty()) {
            showErrorMessage("Empty Message", "Please enter a message");
            return;
        }
        
        messageTextField.setText("");
        sendPrivateMessage(selectedIndices, message);
    }
    
    /**
     * Connects to the chat server.
     * 
     * @param userName the username to use
     */
    private void connectToServer(String userName) {
        try {
            chatClient = new Client(userName, this);
            Thread clientThread = new Thread(chatClient);
            clientThread.setDaemon(true);
            clientThread.start();
            
            LOGGER.info("Connection thread started for user: " + userName);
        } catch (RemoteException e) {
            LOGGER.log(java.util.logging.Level.SEVERE, "Failed to create client", e);
            showErrorMessage("Connection Error", 
                "Failed to initialize client: " + e.getMessage());
        }
    }
    
    /**
     * Sends a message to all users.
     * 
     * @param message the message to send
     * @throws RemoteException if sending fails
     */
    private void sendMessage(String message) throws RemoteException {
        if (chatClient != null && chatClient.getChatService() != null) {
            chatClient.getChatService().updateChat(username, message);
            LOGGER.fine("Sent message: " + message);
        }
    }
    
    /**
     * Sends a private message to selected users.
     * 
     * @param recipientIndices indices of recipients in the user list
     * @param message the message to send
     * @throws RemoteException if sending fails
     */
    private void sendPrivateMessage(int[] recipientIndices, String message) throws RemoteException {
        if (chatClient != null && chatClient.getChatService() != null) {
            // Format private message with timestamp
            String privateMessage = MessageFormatter.formatPrivateMessage(username, message);
            chatClient.getChatService().sendPM(recipientIndices, privateMessage);
            LOGGER.fine("Sent private message to " + recipientIndices.length + " user(s)");
        }
    }
    
    /**
     * Handles window closing event.
     */
    private void handleWindowClosing() {
        if (isConnected && chatClient != null) {
            try {
                sendMessage("Has Left The Chat");
                if (chatClient.getChatService() != null) {
                    chatClient.getChatService().leaveChat(username);
                }
                chatClient.disconnect();
            } catch (RemoteException e) {
                LOGGER.log(java.util.logging.Level.WARNING, "Error during disconnect", e);
            }
        }
        System.exit(0);
    }
    
    /**
     * Appends a message to the chat text area.
     * Thread-safe method that can be called from any thread.
     * 
     * @param message the message to append
     */
    public void appendMessage(String message) {
        if (message == null) {
            return;
        }
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            if (chatTextArea != null) {
                chatTextArea.append(message);
                // Auto-scroll to bottom
                chatTextArea.setCaretPosition(chatTextArea.getDocument().getLength());
            }
        });
    }
    
    /**
     * Updates the user list display.
     * Thread-safe method that can be called from any thread.
     * 
     * @param currentUsers array of current usernames
     */
    public void updateUserList(String[] currentUsers) {
        if (currentUsers == null) {
            return;
        }
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            updateClientPanel(currentUsers);
        });
    }
    
    /**
     * Shows an error message dialog.
     * 
     * @param title the dialog title
     * @param message the error message
     */
    public void showErrorMessage(String title, String message) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        });
    }
    
    /**
     * Sets the connected state.
     * 
     * @param connected true if connected
     */
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }
    
    /**
     * Updates the connection status display.
     * 
     * @param statusText the status text to display
     * @param isConnected true if connected, false otherwise
     */
    public void updateConnectionStatus(String statusText, boolean isConnected) {
        if (statusLabel != null) {
            javax.swing.SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Status: " + statusText);
                statusLabel.setForeground(isConnected ? GUIConstants.CONNECTED_COLOR : GUIConstants.DISCONNECTED_COLOR);
            });
        }
    }
}
