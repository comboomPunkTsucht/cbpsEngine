package app.comboomPunkTsucht.CBPSEngine.input;

/**
 * Mouse input state tracking.
 * Tracks position and button presses.
 */
public class MouseInput {
    private double x = 0;
    private double y = 0;
    private double lastX = 0;
    private double lastY = 0;
    
    private final boolean[] buttonPressed = new boolean[8]; // Max 8 buttons
    private final boolean[] buttonDown = new boolean[8];

    /**
     * Called at end of frame to update state.
     */
    public void updateFrame() {
        lastX = x;
        lastY = y;
        System.arraycopy(buttonPressed, 0, buttonDown, 0, 8);
    }

    /**
     * Update mouse position.
     */
    void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Notify that button was pressed.
     */
    void onButtonPressed(int button) {
        if (button >= 0 && button < 8) {
            buttonPressed[button] = true;
        }
    }

    /**
     * Notify that button was released.
     */
    void onButtonReleased(int button) {
        if (button >= 0 && button < 8) {
            buttonPressed[button] = false;
        }
    }

    /**
     * Get current mouse X position.
     */
    public double getX() {
        return x;
    }

    /**
     * Get current mouse Y position.
     */
    public double getY() {
        return y;
    }

    /**
     * Get mouse delta X (movement this frame).
     */
    public double getDeltaX() {
        return x - lastX;
    }

    /**
     * Get mouse delta Y (movement this frame).
     */
    public double getDeltaY() {
        return y - lastY;
    }

    /**
     * Check if button is currently held.
     */
    public boolean isButtonDown(int button) {
        return button >= 0 && button < 8 && buttonPressed[button];
    }

    /**
     * Check if button was just pressed this frame.
     */
    public boolean isButtonPressed(int button) {
        return button >= 0 && button < 8 && buttonPressed[button] && !buttonDown[button];
    }

    /**
     * Check if button was just released this frame.
     */
    public boolean isButtonReleased(int button) {
        return button >= 0 && button < 8 && !buttonPressed[button] && buttonDown[button];
    }
}
