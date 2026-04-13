package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Event fired when keyboard key is pressed/released.
 * action: GLFW_PRESS (1), GLFW_RELEASE (0), GLFW_REPEAT (2)
 * mods: Bit flags for Shift, Ctrl, Alt, Super modifiers
 */
public record KeyEvent(long timestamp, int key, int scancode, int action, int mods) implements WindowEvent {
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
