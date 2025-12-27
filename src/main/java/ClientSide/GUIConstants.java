package ClientSide;

import java.awt.Color;
import java.awt.Font;

/**
 * Constants for GUI styling and configuration.
 * 
 * @author Refactored
 */
public final class GUIConstants {
    
    private GUIConstants() {
        // Utility class
    }
    
    // Color Scheme
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 250);
    public static final Color CHAT_BACKGROUND = Color.WHITE;
    public static final Color INPUT_BACKGROUND = Color.WHITE;
    public static final Color PANEL_BACKGROUND = new Color(250, 250, 255);
    public static final Color CONNECTED_COLOR = new Color(34, 139, 34); // Forest Green
    public static final Color DISCONNECTED_COLOR = new Color(220, 20, 60); // Crimson
    public static final Color BUTTON_COLOR = new Color(70, 130, 180); // Steel Blue
    public static final Color BUTTON_HOVER = new Color(100, 149, 237); // Cornflower Blue
    public static final Color TEXT_COLOR = new Color(30, 30, 30);
    public static final Color MESSAGE_COLOR = new Color(50, 50, 50);
    public static final Color SERVER_MESSAGE_COLOR = new Color(100, 100, 100);
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font STATUS_FONT = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font USER_LIST_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    
    // Dimensions
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int USER_PANEL_WIDTH = 200;
    public static final int STATUS_BAR_HEIGHT = 30;
}

