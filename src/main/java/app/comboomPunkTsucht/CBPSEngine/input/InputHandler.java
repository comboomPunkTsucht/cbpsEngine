package app.comboomPunkTsucht.CBPSEngine.input;

import app.comboomPunkTsucht.CBPSEngine.graphics.*;
import org.lwjgl.glfw.GLFW;

/**
 * Input handler: processes window events and updates input state.
 * Bridges Window-Events to KeyboardInput/MouseInput.
 */
public class InputHandler {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;
    private long glfwWindow = 0;

    public InputHandler() {
        this.keyboard = new KeyboardInput();
        this.mouse = new MouseInput();
    }

    /**
     * Set the GLFW window handle for polling mouse position.
     */
    public void setGlfwWindow(long window) {
        this.glfwWindow = window;
    }

    /**
     * Process a window event and update input state.
     */
    public void processEvent(WindowEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            handleKeyEvent(keyEvent);
        } else if (event instanceof MouseEvent mouseEvent) {
            handleMouseEvent(mouseEvent);
        }
    }

    private void handleKeyEvent(KeyEvent event) {
        // GLFW_PRESS = 1, GLFW_RELEASE = 0
        if (event.action() == 1) { // PRESS
            keyboard.onKeyPressed(event.key());
        } else if (event.action() == 0) { // RELEASE
            keyboard.onKeyReleased(event.key());
        }
    }

    private void handleMouseEvent(MouseEvent event) {
        mouse.setPosition(event.x(), event.y());

        // If button != -1, it's a button event
        if (event.button() != -1) {
            if (event.action() == 1) { // PRESS
                mouse.onButtonPressed(event.button());
            } else if (event.action() == 0) { // RELEASE
                mouse.onButtonReleased(event.button());
            }
        }
    }

    /**
     * Update input frame state (should be called once per frame).
     * Also polls mouse position directly from GLFW.
     */
    public void updateFrame() {
        keyboard.updateFrame();
        mouse.updateFrame();

        // Poll mouse position directly if window is set
        if (glfwWindow != 0) {
            double[] x = new double[1];
            double[] y = new double[1];
            GLFW.glfwGetCursorPos(glfwWindow, x, y);
            mouse.setPosition(x[0], y[0]);
        }
    }

    /**
     * Get keyboard input state.
     */
    public KeyboardInput getKeyboard() {
        return keyboard;
    }

    /**
     * Get mouse input state.
     */
    public MouseInput getMouse() {
        return mouse;
    }
}
