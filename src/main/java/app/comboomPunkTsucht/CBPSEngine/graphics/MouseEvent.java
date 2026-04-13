package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Event fired when mouse moves or buttons are pressed/released.
 * action: GLFW_PRESS (1), GLFW_RELEASE (0) for buttons
 * button: GLFW_MOUSE_BUTTON_LEFT (0), GLFW_MOUSE_BUTTON_RIGHT (1), GLFW_MOUSE_BUTTON_MIDDLE (2)
 */
public record MouseEvent(long timestamp, double x, double y, int button, int action) implements WindowEvent {
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
